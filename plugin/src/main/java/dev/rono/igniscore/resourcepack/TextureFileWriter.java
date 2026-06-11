package dev.rono.igniscore.resourcepack;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Locale;

final class TextureFileWriter {
    private TextureFileWriter() {
    }

    static void writePackTexture(InputStream source, String sourceFileName, Path destinationPng) throws IOException {
        Files.createDirectories(destinationPng.getParent());
        if (isJpeg(sourceFileName)) {
            BufferedImage image = ImageIO.read(source);
            if (image == null) {
                throw new IOException("Failed to decode JPEG texture: " + sourceFileName);
            }
            ImageIO.write(image, "png", destinationPng.toFile());
            return;
        }

        Files.copy(source, destinationPng, StandardCopyOption.REPLACE_EXISTING);
    }

    private static boolean isJpeg(String fileName) {
        String lower = fileName.toLowerCase(Locale.ROOT);
        return lower.endsWith(".jpg") || lower.endsWith(".jpeg");
    }
}
