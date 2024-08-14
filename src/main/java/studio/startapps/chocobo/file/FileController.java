package studio.startapps.chocobo.file;

import lombok.AllArgsConstructor;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import studio.startapps.chocobo.utils.FileUtils;

@Controller
@RequestMapping(path = "/api/storage")
@AllArgsConstructor
public class FileController {

    private final FileStorageService storageService;
    private final MediaStreamService mediaStreamService;

    @GetMapping(path = "/thumbnails/{filename}")
    ResponseEntity<Resource> getThumbnail(@PathVariable String filename) throws Exception {
        Resource resource = this.storageService.loadThumbnail(filename);
        return ResponseEntity
                .ok()
                .contentType(FileUtils.getMediaType(filename))
                .body(resource);
    }

    @GetMapping(path = "/media/{filename}")
    ResponseEntity<byte[]> getMedia(
        @PathVariable String filename,
        @RequestHeader(value = "Range", required = false) String httpRangeList
    ) {
        return this.mediaStreamService.prepareContent(filename, httpRangeList);
    };
}
