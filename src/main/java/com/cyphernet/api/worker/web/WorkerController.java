package com.cyphernet.api.worker.web;

import com.amazonaws.HttpMethod;
import com.cyphernet.api.account.model.Account;
import com.cyphernet.api.account.model.AccountDetail;
import com.cyphernet.api.account.service.AccountService;
import com.cyphernet.api.exception.*;
import com.cyphernet.api.mail.service.EmailSenderService;
import com.cyphernet.api.mail.service.EmailService;
import com.cyphernet.api.storage.AmazonClient;
import com.cyphernet.api.storage.model.UserFile;
import com.cyphernet.api.storage.model.UserFileDTO;
import com.cyphernet.api.storage.model.UserFileProcess;
import com.cyphernet.api.storage.service.UserFileProcessService;
import com.cyphernet.api.storage.service.UserFileService;
import com.cyphernet.api.worker.model.*;
import com.cyphernet.api.worker.model.processTypes.Cipher.CipherProcess;
import com.cyphernet.api.worker.model.processTypes.Cipher.IV.CipherProcessWithIV;
import com.cyphernet.api.worker.model.processTypes.Compress.CompressProcess;
import com.cyphernet.api.worker.model.processTypes.Process;
import com.cyphernet.api.worker.model.unprocessTypes.Unprocess;
import com.cyphernet.api.worker.service.WorkerTaskProcessService;
import com.cyphernet.api.worker.service.WorkerTaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.*;
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
    private final EmailSenderService emailSenderService;
    private final EmailService emailService;

    @Value("${server.exposedPort}")
    private Integer exposedPort;

    @Value("${server.privateHostname}")
    private String privateHostname;

    @Autowired
    public WorkerController(AmazonClient amazonClient, WorkerTaskService workerTaskService, UserFileService userFileService, UserFileProcessService userFileProcessService, AccountService accountService, WorkerTaskProcessService workerTaskProcessService, EmailSenderService emailSenderService, EmailService emailService) {
        this.amazonClient = amazonClient;
        this.workerTaskService = workerTaskService;
        this.userFileService = userFileService;
        this.userFileProcessService = userFileProcessService;
        this.accountService = accountService;
        this.workerTaskProcessService = workerTaskProcessService;
        this.emailSenderService = emailSenderService;
        this.emailService = emailService;
    }

    @PostMapping("/confirmFileTreatment")
    @Transactional
    public ResponseEntity<?> confirmFileTreatment(@RequestParam("token") String token, @RequestBody WorkerTaskResult workerTaskResult) {
        if (workerTaskResult.getError() != null && !workerTaskResult.getError().isEmpty()) {
            UserFile userFile = userFileService.setUserFileAsError(workerTaskResult.getFileID(), token, workerTaskResult.getError())
                    .orElseThrow(() -> new UserFileNotFoundException("uuid", workerTaskResult.getFileID()));
            amazonClient.deleteFileFromS3Bucket(userFile.getFileNamePrivate());

            Account account = userFile.getAccount();

            SimpleMailMessage errorMail = emailService.createErrorTreatmentFileEmail(account, userFile);

            emailSenderService.sendEmail(errorMail);
        } else {
            UserFile userFile = userFileService.setUserFileAsTreated(workerTaskResult.getFileID(), token)
                    .orElseThrow(() -> new UserFileNotFoundException("uuid", workerTaskResult.getFileID()));

            Account account = userFile.getAccount();

            SimpleMailMessage confirmationMail = emailService.createConfirmTreatmentFileEmail(account, userFile);

            emailSenderService.sendEmail(confirmationMail);
        }
        return ok().build();
    }

    @Secured("ROLE_USER")
    @PostMapping(value = "/uploadFile")
    public ResponseEntity<UserFileDTO> uploadFile(@RequestPart(value = "file") MultipartFile file, @RequestPart(value = "tasks") WorkerTaskCreationDTO workerTaskCreationDTO, @AuthenticationPrincipal AccountDetail currentAccount) throws FileNotSavedException {
        Account account = accountService.getAccountByUuid(currentAccount.getUuid())
                .orElseThrow(() -> new AccountNotFoundException("uuid", currentAccount.getUuid()));

        List<Process> processes;
        try {
            processes = Arrays.stream(workerTaskCreationDTO.getTypes()).map(s -> {
                try {
                    return ProcessFactory.Create(ProcessTaskType.valueOf(s.getName()), s.getPassword(), this.workerTaskProcessService);
                } catch (InvalidKeySpecException | NoSuchAlgorithmException e) {
                    e.printStackTrace();
                    return null;
                }
            }).collect(Collectors.toList());
        } catch (IllegalArgumentException e) {
            throw new ProcessTypeUnknownException();
        }

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

        String responseUrl = String.format("http://%s:%d/api/worker/confirmFileTreatment?token=%s", privateHostname, exposedPort, userFile.getConfirmToken());
        String fileUrlPresignedGet = this.amazonClient.getPresignedUrl(newFileName, HttpMethod.GET).toString();
        String fileUrlPresignedPut = this.amazonClient.getPresignedUrl(newFileName, HttpMethod.PUT).toString();

        workerTaskService.createAndSendNewWorkerTaskProcess(responseUrl, fileUrlPresignedGet, fileUrlPresignedPut, userFile.getUuid(), processes);

        return accepted().body(userFile.toDTO());
    }

    @Secured("ROLE_USER")
    @PostMapping("/retrieveFile/{fileUuid}")
    @Transactional
    public ResponseEntity<UserFileDTO> retrieveFile(@PathVariable String fileUuid, @RequestBody UserFileUnprocessDTO userFileUnprocessDTO, @AuthenticationPrincipal AccountDetail currentAccount) throws MissingPasswordException, FileNotTreatedException, FileNotFoundException {
        Account account = accountService.getAccountByUuid(currentAccount.getUuid())
                .orElseThrow(() -> new AccountNotFoundException("uuid", currentAccount.getUuid()));

        UserFile userFile = userFileService.getUserFileByUuidAndAccountUuid(fileUuid, currentAccount.getUuid())
                .orElseThrow(() -> new UserFileNotFoundException("uuid", fileUuid));

        if (userFile.getIsTemporary()) {
            throw new UserFileNotFoundException("uuid", fileUuid);
        }
        if (!userFile.getIsTreated()) {
            throw new FileNotTreatedException();
        }

        String newFileName = this.amazonClient.generateFileName();

        List<UserFileProcess> userFileProcessList = userFileProcessService.getUserFileProcess(userFile);

        for (UserFileProcess fileProcess : userFileProcessList) {
            Optional<UnprocessInformationDTO> first = Arrays.stream(userFileUnprocessDTO.getTypes())
                    .filter(unprocessInformationDTO ->
                            unprocessInformationDTO.getUuid().equals(fileProcess.getUuid()))
                    .findFirst();
            if (fileProcess.getSalt() != null && first.isEmpty()) {
                throw new MissingPasswordException();
            }
        }

        List<Unprocess> processes = userFileProcessList.stream().sorted().map(userFileProcess -> {
            Optional<UnprocessInformationDTO> unprocessInformationOptional = Arrays.stream(userFileUnprocessDTO.getTypes())
                    .filter(unprocessInformationDTO ->
                            unprocessInformationDTO.getUuid().equals(userFileProcess.getUuid()))
                    .findFirst();
            try {
                if (unprocessInformationOptional.isPresent()) {
                    UnprocessInformationDTO unprocessInformationDTO = unprocessInformationOptional.get();
                    return UnprocessFactory.Create(userFileProcess.getProcessTaskType(), unprocessInformationDTO.getPassword(), userFileProcess.getSalt(), userFileProcess.getIv(), this.workerTaskProcessService);
                } else {
                    return UnprocessFactory.Create(userFileProcess.getProcessTaskType(), null, null, null, this.workerTaskProcessService);
                }
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }).collect(Collectors.toList());

        Collections.reverse(processes);

        UserFile userFileTemp = userFileService.createUserFileTemp(
                userFile.getFileNamePublic(),
                newFileName,
                userFile.getUuid(),
                account);

        String responseUrl = String.format("http://%s:%d/api/worker/confirmFileTreatment?token=%s", privateHostname, exposedPort, userFileTemp.getConfirmToken());
        String fileUrlPresignedGet = this.amazonClient.getPresignedUrl(userFile.getFileNamePrivate(), HttpMethod.GET).toString();
        String fileUrlPresignedPut = this.amazonClient.getPresignedUrl(newFileName, HttpMethod.PUT).toString();

        workerTaskService.createAndSendNewWorkerTaskUnprocess(responseUrl, fileUrlPresignedGet, fileUrlPresignedPut, userFileTemp.getUuid(), processes);

        return accepted().body(userFileTemp.toDTO());
    }
}
