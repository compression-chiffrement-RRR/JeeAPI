package com.cyphernet.api.storage.service;

import com.cyphernet.api.mail.model.ConfirmationCollaboratorToken;
import com.cyphernet.api.storage.model.UserFileCollaborator;
import com.cyphernet.api.storage.repository.UserFileCollaboratorRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserFileCollaboratorService {
    private final UserFileCollaboratorRepository userFileCollaboratorRepository;

    public UserFileCollaboratorService(UserFileCollaboratorRepository userFileCollaboratorRepository) {
        this.userFileCollaboratorRepository = userFileCollaboratorRepository;
    }

    public List<UserFileCollaborator> validateCollaborators(ConfirmationCollaboratorToken confirmationCollaboratorToken) {
        List<UserFileCollaborator> userFileCollaborators = userFileCollaboratorRepository.findAllByConfirmationCollaboratorToken_Uuid(confirmationCollaboratorToken.getUuid());
        userFileCollaborators.forEach(userFileCollaborator -> userFileCollaborator.setPending(false));

        userFileCollaboratorRepository.saveAll(userFileCollaborators);

        return userFileCollaborators;
    }
}
