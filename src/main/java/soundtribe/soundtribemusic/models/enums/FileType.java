package soundtribe.soundtribemusic.models.enums;

import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;

public enum FileType {

    IMAGE("image/png", ".png", 4 * 1024 * 1024),   // 4 MB
    AUDIO("audio/wav", ".wav", 50 * 1024 * 1024);  // 50 MB, ajustá según lo que quieras

    private final String mimeType;
    private final String extension;
    private final long maxSizeBytes;

    FileType(String mimeType, String extension, long maxSizeBytes) {
        this.mimeType = mimeType;
        this.extension = extension;
        this.maxSizeBytes = maxSizeBytes;
    }

    public boolean isValid(MultipartFile file) {
        if (file == null || file.isEmpty()) return false;
        if (!file.getContentType().equalsIgnoreCase(this.mimeType)) return false;
        if (file.getSize() > this.maxSizeBytes) return false;

        switch (this) {
            case IMAGE:
                return validateImage(file);
            case AUDIO:
                return validateAudio(file);
            default:
                return false;
        }
    }

    private boolean validateImage(MultipartFile file) {
        try {
            BufferedImage image = ImageIO.read(file.getInputStream());
            if (image == null) return false;

            // Requisito opcional: imagen cuadrada
            return image.getWidth() == image.getHeight();
        } catch (IOException e) {
            return false;
        }
    }

    private boolean validateAudio(MultipartFile file) {
        // Podés agregar validación extra (como encabezado WAV, etc.) si querés.
        // Por ahora solo validamos mimeType, tamaño y extensión.
        return file.getOriginalFilename() != null && file.getOriginalFilename().toLowerCase().endsWith(extension);
    }

    public String getMimeType() {
        return mimeType;
    }

    public String getExtension() {
        return extension;
    }

    public long getMaxSizeBytes() {
        return maxSizeBytes;
    }
}
