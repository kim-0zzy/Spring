package hello.upload.controller;

import lombok.Data;
import lombok.extern.java.Log;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
public class ItemForm {
    // 상품 저장용 폼
    private Long itemId;
    private String itemName;
    private MultipartFile attachFile; // 단일 업로드용
    private List<MultipartFile> imageFiles; // 다중 업로드용
}
