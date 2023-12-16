package hello.upload.controller;


import hello.upload.domain.Item;
import hello.upload.domain.ItemRepository;
import hello.upload.domain.UploadFile;
import hello.upload.file.FileStore;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.util.UriUtils;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Slf4j
@Controller
@RequiredArgsConstructor
public class ItemController {

    private final ItemRepository itemRepository;
    private final FileStore fileStore;

    @GetMapping("/items/new")
    public String newItem(@ModelAttribute ItemForm form) {
        return "item-form";
    }

    @PostMapping("/items/new")
    public String saveItem(@ModelAttribute ItemForm form, RedirectAttributes redirectAttributes) throws IOException {
        UploadFile attachFile = fileStore.storeFile(form.getAttachFile());
        List<UploadFile> storeImageFiles = fileStore.storeFiles(form.getImageFiles());

        // DB에는 파일의 경로만 저장. 파일은 DB에 저장하지 않고 스토리지에 저장함.(AWS의 경우 S3)
        Item item = new Item();
        item.setItemName(form.getItemName());
        item.setAttachFile(attachFile);
        item.setImageFiles(storeImageFiles);
        itemRepository.save(item);

        redirectAttributes.addAttribute("itemId", item.getId());

        return "redirect:/items/{itemId}";

    }

    @GetMapping("/items/{id}")
    public String items(@PathVariable(name = "id") Long id, Model model) {
        Item item = itemRepository.findById(id);
        model.addAttribute("item", item);
        return "item-view";
    }

    @ResponseBody
    @GetMapping("/images/{filename}")
    public Resource downloadImage(@PathVariable(name = "filename") String filename) throws MalformedURLException {
        return new UrlResource("file:" + fileStore.getFullPath(filename)); // file:/C/Users/~~/파일명.확장자
    }

    @GetMapping("/attach/{itemId)")
    public ResponseEntity<Resource> downloadAttach(@PathVariable(name = "itemId") Long itemId) throws MalformedURLException {
        Item item = itemRepository.findById(itemId);
        String storeFileName = item.getAttachFile().getStoreFileName();
        String uploadFileName = item.getAttachFile().getUploadFileName();

        UrlResource Resource = new UrlResource("file:" + fileStore.getFullPath(storeFileName));

        log.info("uploadFileName = {}", uploadFileName);

        String encodeUploadFileName = UriUtils.encode(uploadFileName, StandardCharsets.UTF_8); // 브라우저에 따라 이거 안넣으면 한글 깨질 수 있음
        String contentDisposition = "attachment; filename=\"" + encodeUploadFileName + "\""; // 이걸 넣어줘야 파일 다운받을 수 있음
                                                                                        // 안넣으면 브라우저에서 파일 내용이 열려버림
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, contentDisposition)
                .body(Resource);
    }

}
