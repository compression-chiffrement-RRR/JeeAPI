package com.cyphernet.api.mail.repository;

import com.cyphernet.api.mail.model.ConfirmationCollaboratorToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ConfirmationCollaboratorTokenRepository extends JpaRepository<ConfirmationCollaboratorToken, String> {
    Optional<ConfirmationCollaboratorToken> findByConfirmationTokenAndValidated(String confirmationToken, Boolean validated);
}
