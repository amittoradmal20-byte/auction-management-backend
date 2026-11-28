package com.auction.matchschedule.util.pdf;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.openpdf.text.Image;
import org.openpdf.text.BadElementException;
import com.auction.storage.service.ImageStorageService;

@Component
public class PdfImageUtil {

    private static final Logger log = LoggerFactory.getLogger(PdfImageUtil.class);
    private static final int MAX_IMAGE_WIDTH = 360;
    private static final int MAX_IMAGE_HEIGHT = 360;
    private static final float JPEG_QUALITY = 0.85f;

    private final ImageStorageService imageStorageService;

    public PdfImageUtil(ImageStorageService imageStorageService) {
        this.imageStorageService = imageStorageService;
    }

    // Cache belongs to a single PDF generation request. Null values cache failures.
    public Image loadImage(String imagePath, float maxWidth, float maxHeight,
                           Map<String, Image> imageCache) {
        if (imagePath == null || imagePath.isBlank()) {
            return null;
        }
        if (!Float.isFinite(maxWidth) || !Float.isFinite(maxHeight)
                || maxWidth <= 0 || maxHeight <= 0) {
            throw new IllegalArgumentException("PDF image dimensions must be positive.");
        }
        if (imageCache == null) {
            throw new IllegalArgumentException("PDF image cache is required.");
        }

        // HashMap supports null values, unlike computeIfAbsent failure caching.
        if (!imageCache.containsKey(imagePath)) {
            imageCache.put(imagePath, prepareImage(imagePath));
        }
        Image cachedImage = imageCache.get(imagePath);
        if (cachedImage == null) {
            return null;
        }

        // Copy placement state without decoding the original bytes again.
        Image image = Image.getInstance(cachedImage);
        image.scaleToFit(maxWidth, maxHeight);
        return image;
    }

    private Image prepareImage(String imagePath) {
        try {
            byte[] original = imageStorageService.loadImageAsBytes(imagePath);
            if (original == null || original.length == 0) {
                return null;
            }
            byte[] optimized = optimizeImage(original);
            Image image = Image.getInstance(optimized);
            log.debug("PDF image optimized. ImagePath={}, OriginalBytes={}, OptimizedBytes={}, ReductionPercentage={}",
                    imagePath, original.length, optimized.length,
                    String.format(java.util.Locale.ROOT, "%.2f",
                            100.0 * (original.length - optimized.length) / original.length));
            return image;
        } catch (BadElementException | IOException | IllegalArgumentException | IllegalStateException ex) {
            log.warn("Unable to prepare PDF image. ImagePath={}", imagePath, ex);
            return null;
        }
    }

    private byte[] optimizeImage(byte[] originalBytes) throws IOException {
        BufferedImage original;
        try (ByteArrayInputStream input = new ByteArrayInputStream(originalBytes)) {
            original = ImageIO.read(input);
        }
        if (original == null) {
            throw new IOException("Unable to decode image.");
        }
        BufferedImage resized = null;
        try {
            boolean hasTransparency = original.getColorModel().hasAlpha();
            resized = resizeImage(original, hasTransparency);
            byte[] optimized = hasTransparency ? encodePng(resized) : encodeJpeg(resized);
            if (original.getWidth() <= MAX_IMAGE_WIDTH
                    && original.getHeight() <= MAX_IMAGE_HEIGHT
                    && originalBytes.length <= optimized.length) {
                return originalBytes;
            }
            return optimized;
        } finally {
            if (resized != null && resized != original) {
                resized.flush();
            }
            original.flush();
        }
    }

    private BufferedImage resizeImage(BufferedImage original, boolean hasTransparency) {
        double scale = Math.min(1.0, Math.min(
                (double) MAX_IMAGE_WIDTH / original.getWidth(),
                (double) MAX_IMAGE_HEIGHT / original.getHeight()));
        int width = Math.max(1, (int) Math.round(original.getWidth() * scale));
        int height = Math.max(1, (int) Math.round(original.getHeight() * scale));
        BufferedImage resized = new BufferedImage(width, height,
                hasTransparency ? BufferedImage.TYPE_INT_ARGB : BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics = resized.createGraphics();
        try {
            graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
            graphics.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            graphics.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
            graphics.drawImage(original, 0, 0, width, height, null);
        } finally {
            graphics.dispose();
        }
        return resized;
    }

    private byte[] encodePng(BufferedImage image) throws IOException {
        try (ByteArrayOutputStream output = new ByteArrayOutputStream()) {
            if (!ImageIO.write(image, "png", output)) {
                throw new IOException("PNG image writer is unavailable.");
            }
            return output.toByteArray();
        }
    }

    private byte[] encodeJpeg(BufferedImage image) throws IOException {
        Iterator<ImageWriter> writers = ImageIO.getImageWritersByFormatName("jpeg");
        if (!writers.hasNext()) {
            throw new IOException("JPEG image writer is unavailable.");
        }
        ImageWriter writer = writers.next();
        try (ByteArrayOutputStream output = new ByteArrayOutputStream()) {
            try (ImageOutputStream imageOutput = ImageIO.createImageOutputStream(output)) {
                if (imageOutput == null) {
                    throw new IOException("Unable to create image output stream.");
                }
                writer.setOutput(imageOutput);
                ImageWriteParam param = writer.getDefaultWriteParam();
                if (param.canWriteCompressed()) {
                    param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
                    param.setCompressionQuality(JPEG_QUALITY);
                }
                writer.write(null, new IIOImage(image, null, null), param);
                imageOutput.flush();
            }
            return output.toByteArray();
        } finally {
            writer.dispose();
        }
    }
}
