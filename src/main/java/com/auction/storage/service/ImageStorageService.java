
package com.auction.storage.service;

import java.util.UUID;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import com.auction.storage.enums.ImageCategory;

public interface ImageStorageService {

    // ==========================================
    // UPLOAD IMAGE
    // ==========================================

    String uploadImage(
            MultipartFile file,
            ImageCategory category,
            UUID entityId
    );

    // ==========================================
    // LOAD IMAGE AS RESOURCE
    // ==========================================

    Resource loadImage(String imagePath);

    // ==========================================
    // LOAD IMAGE AS BYTE ARRAY
    // ==========================================

    byte[] loadImageAsBytes(String imagePath);

    // ==========================================
    // CHECK IMAGE EXISTS
    // ==========================================

    boolean imageExists(String imagePath);

    // ==========================================
    // DELETE IMAGE
    // ==========================================

    void deleteImage(String imagePath);
}
