package com.cyphernet.api.mail.web;

import com.cyphernet.api.account.model.Account;
import com.cyphernet.api.exception.ConfirmationTokenNotFoundException;
import com.cyphernet.api.mail.model.ConfirmationCollaboratorToken;
import com.cyphernet.api.mail.service.ConfirmationCollaboratorTokenService;
import com.cyphernet.api.mail.service.EmailSenderService;
import com.cyphernet.api.mail.service.EmailService;
import com.cyphernet.api.storage.model.UserFileCollaborator;
import com.cyphernet.api.storage.service.UserFileCollaboratorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/mail")
public class MailController {
    private final ConfirmationCollaboratorTokenService confirmationCollaboratorTokenService;
    private final UserFileCollaboratorService userFileCollaboratorService;
    private final EmailSenderService emailSenderService;
    private final EmailService emailService;

    @Autowired
    public MailController(ConfirmationCollaboratorTokenService confirmationCollaboratorTokenService, UserFileCollaboratorService userFileCollaboratorService, EmailSenderService emailSenderService, EmailService emailService) {
        this.confirmationCollaboratorTokenService = confirmationCollaboratorTokenService;
        this.userFileCollaboratorService = userFileCollaboratorService;
        this.emailSenderService = emailSenderService;
        this.emailService = emailService;
    }

    @GetMapping("/confirmCollaborator")
    @Transactional
    public ResponseEntity<?> confirmCollaborator(@RequestParam("token") String token) {
        ConfirmationCollaboratorToken confirmationCollaboratorToken = confirmationCollaboratorTokenService.confirmToken(token)
                .orElseThrow(() -> new ConfirmationTokenNotFoundException("token", token));

        List<UserFileCollaborator> userFileCollaborators = userFileCollaboratorService.validateCollaborators(confirmationCollaboratorToken);

        userFileCollaborators.forEach(userFileCollaborator -> {
            Account account = userFileCollaborator.getAccount();
            SimpleMailMessage mailNewFileAccessGranted = emailService.createNewFileAccessGranted(account);
            emailSenderService.sendEmail(mailNewFileAccessGranted);
        });

        return ResponseEntity.ok("Validated collaborators !");
    }
}
