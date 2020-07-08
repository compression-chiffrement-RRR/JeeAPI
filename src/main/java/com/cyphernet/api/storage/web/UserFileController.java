package com.cyphernet.api.storage.web;

import com.cyphernet.api.account.model.Account;
import com.cyphernet.api.account.model.AccountDetail;
import com.cyphernet.api.account.service.AccountService;
import com.cyphernet.api.exception.AccountNotFoundException;
import com.cyphernet.api.exception.FileNotRetrieveException;
import com.cyphernet.api.exception.UserFileNotFoundException;
import com.cyphernet.api.storage.AmazonClient;
import com.cyphernet.api.storage.model.UserFile;
import com.cyphernet.api.storage.model.UserFileAccountDTO;
import com.cyphernet.api.storage.model.UserFileDTO;
import com.cyphernet.api.storage.service.UserFileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;

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
    public ResponseEntity<UserFileDTO> getFile(@PathVariable String fileUuid, @AuthenticationPrincipal AccountDetail currentAccount) {
        UserFile userFile = userFileService.getUserFileByUuidAndAccountUuid(fileUuid, currentAccount.getUuid())
                .orElseThrow(() -> new UserFileNotFoundException("uuid", fileUuid));
        return ok(userFile.toDTO());
    }

    @Secured("ROLE_USER")
    @PreAuthorize("#accountUuid == authentication.principal.uuid")
    @GetMapping
    public ResponseEntity<UserFileAccountDTO> getFiles(@AuthenticationPrincipal AccountDetail currentAccount) {
        String accountUuid = currentAccount.getUuid();

        Account account = accountService.getAccountByUuid(accountUuid)
                .orElseThrow(() -> new AccountNotFoundException("uuid", accountUuid));

        List<UserFileDTO> userFileAuthorDTOs = account.getUserFiles()
                .stream()
                .map(UserFile::toDTO)
                .collect(Collectors.toList());

        List<UserFileDTO> userFileCollaboratorDTOs = account.getUserFilesCollaborator()
                .stream()
                .map(UserFile::toDTO)
                .collect(Collectors.toList());

        UserFileAccountDTO userFileAccountDTO = new UserFileAccountDTO()
                .setUserFilesAuthor(userFileAuthorDTOs)
                .setUserFilesCollaborator(userFileCollaboratorDTOs);

        return ok(userFileAccountDTO);
    }

    @Secured("ROLE_USER")
    @GetMapping("/download/{fileUuid}")
    public ResponseEntity<ByteArrayResource> getDownloadFiles(@PathVariable String fileUuid, @AuthenticationPrincipal AccountDetail currentAccount) throws FileNotRetrieveException {
        UserFile userFile = userFileService.getUserFileByUuidAndAccountUuid(fileUuid, currentAccount.getUuid())
                .orElseThrow(() -> new UserFileNotFoundException("uuid", fileUuid));

        byte[] fileBytes;

        try {
            fileBytes = amazonClient.download(userFile.getFileNamePrivate());
        } catch (IOException e) {
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
        amazonClient.deleteFileFromS3Bucket(userFile.getFileNamePrivate());
        userFileService.deleteUserFile(fileUuid);
        return ok().build();
    }
}
