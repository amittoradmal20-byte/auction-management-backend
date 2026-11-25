
package com.auction.storage.service.impl;

import static org.junit.jupiter.api.Assertions.*;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.UUID;

import javax.imageio.ImageIO;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import org.springframework.core.io.Resource;
import org.springframework.mock.web.MockMultipartFile;

import com.auction.storage.config.ImageStorageProperties;
import com.auction.storage.enums.ImageCategory;

class ImageStorageServiceImplTest {

    @TempDir
    Path tempDirectory;

    private ImageStorageServiceImpl imageStorageService;

    private ImageStorageProperties storageProperties;

    private UUID entityId;

    // ==========================================
    // TEST INITIALIZATION
    // ==========================================

    @BeforeEach
    void setUp() {

        storageProperties = new ImageStorageProperties();

        storageProperties.setUploadDir(
                tempDirectory.toString()
        );

        storageProperties.setMaxFileSize(2097152L);

        storageProperties.setAllowedContentTypes(
                List.of(
                        "image/png",
                        "image/jpeg"
                )
        );

        imageStorageService =
                new ImageStorageServiceImpl(
                        storageProperties
                );

        entityId = UUID.randomUUID();
    }

    // ==========================================
    // TEST 01: STORAGE DIRECTORY INITIALIZATION
    // ==========================================

    @Test
    @DisplayName("Should initialize all image storage directories")
    void shouldInitializeStorageDirectories() {

        for (ImageCategory category : ImageCategory.values()) {

            Path categoryPath = tempDirectory.resolve(
                    category.getDirectoryName()
            );

            assertTrue(
                    Files.isDirectory(categoryPath),
                    "Directory not created: "
                            + category.getDirectoryName()
            );
        }
    }

    // ==========================================
    // TEST 02: VALID PNG IMAGE UPLOAD
    // ==========================================

    @Test
    @DisplayName("Should upload a valid PNG image")
    void shouldUploadValidPngImage() throws IOException {

        MockMultipartFile file = createPngFile();

        String imagePath = imageStorageService.uploadImage(
                file,
                ImageCategory.TOURNAMENT,
                entityId
        );

        assertNotNull(imagePath);

        assertTrue(
                imagePath.startsWith(
                        "tournaments/" + entityId + "/"
                )
        );

        assertTrue(imagePath.endsWith(".png"));

        assertTrue(
                Files.isRegularFile(
                        tempDirectory.resolve(imagePath)
                )
        );

        assertArrayEquals(
                file.getBytes(),
                Files.readAllBytes(
                        tempDirectory.resolve(imagePath)
                )
        );
    }

    // ==========================================
    // TEST 03: VALID JPEG IMAGE UPLOAD
    // ==========================================

    @Test
    @DisplayName("Should upload a valid JPEG image")
    void shouldUploadValidJpegImage() throws IOException {

        byte[] imageBytes = createImageBytes(
                "jpeg",
                128,
                128
        );

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "team-logo.jpeg",
                "image/jpeg",
                imageBytes
        );

        String imagePath = imageStorageService.uploadImage(
                file,
                ImageCategory.TEAM,
                entityId
        );

        assertNotNull(imagePath);

        assertTrue(
                imagePath.startsWith(
                        "teams/" + entityId + "/"
                )
        );

        assertTrue(imagePath.endsWith(".jpg"));

        assertTrue(
                Files.isRegularFile(
                        tempDirectory.resolve(imagePath)
                )
        );

        assertArrayEquals(
                imageBytes,
                Files.readAllBytes(
                        tempDirectory.resolve(imagePath)
                )
        );
    }

    // ==========================================
    // TEST 04: LOAD IMAGE AS RESOURCE
    // ==========================================

    @Test
    @DisplayName("Should load an uploaded image as Resource")
    void shouldLoadImageAsResource() throws IOException {

        MockMultipartFile file = createPngFile();

        String imagePath = imageStorageService.uploadImage(
                file,
                ImageCategory.TEAM,
                entityId
        );

        Resource resource =
                imageStorageService.loadImage(imagePath);

        assertNotNull(resource);

        assertTrue(resource.exists());

        assertTrue(resource.isReadable());

        assertNotNull(resource.getFilename());

        assertTrue(
                resource.getFilename().endsWith(".png")
        );

        try (var inputStream = resource.getInputStream()) {

            assertArrayEquals(
                    file.getBytes(),
                    inputStream.readAllBytes()
            );
        }
    }

    // ==========================================
    // TEST 05: LOAD IMAGE AS BYTE ARRAY
    // ==========================================

    @Test
    @DisplayName("Should load an uploaded image as byte array")
    void shouldLoadImageAsBytes() throws IOException {

        MockMultipartFile file = createPngFile();

        String imagePath = imageStorageService.uploadImage(
                file,
                ImageCategory.TOURNAMENT,
                entityId
        );

        byte[] imageBytes =
                imageStorageService.loadImageAsBytes(
                        imagePath
                );

        assertNotNull(imageBytes);

        assertTrue(imageBytes.length > 0);

        assertArrayEquals(
                file.getBytes(),
                imageBytes
        );
    }

    // ==========================================
    // TEST 06: CHECK IMAGE EXISTENCE
    // ==========================================

    @Test
    @DisplayName("Should return true for an existing image")
    void shouldReturnTrueForExistingImage()
            throws IOException {

        String imagePath = uploadTestImage();

        assertTrue(
                imageStorageService.imageExists(imagePath)
        );
    }

    // ==========================================
    // TEST 07: DELETE IMAGE
    // ==========================================

    @Test
    @DisplayName("Should delete an existing image")
    void shouldDeleteExistingImage() throws IOException {

        String imagePath = uploadTestImage();

        assertTrue(
                imageStorageService.imageExists(imagePath)
        );

        imageStorageService.deleteImage(imagePath);

        assertFalse(
                imageStorageService.imageExists(imagePath)
        );

        assertFalse(
                Files.exists(
                        tempDirectory.resolve(imagePath)
                )
        );
    }

    // ==========================================
    // TEST 08: REJECT EMPTY FILE
    // ==========================================

    @Test
    @DisplayName("Should reject an empty image file")
    void shouldRejectEmptyFile() {

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "empty.png",
                "image/png",
                new byte[0]
        );

        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> imageStorageService.uploadImage(
                                file,
                                ImageCategory.TEAM,
                                entityId
                        )
                );

        assertEquals(
                "Image file is required.",
                exception.getMessage()
        );
    }

    // ==========================================
    // TEST 09: REJECT OVERSIZED FILE
    // ==========================================

    @Test
    @DisplayName("Should reject image larger than 2 MB")
    void shouldRejectOversizedFile() {

        byte[] oversizedBytes = new byte[
                (int) storageProperties.getMaxFileSize() + 1
        ];

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "large-image.png",
                "image/png",
                oversizedBytes
        );

        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> imageStorageService.uploadImage(
                                file,
                                ImageCategory.TEAM,
                                entityId
                        )
                );

        assertEquals(
                "Image file exceeds the maximum allowed size.",
                exception.getMessage()
        );
    }

    // ==========================================
    // TEST 10: REJECT UNSUPPORTED FILE TYPE
    // ==========================================

    @Test
    @DisplayName("Should reject unsupported content type")
    void shouldRejectUnsupportedContentType() {

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "document.pdf",
                "application/pdf",
                new byte[]{1, 2, 3, 4}
        );

        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> imageStorageService.uploadImage(
                                file,
                                ImageCategory.TEAM,
                                entityId
                        )
                );

        assertEquals(
                "Only PNG and JPEG images are supported.",
                exception.getMessage()
        );
    }

    // ==========================================
    // TEST 11: REJECT FAKE PNG IMAGE
    // ==========================================

    @Test
    @DisplayName("Should reject fake PNG image content")
    void shouldRejectFakePngImage() {

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "fake-image.png",
                "image/png",
                new byte[]{1, 2, 3, 4, 5}
        );

        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> imageStorageService.uploadImage(
                                file,
                                ImageCategory.TEAM,
                                entityId
                        )
                );

        assertEquals(
                "Uploaded file is not a valid image.",
                exception.getMessage()
        );
    }

    // ==========================================
    // TEST 12: REJECT MISMATCHED CONTENT TYPE
    // ==========================================

    @Test
    @DisplayName("Should reject mismatched image content type")
    void shouldRejectMismatchedContentType()
            throws IOException {

        byte[] pngBytes = createImageBytes(
                "png",
                128,
                128
        );

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "fake-jpeg.jpg",
                "image/jpeg",
                pngBytes
        );

        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> imageStorageService.uploadImage(
                                file,
                                ImageCategory.TEAM,
                                entityId
                        )
                );

        assertEquals(
                "Image content does not match "
                        + "the declared content type.",
                exception.getMessage()
        );
    }

    // ==========================================
    // TEST 13: REJECT INVALID IMAGE DIMENSIONS
    // ==========================================

    @Test
    @DisplayName("Should reject image exceeding maximum dimensions")
    void shouldRejectOversizedImageDimensions()
            throws IOException {

        byte[] imageBytes = createImageBytes(
                "png",
                4097,
                100
        );

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "oversized-dimensions.png",
                "image/png",
                imageBytes
        );

        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> imageStorageService.uploadImage(
                                file,
                                ImageCategory.TEAM,
                                entityId
                        )
                );

        assertEquals(
                "Image dimensions must not exceed "
                        + "4096 x 4096 pixels.",
                exception.getMessage()
        );
    }

    // ==========================================
    // TEST 14: REJECT DIRECTORY TRAVERSAL
    // ==========================================

    @Test
    @DisplayName("Should reject directory traversal paths")
    void shouldRejectDirectoryTraversal() {

        List<String> invalidPaths = List.of(
                "../application.yaml",
                "../../application.yaml",
                "teams/../../application.yaml",
                "/etc/passwd",
                "C:/Windows/system.ini",
                "teams/../logo.png"
        );

        for (String invalidPath : invalidPaths) {

            assertThrows(
                    IllegalArgumentException.class,
                    () -> imageStorageService.loadImageAsBytes(
                            invalidPath
                    )
            );

            assertThrows(
                    IllegalArgumentException.class,
                    () -> imageStorageService.deleteImage(
                            invalidPath
                    )
            );

            assertThrows(
                    IllegalArgumentException.class,
                    () -> imageStorageService.imageExists(
                            invalidPath
                    )
            );
        }
    }

    // ==========================================
    // TEST 15: NONEXISTENT IMAGE
    // ==========================================

    @Test
    @DisplayName("Should reject retrieval of nonexistent image")
    void shouldRejectNonexistentImage() {

        String imagePath =
                "teams/"
                + entityId
                + "/"
                + UUID.randomUUID()
                + ".png";

        assertFalse(
                imageStorageService.imageExists(imagePath)
        );

        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> imageStorageService.loadImageAsBytes(
                                imagePath
                        )
                );

        assertEquals(
                "Image file not found.",
                exception.getMessage()
        );
    }

    // ==========================================
    // TEST 16: UNIQUE IMAGE FILENAMES
    // ==========================================

    @Test
    @DisplayName("Should generate unique filenames for every upload")
    void shouldGenerateUniqueImageFilenames()
            throws IOException {

        MockMultipartFile file = createPngFile();

        String firstImagePath =
                imageStorageService.uploadImage(
                        file,
                        ImageCategory.TEAM,
                        entityId
                );

        String secondImagePath =
                imageStorageService.uploadImage(
                        file,
                        ImageCategory.TEAM,
                        entityId
                );

        assertNotEquals(
                firstImagePath,
                secondImagePath
        );

        assertTrue(
                imageStorageService.imageExists(
                        firstImagePath
                )
        );

        assertTrue(
                imageStorageService.imageExists(
                        secondImagePath
                )
        );
    }

    // ==========================================
    // TEST 17: REJECT NULL CATEGORY
    // ==========================================

    @Test
    @DisplayName("Should reject upload without image category")
    void shouldRejectNullCategory() throws IOException {

        MockMultipartFile file = createPngFile();

        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> imageStorageService.uploadImage(
                                file,
                                null,
                                entityId
                        )
                );

        assertEquals(
                "Image category is required.",
                exception.getMessage()
        );
    }

    // ==========================================
    // TEST 18: REJECT NULL ENTITY ID
    // ==========================================

    @Test
    @DisplayName("Should reject upload without entity ID")
    void shouldRejectNullEntityId() throws IOException {

        MockMultipartFile file = createPngFile();

        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> imageStorageService.uploadImage(
                                file,
                                ImageCategory.TEAM,
                                null
                        )
                );

        assertEquals(
                "Entity ID is required.",
                exception.getMessage()
        );
    }

    // ==========================================
    // TEST 19: SAFE DELETION OF MISSING IMAGE
    // ==========================================

    @Test
    @DisplayName("Should safely delete a nonexistent image")
    void shouldSafelyDeleteNonexistentImage() {

        String imagePath =
                "teams/"
                + entityId
                + "/"
                + UUID.randomUUID()
                + ".png";

        assertDoesNotThrow(
                () -> imageStorageService.deleteImage(
                        imagePath
                )
        );
    }

    // ==========================================
    // TEST 20: NULL AND EMPTY IMAGE PATH
    // ==========================================

    @Test
    @DisplayName("Should handle null and empty image paths")
    void shouldHandleNullAndEmptyImagePaths() {

        assertFalse(
                imageStorageService.imageExists(null)
        );

        assertFalse(
                imageStorageService.imageExists("")
        );

        assertFalse(
                imageStorageService.imageExists("   ")
        );

        assertDoesNotThrow(
                () -> imageStorageService.deleteImage(null)
        );

        assertDoesNotThrow(
                () -> imageStorageService.deleteImage("")
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> imageStorageService.loadImageAsBytes(null)
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> imageStorageService.loadImageAsBytes("")
        );
    }

    // ==========================================
    // HELPER: UPLOAD TEST IMAGE
    // ==========================================

    private String uploadTestImage() throws IOException {

        return imageStorageService.uploadImage(
                createPngFile(),
                ImageCategory.TEAM,
                entityId
        );
    }

    // ==========================================
    // HELPER: CREATE PNG MULTIPART FILE
    // ==========================================

    private MockMultipartFile createPngFile()
            throws IOException {

        byte[] imageBytes = createImageBytes(
                "png",
                128,
                128
        );

        return new MockMultipartFile(
                "file",
                "test-logo.png",
                "image/png",
                imageBytes
        );
    }

    // ==========================================
    // HELPER: GENERATE VALID IMAGE CONTENT
    // ==========================================

    private byte[] createImageBytes(
            String format,
            int width,
            int height) throws IOException {

        BufferedImage image = new BufferedImage(
                width,
                height,
                BufferedImage.TYPE_INT_RGB
        );

        Graphics2D graphics = image.createGraphics();

        try {

            graphics.setColor(Color.BLUE);

            graphics.fillRect(
                    0,
                    0,
                    width,
                    height
            );

        } finally {

            graphics.dispose();
        }

        try (ByteArrayOutputStream outputStream =
                     new ByteArrayOutputStream()) {

            boolean written = ImageIO.write(
                    image,
                    format,
                    outputStream
            );

            if (!written) {

                throw new IOException(
                        "Unable to generate test image: "
                                + format
                );
            }

            return outputStream.toByteArray();

        } finally {

            image.flush();
        }
    }
}
