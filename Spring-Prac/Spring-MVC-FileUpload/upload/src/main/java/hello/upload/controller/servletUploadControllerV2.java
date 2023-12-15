package hello.upload.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.Part;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.util.StreamUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Collection;


@Slf4j
@Controller
@RequestMapping("/servlet/v2")
public class servletUploadControllerV2 {

    @Value("${file.dir}")
    private String fileDir; // <-- application.properties

    @GetMapping("/upload")
    public String newFile() {
        return "/upload-form";
    }

    @PostMapping("/upload")
    public String saveFileV1(HttpServletRequest request) throws ServletException, IOException {
        log.info("request= {}", request);

        String itemName = request.getParameter("itemName");
        log.info("itemName={}", itemName);

        // ↓ multiPart/form-data의 각 구분된 파트 별 데이터를 받아볼 수 있는 것.
        Collection<Part> parts = request.getParts();
        log.info("parts={}", parts);

        for (Part part : parts) {
            log.info("=== PART ===");
            log.info("name={}", part.getName());
            Collection<String> headerNames = part.getHeaderNames();
            for (String headerName : headerNames) {
                log.info("header {} : {}", headerName, part.getHeader(headerName));
            }
            // content-disposition; fileName 자동 파싱해주는 편의 메서드
            log.info("submittedFilename={}", part.getSubmittedFileName());
            log.info("size={}", part.getSize());

            // read Data
            InputStream inputStream = part.getInputStream();
            // ↓↓↓↓ binary file을 읽을 때는 항상 CharSet을 정해줘야한다.
            String body = StreamUtils.copyToString(inputStream, StandardCharsets.UTF_8);
            log.info("body ={}", body);


            //파일에 저장하기
            if (StringUtils.hasText(part.getSubmittedFileName())) {
                String fullPath = fileDir + part.getSubmittedFileName();
                log.info("파일 저장 fullPath={}", fullPath);
                part.write(fullPath);
            }
        }

        return "/upload-form";
    }
}
