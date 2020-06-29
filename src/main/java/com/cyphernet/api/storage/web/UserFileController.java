package com.cyphernet.api.storage.web;

import com.cyphernet.api.exception.FileNotRetrieveException;
import com.cyphernet.api.exception.UserFileNotFoundException;
import com.cyphernet.api.storage.AmazonClient;
import com.cyphernet.api.storage.model.UserFile;
import com.cyphernet.api.storage.model.UserFileDTO;
import com.cyphernet.api.storage.service.UserFileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import static org.springframework.http.ResponseEntity.ok;

@RestController
@RequestMapping("/api/file")
public class UserFileController {
    private final UserFileService userFileService;
    private final AmazonClient amazonClient;

    @Autowired
    public UserFileController(UserFileService userFileService, AmazonClient amazonClient) {
        this.userFileService = userFileService;
        this.amazonClient = amazonClient;
    }

    @GetMapping("/{fileUuid}")
    public ResponseEntity<UserFileDTO> getFiles(@PathVariable String fileUuid) {
        UserFile userFile = userFileService.getUserFileByUuid(fileUuid)
                .orElseThrow(() -> new UserFileNotFoundException("uuid", fileUuid));
        return ok(userFile.toDTO());
    }

    @GetMapping("/download/{fileUuid}")
    public ResponseEntity<ByteArrayResource> getDownloadFiles(@PathVariable String fileUuid) throws FileNotRetrieveException {
        UserFile userFile = userFileService.getUserFileByUuid(fileUuid)
                .orElseThrow(() -> new UserFileNotFoundException("uuid", fileUuid));

        byte[] fileBytes;

        try {
            fileBytes = amazonClient.download(fileUuid);
        } catch (IOException e) {
            e.printStackTrace();
            throw new FileNotRetrieveException();
        }

        final ByteArrayResource resource = new ByteArrayResource(fileBytes);

        String fileName = URLEncoder.encode(userFile.getFileName(), StandardCharsets.UTF_8)
                .replaceAll("\\+", "%20");

        return ResponseEntity
                .ok()
                .contentLength(fileBytes.length)
                .header("Content-type", "application/octet-stream")
                .header("Content-disposition", "attachment; filename=\"" + fileName + "\"")
                .body(resource);
    }

    @DeleteMapping("/{fileUuid}")
    public ResponseEntity<Void> deleteFile(@PathVariable String fileUuid) {
        userFileService.deleteUserFile(fileUuid);
        return ok().build();
    }
}
