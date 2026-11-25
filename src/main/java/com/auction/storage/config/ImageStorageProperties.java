
package com.auction.storage.config;

import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Component
@ConfigurationProperties(prefix = "app.storage")
public class ImageStorageProperties {

    // ==========================================
    // ROOT IMAGE STORAGE DIRECTORY
    // ==========================================

    private String uploadDir = "./uploads";

    // ==========================================
    // MAXIMUM IMAGE FILE SIZE IN BYTES
    // ==========================================

    private long maxFileSize = 2097152L;

    // ==========================================
    // ALLOWED IMAGE CONTENT TYPES
    // ==========================================

    private List<String> allowedContentTypes =
            List.of(
                    "image/png",
                    "image/jpeg"
            );

    public String getUploadDir() {
        return uploadDir;
    }

    public void setUploadDir(String uploadDir) {
        this.uploadDir = uploadDir;
    }

    public long getMaxFileSize() {
        return maxFileSize;
    }

    public void setMaxFileSize(long maxFileSize) {
        this.maxFileSize = maxFileSize;
    }

    public List<String> getAllowedContentTypes() {
        return allowedContentTypes;
    }

    public void setAllowedContentTypes(
            List<String> allowedContentTypes) {

        this.allowedContentTypes = allowedContentTypes;
    }
}
