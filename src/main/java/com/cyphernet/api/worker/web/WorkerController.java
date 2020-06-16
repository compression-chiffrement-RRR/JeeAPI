package com.cyphernet.api.worker.web;

import com.cyphernet.api.account.model.Account;
import com.cyphernet.api.account.service.AccountService;
import com.cyphernet.api.exception.AccountNotFoundException;
import com.cyphernet.api.exception.FileNotSavedException;
import com.cyphernet.api.exception.UserFileNotFoundException;
import com.cyphernet.api.storage.AmazonClient;
import com.cyphernet.api.storage.model.UserFile;
import com.cyphernet.api.storage.model.UserFileDTO;
import com.cyphernet.api.storage.service.UserFileService;
import com.cyphernet.api.worker.model.WorkerTaskResult;
import com.cyphernet.api.worker.service.WorkerTaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

import static org.springframework.http.ResponseEntity.accepted;
import static org.springframework.http.ResponseEntity.ok;

@RestController
@RequestMapping("/api/worker")
public class WorkerController {

    private final AmazonClient amazonClient;
    private final WorkerTaskService workerTaskService;
    private final UserFileService userFileService;
    private final AccountService accountService;

    @Autowired
    public WorkerController(AmazonClient amazonClient, WorkerTaskService workerTaskService, UserFileService userFileService, AccountService accountService) {
        this.amazonClient = amazonClient;
        this.workerTaskService = workerTaskService;
        this.userFileService = userFileService;
        this.accountService = accountService;
    }

    @PostMapping("/confirmFileTreatment")
    public ResponseEntity<UserFileDTO> confirmFileTreatment(@RequestBody WorkerTaskResult workerTaskResult) {
        UserFile userFile = userFileService.setUserFileAsTreated(workerTaskResult.getFileID(), "")
                .orElseThrow(() -> new UserFileNotFoundException("uuid", workerTaskResult.getFileID()));
        return ok(userFile.toDTO());
    }

    @PostMapping("/uploadFile")
    public ResponseEntity<UserFileDTO> uploadFile(@RequestPart(value = "file") MultipartFile file, @RequestPart(value = "accountUuid") String accountUuid) throws FileNotSavedException {
        Account account = accountService.getAccountByUuid(accountUuid)
                .orElseThrow(() -> new AccountNotFoundException("uuid", accountUuid));

        String fileUrl = "";
        try {
            fileUrl = this.amazonClient.uploadFile(file);
        } catch (IOException e) {
            e.printStackTrace();
            throw new FileNotSavedException();
        }

        UserFile userFile = userFileService.createUserFile(file.getOriginalFilename().replace(" ", "_"), account);

        workerTaskService.createAndSendNewWorkerTask("test", fileUrl, userFile.getUuid(), null);

        return accepted().body(userFile.toDTO());
    }

    @DeleteMapping("/deleteFile")
    public String deleteFile(@RequestPart(value = "url") String fileUrl) {
        return this.amazonClient.deleteFileFromS3Bucket(fileUrl);
    }
}
