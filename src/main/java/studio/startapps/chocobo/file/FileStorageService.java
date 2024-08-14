package studio.startapps.chocobo.file;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import studio.startapps.chocobo.utils.FileUtils;
import studio.startapps.chocobo.utils.StringUtils;

@Service
public class FileStorageService {

    private static final Path MEDIA_ROOT = Paths.get("storage/media");
    private static final Path THUMBNAIL_ROOT = Paths.get("storage/thumbs");

    public static void initDirs() throws IOException {
        Files.createDirectories(MEDIA_ROOT);
        Files.createDirectories(THUMBNAIL_ROOT);
    }

    public String saveThumbnail(MultipartFile file, String filename) {
        return this.save(file, filename, THUMBNAIL_ROOT);
    }

    public String saveMedia(MultipartFile file, String filename) {
        return this.save(file, filename, MEDIA_ROOT);
    }

    private String save(MultipartFile file, String filename, Path folder) {
        try {
            String extension = FileUtils.getExtension(file.getOriginalFilename());
            String filenameWithExtension = String.format("%s.%s", filename, extension);

            Files.copy(file.getInputStream(), folder.resolve(filenameWithExtension));
            return filenameWithExtension;
        }
        catch (Exception e) {
            if (e instanceof FileAlreadyExistsException) {
                filename = filename + "1"; // Append 1 at the end of the filename
                return this.save(file, filename, folder);
            }
            throw new RuntimeException(e.getMessage());
        }
    }

    Resource loadThumbnail(String filename) throws MalformedURLException {
        return this.load(filename, THUMBNAIL_ROOT);
    }

    Resource loadMedia(String filename) throws MalformedURLException {
        return this.load(filename, MEDIA_ROOT);
    }

    Path getMediaPath(String filename) {
        return MEDIA_ROOT.resolve(filename);
    }

    private Resource load(String filename, Path folder) throws MalformedURLException {
        Path file = folder.resolve(filename);
        Resource resource = new UrlResource(file.toUri());

        if (resource.exists() && resource.isReadable()) {
            return resource;
        }
        else {
            throw new RuntimeException("Unable to process file");
        }
    }
}
