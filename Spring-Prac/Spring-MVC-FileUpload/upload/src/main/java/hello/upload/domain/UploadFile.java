package hello.upload.domain;

import lombok.Data;

@Data
public class UploadFile {
    // FileName이 두 가지인 이유 : 사용자들이 같은 파일명으로 업로드 할 경우, 덮어쓰기가 되기 때문.
    private String uploadFileName;
    private String storeFileName;

    public UploadFile(String uploadFileName, String storeFileName) {
        this.uploadFileName = uploadFileName;
        this.storeFileName = storeFileName;
    }
}
