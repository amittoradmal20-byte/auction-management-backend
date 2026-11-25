
package com.auction.storage.service.impl;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Iterator;
import java.util.Locale;
import java.util.UUID;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.auction.storage.config.ImageStorageProperties;
import com.auction.storage.enums.ImageCategory;
import com.auction.storage.service.ImageStorageService;

@Service
public class ImageStorageServiceImpl
        implements ImageStorageService {

    private static final Logger log =
            LoggerFactory.getLogger(
                    ImageStorageServiceImpl.class
            );

    private static final int MAX_IMAGE_WIDTH = 4096;

    private static final int MAX_IMAGE_HEIGHT = 4096;

    private static final String UUID_PATTERN =
            "[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-"
            + "[0-9a-f]{4}-[0-9a-f]{12}";

    private static final Pattern IMAGE_PATH_PATTERN =
            Pattern.compile(
                    "^(tournaments|teams|players|sponsors)/"
                    + UUID_PATTERN
                    + "/"
                    + UUID_PATTERN
                    + "\\.(png|jpg)$"
            );

    private final ImageStorageProperties storageProperties;

    private final Path rootStoragePath;

    // ==========================================
    // CONSTRUCTOR AND STORAGE INITIALIZATION
    // ==========================================

    public ImageStorageServiceImpl(
            ImageStorageProperties storageProperties) {

        this.storageProperties = storageProperties;

        validateStorageConfiguration();

        this.rootStoragePath = Paths.get(
                storageProperties.getUploadDir()
        ).toAbsolutePath().normalize();

        initializeStorageDirectories();
    }

    // ==========================================
    // VALIDATE STORAGE CONFIGURATION
    // ==========================================

    private void validateStorageConfiguration() {

        if (storageProperties.getUploadDir() == null
                || storageProperties.getUploadDir().isBlank()) {

            throw new IllegalStateException(
                    "Image storage directory must be configured."
            );
        }

        if (storageProperties.getMaxFileSize() <= 0) {

            throw new IllegalStateException(
                    "Maximum image file size must be positive."
            );
        }

        if (storageProperties.getAllowedContentTypes() == null
                || storageProperties
                        .getAllowedContentTypes().isEmpty()) {

            throw new IllegalStateException(
                    "Allowed image content types must be configured."
            );
        }
    }

    // ==========================================
    // INITIALIZE STORAGE DIRECTORIES
    // ==========================================

    private void initializeStorageDirectories() {

        try {

            validateNoSymbolicLinks(rootStoragePath);

            Files.createDirectories(rootStoragePath);

            for (ImageCategory category
                    : ImageCategory.values()) {

                Path categoryPath = rootStoragePath.resolve(
                        category.getDirectoryName()
                );

                validateNoSymbolicLinks(categoryPath);

                Files.createDirectories(categoryPath);
            }

            log.info(
                    "Image storage initialized successfully. Path={}",
                    rootStoragePath
            );

        } catch (IOException ex) {

            log.error(
                    "Failed to initialize image storage directories.",
                    ex
            );

            throw new IllegalStateException(
                    "Unable to initialize image storage.",
                    ex
            );
        }
    }

    // ==========================================
    // UPLOAD IMAGE
    // ==========================================

    @Override
    public String uploadImage(
            MultipartFile file,
            ImageCategory category,
            UUID entityId) {

        validateUploadRequest(file, category, entityId);

        Path targetPath = null;

        boolean fileCreated = false;

        try {

            byte[] imageBytes;

            try (InputStream inputStream =
                         file.getInputStream()) {

                imageBytes = readLimitedBytes(inputStream);
            }

            String extension = validateImageContent(
                    imageBytes,
                    file.getContentType()
            );

            String fileName =
                    UUID.randomUUID() + "." + extension;

            String relativePath =
                    category.getDirectoryName()
                    + "/"
                    + entityId
                    + "/"
                    + fileName;

            targetPath = resolveImagePath(relativePath);

            Files.createDirectories(targetPath.getParent());

            validateNoSymbolicLinks(targetPath);

            try (var outputStream = Files.newOutputStream(
                    targetPath,
                    StandardOpenOption.CREATE_NEW,
                    StandardOpenOption.WRITE,
                    LinkOption.NOFOLLOW_LINKS)) {

                fileCreated = true;

                outputStream.write(imageBytes);
            }

            log.info(
                    "Image uploaded successfully. "
                    + "Category={}, EntityId={}, ImagePath={}",
                    category,
                    entityId,
                    relativePath
            );

            return relativePath;

        } catch (IOException ex) {

            if (fileCreated && targetPath != null) {

                try {

                    Files.deleteIfExists(targetPath);

                } catch (IOException cleanupException) {

                    log.warn(
                            "Unable to clean up failed image upload.",
                            cleanupException
                    );
                }
            }

            log.error(
                    "Image upload failed. Category={}, EntityId={}",
                    category,
                    entityId,
                    ex
            );

            throw new IllegalStateException(
                    "Unable to upload image.",
                    ex
            );
        }
    }

    // ==========================================
    // VALIDATE UPLOAD REQUEST
    // ==========================================

    private void validateUploadRequest(
            MultipartFile file,
            ImageCategory category,
            UUID entityId) {

        if (category == null) {

            throw new IllegalArgumentException(
                    "Image category is required."
            );
        }

        if (entityId == null) {

            throw new IllegalArgumentException(
                    "Entity ID is required."
            );
        }

        if (file == null || file.isEmpty()) {

            throw new IllegalArgumentException(
                    "Image file is required."
            );
        }

        if (file.getSize()
                > storageProperties.getMaxFileSize()) {

            throw new IllegalArgumentException(
                    "Image file exceeds the maximum allowed size."
            );
        }

        String contentType = file.getContentType();

        if (contentType == null
                || !storageProperties
                        .getAllowedContentTypes()
                        .contains(
                                contentType.toLowerCase(
                                        Locale.ROOT
                                )
                        )) {

            throw new IllegalArgumentException(
                    "Only PNG and JPEG images are supported."
            );
        }
    }

    // ==========================================
    // VALIDATE ACTUAL IMAGE CONTENT
    // ==========================================

    private String validateImageContent(
            byte[] imageBytes,
            String declaredContentType) {

        try (ImageInputStream imageInputStream =
                     ImageIO.createImageInputStream(
                             new ByteArrayInputStream(imageBytes)
                     )) {

            if (imageInputStream == null) {

                throw new IllegalArgumentException(
                        "Invalid image file."
                );
            }

            Iterator<ImageReader> readers =
                    ImageIO.getImageReaders(imageInputStream);

            if (!readers.hasNext()) {

                throw new IllegalArgumentException(
                        "Uploaded file is not a valid image."
                );
            }

            ImageReader reader = readers.next();

            try {

                reader.setInput(
                        imageInputStream,
                        true,
                        true
                );

                String format = reader.getFormatName()
                        .toLowerCase(Locale.ROOT);

                String extension;

                String actualContentType;

                switch (format) {

                    case "png":

                        extension = "png";
                        actualContentType = "image/png";
                        break;

                    case "jpeg":
                    case "jpg":

                        extension = "jpg";
                        actualContentType = "image/jpeg";
                        break;

                    default:

                        throw new IllegalArgumentException(
                                "Unsupported image format."
                        );
                }

                if (!actualContentType.equalsIgnoreCase(
                        declaredContentType)) {

                    throw new IllegalArgumentException(
                            "Image content does not match "
                            + "the declared content type."
                    );
                }

                if (!storageProperties
                        .getAllowedContentTypes()
                        .contains(actualContentType)) {

                    throw new IllegalArgumentException(
                            "Image content type is not allowed."
                    );
                }

                int width = reader.getWidth(0);

                int height = reader.getHeight(0);

                if (width <= 0
                        || height <= 0
                        || width > MAX_IMAGE_WIDTH
                        || height > MAX_IMAGE_HEIGHT) {

                    throw new IllegalArgumentException(
                            "Image dimensions must not exceed "
                            + MAX_IMAGE_WIDTH
                            + " x "
                            + MAX_IMAGE_HEIGHT
                            + " pixels."
                    );
                }

                BufferedImage image = reader.read(0);

                if (image == null) {

                    throw new IllegalArgumentException(
                            "Unable to decode uploaded image."
                    );
                }

                image.flush();

                return extension;

            } finally {

                reader.dispose();
            }

        } catch (IOException ex) {

            throw new IllegalArgumentException(
                    "Invalid or corrupted image file.",
                    ex
            );
        }
    }

    // ==========================================
    // LOAD IMAGE AS RESOURCE
    // ==========================================

    @Override
    public Resource loadImage(String imagePath) {

        byte[] imageBytes = loadImageAsBytes(imagePath);

        String fileName = Paths.get(imagePath)
                .getFileName()
                .toString();

        return new ByteArrayResource(imageBytes) {

            @Override
            public String getFilename() {

                return fileName;
            }
        };
    }

    // ==========================================
    // LOAD IMAGE AS BYTE ARRAY
    // ==========================================

    @Override
    public byte[] loadImageAsBytes(String imagePath) {

        Path filePath = resolveImagePath(imagePath);

        if (!Files.isRegularFile(
                filePath,
                LinkOption.NOFOLLOW_LINKS)) {

            throw new IllegalArgumentException(
                    "Image file not found."
            );
        }

        try (InputStream inputStream =
                     Files.newInputStream(
                             filePath,
                             StandardOpenOption.READ,
                             LinkOption.NOFOLLOW_LINKS
                     )) {

            return readLimitedBytes(inputStream);

        } catch (IOException ex) {

            log.error(
                    "Failed to load image. ImagePath={}",
                    imagePath,
                    ex
            );

            throw new IllegalStateException(
                    "Unable to read image file.",
                    ex
            );
        }
    }

    // ==========================================
    // CHECK IMAGE EXISTS
    // ==========================================

    @Override
    public boolean imageExists(String imagePath) {

        if (imagePath == null || imagePath.isBlank()) {

            return false;
        }

        Path filePath = resolveImagePath(imagePath);

        return Files.isRegularFile(
                filePath,
                LinkOption.NOFOLLOW_LINKS
        );
    }

    // ==========================================
    // DELETE IMAGE
    // ==========================================

    @Override
    public void deleteImage(String imagePath) {

        if (imagePath == null || imagePath.isBlank()) {

            return;
        }

        Path filePath = resolveImagePath(imagePath);

        try {

            if (Files.exists(
                    filePath,
                    LinkOption.NOFOLLOW_LINKS)
                    && !Files.isRegularFile(
                            filePath,
                            LinkOption.NOFOLLOW_LINKS)) {

                throw new IllegalArgumentException(
                        "Invalid image file."
                );
            }

            boolean deleted = Files.deleteIfExists(filePath);

            if (deleted) {

                log.info(
                        "Image deleted successfully. ImagePath={}",
                        imagePath
                );
            }

        } catch (IOException ex) {

            log.error(
                    "Failed to delete image. ImagePath={}",
                    imagePath,
                    ex
            );

            throw new IllegalStateException(
                    "Unable to delete image file.",
                    ex
            );
        }
    }

    // ==========================================
    // RESOLVE AND VALIDATE IMAGE PATH
    // ==========================================

    private Path resolveImagePath(String imagePath) {

        if (imagePath == null || imagePath.isBlank()) {

            throw new IllegalArgumentException(
                    "Image path is required."
            );
        }

        if (!IMAGE_PATH_PATTERN.matcher(imagePath).matches()) {

            throw new IllegalArgumentException(
                    "Invalid image storage path."
            );
        }

        Path resolvedPath = rootStoragePath
                .resolve(imagePath)
                .normalize();

        if (!resolvedPath.startsWith(rootStoragePath)) {

            throw new IllegalArgumentException(
                    "Invalid image storage location."
            );
        }

        validateNoSymbolicLinks(resolvedPath);

        return resolvedPath;
    }

    // ==========================================
    // PROTECT AGAINST SYMBOLIC LINKS
    // ==========================================

    private void validateNoSymbolicLinks(Path path) {

        Path absolutePath = path.toAbsolutePath().normalize();

        Path currentPath = absolutePath.getRoot();

        for (Path part : absolutePath) {

            currentPath = currentPath.resolve(part);

            if (Files.isSymbolicLink(currentPath)) {

                throw new IllegalArgumentException(
                        "Symbolic links are not allowed "
                        + "in image storage paths."
                );
            }
        }
    }

    // ==========================================
    // READ IMAGE WITH FILE SIZE LIMIT
    // ==========================================

    private byte[] readLimitedBytes(
            InputStream inputStream) throws IOException {

        long maxFileSize =
                storageProperties.getMaxFileSize();

        if (maxFileSize >= Integer.MAX_VALUE) {

            throw new IllegalStateException(
                    "Configured image file size is too large."
            );
        }

        byte[] bytes = inputStream.readNBytes(
                (int) maxFileSize + 1
        );

        if (bytes.length > maxFileSize) {

            throw new IllegalArgumentException(
                    "Image file exceeds the maximum allowed size."
            );
        }

        return bytes;
    }
}
