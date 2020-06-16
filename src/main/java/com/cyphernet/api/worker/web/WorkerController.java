package com.cyphernet.api.worker.web;

import com.cyphernet.api.account.model.Account;
import com.cyphernet.api.account.model.AccountDTO;
import com.cyphernet.api.account.service.AccountService;
import com.cyphernet.api.exception.AccountNotFoundException;
import com.cyphernet.api.exception.FileNotSavedException;
import com.cyphernet.api.storage.model.UserFile;
import com.cyphernet.api.storage.model.UserFileDTO;
import com.cyphernet.api.storage.service.UserFileService;
import com.cyphernet.api.worker.model.WorkerTask;
import com.cyphernet.api.worker.model.WorkerTaskProcess;
import com.cyphernet.api.storage.AmazonClient;
import com.cyphernet.api.worker.service.WorkerTaskService;
import org.apache.http.HttpStatus;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Optional;

import static org.springframework.http.ResponseEntity.*;

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

    @PostMapping("/uploadFile")
    public ResponseEntity<UserFileDTO> uploadFile(@RequestPart(value = "file") MultipartFile file, @RequestPart(value = "accountUuid") String accountUuid) throws FileNotSavedException {
        String fileUrl = "";
        try {
            fileUrl = this.amazonClient.uploadFile(file);
        } catch (IOException e) {
            e.printStackTrace();
            throw new FileNotSavedException();
        }

        workerTaskService.createAndSendNewWorkerTask("test", fileUrl, file.getOriginalFilename().replace(" ", "_"), null);

        Account account = accountService.getAccountByUuid(accountUuid)
                .orElseThrow(() -> new AccountNotFoundException("uuid", accountUuid));

        UserFile userFile = userFileService.createUserFile(file.getOriginalFilename(), account);

        return accepted().body(userFile.toDTO());
    }

    @DeleteMapping("/deleteFile")
    public String deleteFile(@RequestPart(value = "url") String fileUrl) {
        return this.amazonClient.deleteFileFromS3Bucket(fileUrl);
    }
}
