package com.cyphernet.api.mail.service;

import com.cyphernet.api.account.model.Account;
import com.cyphernet.api.mail.model.ConfirmationCollaboratorToken;
import com.cyphernet.api.mail.repository.ConfirmationCollaboratorTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
public class ConfirmationCollaboratorTokenService {
    ConfirmationCollaboratorTokenRepository confirmationCollaboratorTokenRepository;

    @Autowired
    public ConfirmationCollaboratorTokenService(ConfirmationCollaboratorTokenRepository confirmationCollaboratorTokenRepository) {
        this.confirmationCollaboratorTokenRepository = confirmationCollaboratorTokenRepository;
    }

    public ConfirmationCollaboratorToken createConfirmationToken(Account account) {
        UUID uuid = UUID.randomUUID();

        ConfirmationCollaboratorToken confirmationCollaboratorToken = new ConfirmationCollaboratorToken();
        confirmationCollaboratorToken.setAccount(account);
        confirmationCollaboratorToken.setConfirmationToken(uuid.toString());

        return this.confirmationCollaboratorTokenRepository.save(confirmationCollaboratorToken);
    }

    @Transactional
    public Optional<ConfirmationCollaboratorToken> confirmToken(String token) {
        Optional<ConfirmationCollaboratorToken> optionalConfirmationToken = this.confirmationCollaboratorTokenRepository.findByConfirmationTokenAndValidated(token, false);
        if (optionalConfirmationToken.isEmpty()) {
            return Optional.empty();
        }
        ConfirmationCollaboratorToken confirmationCollaboratorToken = optionalConfirmationToken.get();
        confirmationCollaboratorToken.setValidated(true);

        return Optional.of(this.confirmationCollaboratorTokenRepository.save(confirmationCollaboratorToken));
    }
}
