
package com.auction.storage.controller;

import java.util.Arrays;
import java.util.UUID;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.http.CacheControl;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.auction.exception.ResourceNotFoundException;
import com.auction.storage.enums.ImageCategory;
import com.auction.storage.service.ImageStorageService;

@RestController
@RequestMapping("/api/v1/images")
public class ImageController {

    private static final Logger log =
            LoggerFactory.getLogger(ImageController.class);

    private static final Pattern IMAGE_FILENAME_PATTERN =
            Pattern.compile(
                    "^[0-9a-f]{8}-"
                    + "[0-9a-f]{4}-"
                    + "[0-9a-f]{4}-"
                    + "[0-9a-f]{4}-"
                    + "[0-9a-f]{12}\\.(png|jpg)$");

    private final ImageStorageService imageStorageService;

    public ImageController(
            ImageStorageService imageStorageService) {

        this.imageStorageService = imageStorageService;
    }

    // ==========================================
    // RETRIEVE IMAGE
    // ==========================================

    @GetMapping("/{category}/{entityId}/{filename}")
    public ResponseEntity<Resource> getImage(
            @PathVariable String category,
            @PathVariable UUID entityId,
            @PathVariable String filename) {

        // 1. Validate image category
        ImageCategory imageCategory =
                resolveImageCategory(category);

        // 2. Validate generated image filename
        validateFilename(filename);

        // 3. Build relative image storage path
        String imagePath =
                imageCategory.getDirectoryName()
                        + "/"
                        + entityId
                        + "/"
                        + filename;

        log.debug(
                "Retrieving image: {}",
                imagePath);

        // 4. Load image using reusable storage service
        Resource imageResource;

        try {

            imageResource =
                    imageStorageService.loadImage(imagePath);

        } catch (IllegalArgumentException ex) {

            throw new ResourceNotFoundException(
                    "Image not found");
        }

        // 5. Resolve image content type
        MediaType mediaType =
                resolveMediaType(filename);

        // 6. Return image response
        return ResponseEntity.ok()
                .contentType(mediaType)
                .cacheControl(CacheControl.noStore())
                .header(
                        "X-Content-Type-Options",
                        "nosniff")
                .body(imageResource);
    }

    // ==========================================
    // RESOLVE IMAGE CATEGORY
    // ==========================================

    private ImageCategory resolveImageCategory(
            String category) {

        return Arrays.stream(ImageCategory.values())
                .filter(imageCategory ->
                        imageCategory.getDirectoryName()
                                .equals(category))
                .findFirst()
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Image category not found"));
    }

    // ==========================================
    // VALIDATE IMAGE FILENAME
    // ==========================================

    private void validateFilename(String filename) {

        if (filename == null
                || !IMAGE_FILENAME_PATTERN
                        .matcher(filename)
                        .matches()) {

            throw new ResourceNotFoundException(
                    "Image not found");
        }
    }

    // ==========================================
    // RESOLVE IMAGE CONTENT TYPE
    // ==========================================

    private MediaType resolveMediaType(
            String filename) {

        if (filename.endsWith(".png")) {

            return MediaType.IMAGE_PNG;
        }

        if (filename.endsWith(".jpg")) {

            return MediaType.IMAGE_JPEG;
        }

        throw new ResourceNotFoundException(
                "Unsupported image format");
    }
}
