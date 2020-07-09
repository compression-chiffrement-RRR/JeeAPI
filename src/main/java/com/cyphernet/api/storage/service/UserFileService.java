package com.cyphernet.api.storage.service;

import com.cyphernet.api.account.model.Account;
import com.cyphernet.api.mail.model.ConfirmationCollaboratorToken;
import com.cyphernet.api.storage.model.UserFile;
import com.cyphernet.api.storage.model.UserFileCollaborator;
import com.cyphernet.api.storage.repository.UserFileCollaboratorRepository;
import com.cyphernet.api.storage.repository.UserFileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserFileService {
    private final UserFileRepository userFileRepository;
    private final UserFileCollaboratorRepository userFileCollaboratorRepository;

    @Autowired
    public UserFileService(UserFileRepository userFileRepository, UserFileCollaboratorRepository userFileCollaboratorRepository) {
        this.userFileRepository = userFileRepository;
        this.userFileCollaboratorRepository = userFileCollaboratorRepository;
    }

    public Optional<UserFile> getUserFileByUuid(String uuid) {
        return userFileRepository.findById(uuid);
    }

    public Optional<UserFile> getUserFileByUuidAndAccountUuid(String fileUuid, String accountUuid) {
        return userFileRepository.findByUuidAndAccountUuid(fileUuid, accountUuid);
    }

    public List<UserFile> getUserFiles() {
        return userFileRepository.findAll();
    }

    public List<UserFile> getUserFilesOfAccount(String accountUuid) {
        return userFileRepository.findByAccountUuid(accountUuid);
    }

    public UserFile createUserFile(String publicName, String privateName, Account account) {
        UserFile userFile = new UserFile();
        userFile.setFileNamePublic(publicName);
        userFile.setFileNamePrivate(privateName);
        userFile.setAccount(account);

        return userFileRepository.save(userFile);
    }

    public Optional<UserFile> updateUserFile(String userFileUuid, String name, String fileUrl, Boolean isTreated) {
        Optional<UserFile> optionalUserFile = getUserFileByUuid(userFileUuid);
        if (optionalUserFile.isEmpty()) {
            return Optional.empty();
        }
        UserFile userFile = optionalUserFile.get();
        userFile.setFileNamePublic(name);
        userFile.setIsTreated(isTreated);

        return Optional.of(userFileRepository.save(userFile));
    }

    public Optional<UserFile> setUserFileAsTreated(String userFileUuid) {
        Optional<UserFile> optionalUserFile = getUserFileByUuid(userFileUuid);
        if (optionalUserFile.isEmpty()) {
            return Optional.empty();
        }
        UserFile userFile = optionalUserFile.get();
        userFile.setIsTreated(true);

        return Optional.of(userFileRepository.save(userFile));
    }

    @Transactional
    public Optional<UserFile> addCollaborators(UserFile userFile, List<Account> collaborators, ConfirmationCollaboratorToken confirmationCollaboratorToken) {

        collaborators.forEach(account -> {
            UserFileCollaborator userFileCollaborator = new UserFileCollaborator()
                    .setUserFile(userFile)
                    .setAccount(account)
                    .setConfirmationCollaboratorToken(confirmationCollaboratorToken);
            userFileCollaborator = userFileCollaboratorRepository.save(userFileCollaborator);
            userFile.getUserFileCollaborator().add(userFileCollaborator);
        });

        return Optional.of(userFileRepository.save(userFile));
    }

    public void deleteUserFile(String userFileUuid) {
        Optional<UserFile> optionalUserFile = getUserFileByUuid(userFileUuid);
        if (optionalUserFile.isEmpty()) {
            return;
        }
        UserFile userFile = optionalUserFile.get();
        userFileRepository.delete(userFile);
    }

    @Transactional
    public Optional<UserFile> removeCollaborators(String fileUuid, String accountUuid, List<Account> collaborators) {
        Optional<UserFile> optionalUserFile = userFileRepository.findByUuidAndAccountUuid(fileUuid, accountUuid);
        if (optionalUserFile.isEmpty()) {
            return Optional.empty();
        }
        UserFile userFile = optionalUserFile.get();
        List<UserFileCollaborator> userFileCollaborator = userFile.getUserFileCollaborator();
        List<UserFileCollaborator> userFileCollaboratorsToRemove = userFileCollaborator
                .stream()
                .filter(fileCollaborator ->
                        collaborators.contains(fileCollaborator.getAccount()))
                .collect(Collectors.toList());

        userFileCollaboratorRepository.deleteAll(userFileCollaboratorsToRemove);

        return Optional.of(userFile);
    }
}
