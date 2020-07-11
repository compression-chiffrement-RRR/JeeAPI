package com.cyphernet.api.storage.web;

import com.cyphernet.api.account.model.Account;
import com.cyphernet.api.account.model.AccountDetail;
import com.cyphernet.api.account.service.AccountService;
import com.cyphernet.api.exception.AccountNotFoundException;
import com.cyphernet.api.exception.UserFileNotFoundException;
import com.cyphernet.api.mail.model.ConfirmationCollaboratorToken;
import com.cyphernet.api.mail.service.ConfirmationCollaboratorTokenService;
import com.cyphernet.api.mail.service.EmailSenderService;
import com.cyphernet.api.mail.service.EmailService;
import com.cyphernet.api.storage.model.UserFile;
import com.cyphernet.api.storage.model.UserFileCollaboratorDTO;
import com.cyphernet.api.storage.model.UserFileDTO;
import com.cyphernet.api.storage.service.UserFileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.http.ResponseEntity.noContent;
import static org.springframework.http.ResponseEntity.ok;

@RestController
@RequestMapping("/api/file/share")
public class UserFileShareController {
    private final UserFileService userFileService;
    private final AccountService accountService;
    private final EmailSenderService emailSenderService;
    private final EmailService emailService;
    private final ConfirmationCollaboratorTokenService confirmationCollaboratorTokenService;

    @Autowired
    public UserFileShareController(UserFileService userFileService, AccountService accountService, EmailSenderService emailSenderService, EmailService emailService, ConfirmationCollaboratorTokenService confirmationCollaboratorTokenService) {
        this.userFileService = userFileService;
        this.accountService = accountService;
        this.emailSenderService = emailSenderService;
        this.emailService = emailService;
        this.confirmationCollaboratorTokenService = confirmationCollaboratorTokenService;
    }

    @Secured("ROLE_USER")
    @PostMapping("/{fileUuid}")
    @Transactional
    public ResponseEntity<UserFileDTO> addCollaborators(@PathVariable String fileUuid, @RequestBody UserFileCollaboratorDTO fileCollaboratorDTO, @AuthenticationPrincipal AccountDetail currentAccount) {
        List<Account> collaborators = new ArrayList<>();

        Account accountUserLogged = accountService.getAccountByUuid(currentAccount.getUuid())
                .orElseThrow(() -> new AccountNotFoundException("uuid", currentAccount.getUuid()));

        fileCollaboratorDTO.getCollaboratorsUuid().forEach(accountUuid -> {
            Account account = accountService.getAccountByUuid(accountUuid)
                    .orElseThrow(() -> new AccountNotFoundException("uuid", accountUuid));
            collaborators.add(account);
        });

        UserFile userFile = userFileService.getUserFileByUuidAndAccountUuid(fileUuid, currentAccount.getUuid())
                .orElseThrow(() -> new UserFileNotFoundException("uuid", fileUuid));

        ConfirmationCollaboratorToken confirmationCollaboratorToken = confirmationCollaboratorTokenService.createConfirmationToken(accountUserLogged);

        userFile = userFileService.addCollaborators(userFile, collaborators, confirmationCollaboratorToken)
                .orElseThrow(() -> new UserFileNotFoundException("uuid", fileUuid));

        SimpleMailMessage confirmationMail = emailService.createConfirmationCollaboratorsEmail(accountUserLogged, confirmationCollaboratorToken);

        emailSenderService.sendEmail(confirmationMail);

        return ok(userFile.toDTO());
    }

    @Secured("ROLE_USER")
    @DeleteMapping("/{fileUuid}")
    @Transactional
    public ResponseEntity<Void> removeCollaborators(@PathVariable String fileUuid, @RequestParam("collaboratorUuid") String collaboratorUuid, @AuthenticationPrincipal AccountDetail currentAccount) {
        Account collaborator = accountService.getAccountByUuid(collaboratorUuid)
                .orElseThrow(() -> new AccountNotFoundException("uuid", collaboratorUuid));

        UserFile userFile = userFileService.getUserFileByUuidAndAccountUuid(fileUuid, currentAccount.getUuid())
                .orElseThrow(() -> new UserFileNotFoundException("uuid", fileUuid));

        userFileService.removeCollaborator(userFile.getUuid(), currentAccount.getUuid(), collaborator)
                .orElseThrow(() -> new UserFileNotFoundException("uuid", fileUuid));

        return noContent().build();
    }
}
