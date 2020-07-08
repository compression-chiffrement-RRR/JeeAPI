package com.cyphernet.api.worker.web;

import com.amazonaws.HttpMethod;
import com.cyphernet.api.account.model.Account;
import com.cyphernet.api.account.service.AccountService;
import com.cyphernet.api.exception.AccountNotFoundException;
import com.cyphernet.api.exception.FileNotSavedException;
import com.cyphernet.api.exception.UserFileNotFoundException;
import com.cyphernet.api.storage.AmazonClient;
import com.cyphernet.api.storage.model.UserFile;
import com.cyphernet.api.storage.model.UserFileDTO;
import com.cyphernet.api.storage.model.UserFileProcess;
import com.cyphernet.api.storage.service.UserFileProcessService;
import com.cyphernet.api.storage.service.UserFileService;
import com.cyphernet.api.worker.model.ProcessFactory;
import com.cyphernet.api.worker.model.ProcessTaskType;
import com.cyphernet.api.worker.model.WorkerTaskCreationDTO;
import com.cyphernet.api.worker.model.WorkerTaskResult;
import com.cyphernet.api.worker.model.processTypes.Cipher.CipherProcess;
import com.cyphernet.api.worker.model.processTypes.Cipher.IV.CipherProcessWithIV;
import com.cyphernet.api.worker.model.processTypes.Compress.CompressProcess;
import com.cyphernet.api.worker.model.processTypes.Process;
import com.cyphernet.api.worker.service.WorkerTaskProcessService;
import com.cyphernet.api.worker.service.WorkerTaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static org.springframework.http.ResponseEntity.accepted;
import static org.springframework.http.ResponseEntity.ok;

@RestController
@RequestMapping("/api/worker")
public class WorkerController {

    private final AmazonClient amazonClient;
    private final WorkerTaskService workerTaskService;
    private final UserFileService userFileService;
    private final UserFileProcessService userFileProcessService;
    private final AccountService accountService;
    private final WorkerTaskProcessService workerTaskProcessService;

    @Value("${server.port}")
    private Integer port;

    @Value("${server.privateHostname}")
    private String privateHostname;

    @Autowired
    public WorkerController(AmazonClient amazonClient, WorkerTaskService workerTaskService, UserFileService userFileService, UserFileProcessService userFileProcessService, AccountService accountService, WorkerTaskProcessService workerTaskProcessService) {
        this.amazonClient = amazonClient;
        this.workerTaskService = workerTaskService;
        this.userFileService = userFileService;
        this.userFileProcessService = userFileProcessService;
        this.accountService = accountService;
        this.workerTaskProcessService = workerTaskProcessService;
    }

    @Secured("ROLE_USER")
    @PostMapping("/confirmFileTreatment")
    public ResponseEntity<UserFileDTO> confirmFileTreatment(@RequestBody WorkerTaskResult workerTaskResult) {
        UserFile userFile = userFileService.setUserFileAsTreated(workerTaskResult.getFileID())
                .orElseThrow(() -> new UserFileNotFoundException("uuid", workerTaskResult.getFileID()));
        return ok(userFile.toDTO());
    }

    @Secured("ROLE_USER")
    @PostMapping(value = "/uploadFile")
    public ResponseEntity<UserFileDTO> uploadFile(@RequestPart(value = "file") MultipartFile file, @RequestPart(value = "tasks") WorkerTaskCreationDTO workerTaskCreationDTO) throws FileNotSavedException {
        Account account = accountService.getAccountByUuid(workerTaskCreationDTO.getAccountUuid())
                .orElseThrow(() -> new AccountNotFoundException("uuid", workerTaskCreationDTO.getAccountUuid()));

        List<Process> processes = Arrays.stream(workerTaskCreationDTO.getTypes()).map(s -> {
            try {
                return ProcessFactory.Create(ProcessTaskType.valueOf(s.getName()), s.getPassword(), this.workerTaskProcessService);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }).collect(Collectors.toList());

        String newFileName = this.amazonClient.generateFileName();

        try {
            this.amazonClient.uploadFile(file, newFileName);
        } catch (IOException e) {
            e.printStackTrace();
            throw new FileNotSavedException();
        }

        UserFile userFile = userFileService.createUserFile(
                Objects.requireNonNull(file.getOriginalFilename()).replace(" ", "_"),
                newFileName,
                account);

        processes.stream()
                .collect(HashMap<Integer, Process>::new, (h, o) -> h.put(h.size(), o), (h, o) -> {
                }) // Create a map of the index to the object
                .forEach((index, process) -> {
                    UserFileProcess userFileProcess = null;
                    if (process instanceof CipherProcess) {
                        if (process instanceof CipherProcessWithIV) {
                            userFileProcess = userFileProcessService.createUserFileProcess(process.getType(), index, ((CipherProcess) process).getSalt(), ((CipherProcessWithIV) process).getIv(), userFile);
                        } else {
                            userFileProcess = userFileProcessService.createUserFileProcess(process.getType(), index, ((CipherProcess) process).getSalt(), null, userFile);
                        }
                    } else if (process instanceof CompressProcess) {
                        userFileProcess = userFileProcessService.createUserFileProcess(process.getType(), index, null, null, userFile);
                    }
                    if (userFileProcess != null) {
                        userFile.addFileProcess(userFileProcess);
                    }
                });

        String responseUrl = String.format("http://%s:%d/api/worker/confirmFileTreatment", privateHostname, port);
        String fileUrlPresignedGet = this.amazonClient.getPresignedUrl(newFileName, HttpMethod.GET).toString();
        String fileUrlPresignedPut = this.amazonClient.getPresignedUrl(newFileName, HttpMethod.PUT).toString();

        workerTaskService.createAndSendNewWorkerTask(responseUrl, fileUrlPresignedGet, fileUrlPresignedPut, userFile.getUuid(), processes);

        return accepted().body(userFile.toDTO());
    }

    @Secured("ROLE_ADMIN")
    @DeleteMapping("/deleteFile")
    public String deleteFile(@RequestPart(value = "url") String fileUrl) {
        return this.amazonClient.deleteFileFromS3Bucket(fileUrl);
    }
}
