package com.cyphernet.api.mail.service;

import com.cyphernet.api.account.model.Account;
import com.cyphernet.api.mail.model.ConfirmationCollaboratorToken;
import com.cyphernet.api.storage.model.UserFile;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
    @Value("${spring.mail.username}")
    private String mailFrom;
    @Value("${server.publicHostname}")
    private String hostname;
    @Value("${server.publicPort}")
    private String port;

    public SimpleMailMessage createConfirmationCollaboratorsEmail(Account account, ConfirmationCollaboratorToken confirmationCollaboratorToken) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(account.getEmail());
        mailMessage.setSubject("Confirm new collaborators!");
        mailMessage.setFrom(mailFrom);
        mailMessage.setText("To confirm the new collaborators, please click here : "
                + String.format("http://%s:%s/api/mail/confirmCollaborator?token=%s", hostname, port, confirmationCollaboratorToken.getConfirmationToken()));
        return mailMessage;
    }

    public SimpleMailMessage createConfirmTreatmentFileEmail(Account account, UserFile userFile) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(account.getEmail());
        mailMessage.setSubject(String.format("File %s available", userFile.getFileNamePublic()));
        mailMessage.setFrom(mailFrom);
        mailMessage.setText(String.format("Your new file %s is ready to be shared on your account space", userFile.getFileNamePublic()));
        return mailMessage;
    }

    public SimpleMailMessage createErrorTreatmentFileEmail(Account account, UserFile userFile) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(account.getEmail());
        mailMessage.setSubject(String.format("ERROR: %s encountered a problem", userFile.getFileNamePublic()));
        mailMessage.setFrom(mailFrom);
        mailMessage.setText(String.format("Your new file %s encountered a problem during the process and is unfortunately no longer available. Please kindly ressay", userFile.getFileNamePublic()));
        return mailMessage;
    }
}
