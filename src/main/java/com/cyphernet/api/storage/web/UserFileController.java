package com.cyphernet.api.storage.web;

import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.cyphernet.api.account.model.AccountDetail;
import com.cyphernet.api.account.service.AccountService;
import com.cyphernet.api.exception.FileNotRetrieveException;
import com.cyphernet.api.exception.FileNotTreatedException;
import com.cyphernet.api.exception.UserFileNotFoundException;
import com.cyphernet.api.storage.AmazonClient;
import com.cyphernet.api.storage.model.UserFile;
import com.cyphernet.api.storage.model.UserFileAccountDTO;
import com.cyphernet.api.storage.model.UserFileDTO;
import com.cyphernet.api.storage.model.UserFileInformationDTO;
import com.cyphernet.api.storage.service.UserFileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.springframework.http.ResponseEntity.noContent;
import static org.springframework.http.ResponseEntity.ok;

@RestController
@RequestMapping("/api/file")
public class UserFileController {
    private final UserFileService userFileService;
    private final AccountService accountService;
    private final AmazonClient amazonClient;

    @Autowired
    public UserFileController(UserFileService userFileService, AccountService accountService, AmazonClient amazonClient) {
        this.userFileService = userFileService;
        this.accountService = accountService;
        this.amazonClient = amazonClient;
    }

    @Secured("ROLE_USER")
    @GetMapping("/{fileUuid}")
    @Transactional
    public ResponseEntity<UserFileInformationDTO> getFile(@PathVariable String fileUuid, @AuthenticationPrincipal AccountDetail currentAccount) {
        UserFile userFile = userFileService.getUserFileByUuidAndAccountUuid(fileUuid, currentAccount.getUuid())
                .orElseThrow(() -> new UserFileNotFoundException("uuid", fileUuid));
        return ok(userFile.toInformationDTO());
    }

    @Secured("ROLE_USER")
    @GetMapping
    public ResponseEntity<UserFileAccountDTO> getFiles(@AuthenticationPrincipal AccountDetail currentAccount) {
        String accountUuid = currentAccount.getUuid();

        List<UserFileDTO> userFileAuthorDTO = accountService.getFilesAuthorDTO(accountUuid);
        List<UserFileDTO> userFileCollaboratorDTO = accountService.getFilesCollaboratorDTO(accountUuid);

        UserFileAccountDTO userFileAccountDTO = new UserFileAccountDTO().setUserFilesAuthor(userFileAuthorDTO).setUserFilesCollaborator(userFileCollaboratorDTO);

        return ok(userFileAccountDTO);
    }

    @Secured("ROLE_USER")
    @GetMapping("/download/{fileUuid}")
    public ResponseEntity<ByteArrayResource> getDownloadFilesProcessed(@PathVariable String fileUuid, @AuthenticationPrincipal AccountDetail currentAccount) throws FileNotRetrieveException, FileNotTreatedException {
        UserFile userFile = userFileService.getUserFileSharedOrOwned(fileUuid, currentAccount.getUuid())
                .orElseThrow(() -> new UserFileNotFoundException("uuid", fileUuid));

        if (!userFile.getIsTreated()) {
            throw new FileNotTreatedException();
        }

        byte[] fileBytes;

        try {
            fileBytes = amazonClient.download(userFile.getFileNamePrivate());
        } catch (IOException | AmazonS3Exception e) {
            e.printStackTrace();
            throw new FileNotRetrieveException();
        }

        final ByteArrayResource resource = new ByteArrayResource(fileBytes);

        String fileName = URLEncoder.encode(userFile.getFileNamePublic(), StandardCharsets.UTF_8)
                .replaceAll("\\+", "%20");

        return ResponseEntity
                .ok()
                .contentLength(fileBytes.length)
                .header("Content-type", "application/octet-stream")
                .header("Content-disposition", "attachment; filename=\"" + fileName + "\"")
                .body(resource);
    }

    @Secured("ROLE_USER")
    @DeleteMapping("/{fileUuid}")
    public ResponseEntity<Void> deleteFile(@PathVariable String fileUuid, @AuthenticationPrincipal AccountDetail currentAccount) {
        UserFile userFile = userFileService.getUserFileByUuidAndAccountUuid(fileUuid, currentAccount.getUuid())
                .orElseThrow(() -> new UserFileNotFoundException("uuid", fileUuid));
        try {
            amazonClient.deleteFileFromS3Bucket(userFile.getFileNamePrivate());
        } catch (SdkClientException e) {
            e.printStackTrace();
        }
        userFileService.deleteUserFile(fileUuid);
        return noContent().build();
    }
}
