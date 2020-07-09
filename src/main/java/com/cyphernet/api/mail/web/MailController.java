package com.cyphernet.api.mail.web;

import com.cyphernet.api.exception.ConfirmationTokenNotFoundException;
import com.cyphernet.api.mail.model.ConfirmationCollaboratorToken;
import com.cyphernet.api.mail.service.ConfirmationCollaboratorTokenService;
import com.cyphernet.api.storage.service.UserFileCollaboratorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/mail")
public class MailController {
    private final ConfirmationCollaboratorTokenService confirmationCollaboratorTokenService;
    private final UserFileCollaboratorService userFileCollaboratorService;

    @Autowired
    public MailController(ConfirmationCollaboratorTokenService confirmationCollaboratorTokenService, UserFileCollaboratorService userFileCollaboratorService) {
        this.confirmationCollaboratorTokenService = confirmationCollaboratorTokenService;
        this.userFileCollaboratorService = userFileCollaboratorService;
    }

    @GetMapping("/confirmCollaborator")
    public ResponseEntity<?> confirmCollaborator(@RequestParam("token") String token) {
        ConfirmationCollaboratorToken confirmationCollaboratorToken = confirmationCollaboratorTokenService.confirmToken(token)
                .orElseThrow(() -> new ConfirmationTokenNotFoundException("token", token));

        userFileCollaboratorService.validateCollaborators(confirmationCollaboratorToken);

        return ResponseEntity.ok("Validated collaborators !");
    }
}
