package dev.rono.igniscore.resourcepack;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TextureFileWriterTest {
    @TempDir
    Path tempDir;

    @Test
    void convertsJpegSourceToPackPng() throws Exception {
        byte[] jpegBytes = createJpegBytes();
        Path destination = tempDir.resolve("top.png");

        try (InputStream source = new ByteArrayInputStream(jpegBytes)) {
            TextureFileWriter.writePackTexture(source, "top.jpg", destination);
        }

        assertTrue(Files.exists(destination));
        BufferedImage image = ImageIO.read(destination.toFile());
        assertNotNull(image);
        assertTrue(image.getWidth() > 0);
        assertTrue(image.getHeight() > 0);
    }

    @Test
    void copiesPngSourceWithoutConversion() throws Exception {
        BufferedImage sourceImage = new BufferedImage(2, 2, BufferedImage.TYPE_INT_ARGB);
        ByteArrayOutputStream pngBytes = new ByteArrayOutputStream();
        ImageIO.write(sourceImage, "png", pngBytes);
        Path destination = tempDir.resolve("side.png");

        try (InputStream source = new ByteArrayInputStream(pngBytes.toByteArray())) {
            TextureFileWriter.writePackTexture(source, "side.png", destination);
        }

        assertTrue(Files.exists(destination));
        assertNotNull(ImageIO.read(destination.toFile()));
    }

    private byte[] createJpegBytes() throws Exception {
        BufferedImage sourceImage = new BufferedImage(2, 2, BufferedImage.TYPE_INT_RGB);
        ByteArrayOutputStream jpegBytes = new ByteArrayOutputStream();
        ImageIO.write(sourceImage, "jpg", jpegBytes);
        return jpegBytes.toByteArray();
    }
}
