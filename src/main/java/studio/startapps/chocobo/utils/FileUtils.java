package studio.startapps.chocobo.utils;

import org.springframework.http.MediaType;

import java.util.List;

public interface FileUtils {

    static String getExtension(String filename) {
        String[] parts = filename.split("\\.");
        return parts[parts.length - 1];
    }

    static MediaType getMediaType(String filename) {
        String extension = FileUtils.getExtension(filename);
        if (List.of("jpg", "jpeg").contains(extension)) {
            return MediaType.IMAGE_JPEG;
        }
        else if (List.of("png").contains(extension)) {
            return MediaType.IMAGE_PNG;
        }
        else if (List.of("gif").contains(extension)) {
            return MediaType.IMAGE_GIF;
        }
        else if (List.of("mp4", "webm", "mpeg").contains(extension)) {
            return MediaType.parseMediaType(String.format("video/%s", extension));
        }
        return null;
    }
}
