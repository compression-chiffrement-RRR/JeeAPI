package com.cyphernet.api.storage.service;

import com.cyphernet.api.account.model.Account;
import com.cyphernet.api.storage.model.UserFile;
import com.cyphernet.api.storage.repository.UserFileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserFileService {
    private final UserFileRepository userFileRepository;

    @Autowired
    public UserFileService(UserFileRepository userFileRepository) {
        this.userFileRepository = userFileRepository;
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

    public Optional<UserFile> addCollaborators(String fileUuid, String accountUuid, List<Account> collaborators) {
        Optional<UserFile> optionalUserFile = userFileRepository.findByUuidAndAccountUuid(fileUuid, accountUuid);
        if (optionalUserFile.isEmpty()) {
            return Optional.empty();
        }
        UserFile userFile = optionalUserFile.get();
        userFile.getCollaborators().addAll(collaborators);

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

    public Optional<UserFile> removeCollaborators(String fileUuid, String accountUuid, List<Account> collaborators) {
        Optional<UserFile> optionalUserFile = userFileRepository.findByUuidAndAccountUuid(fileUuid, accountUuid);
        if (optionalUserFile.isEmpty()) {
            return Optional.empty();
        }
        UserFile userFile = optionalUserFile.get();
        userFile.getCollaborators().removeAll(collaborators);

        return Optional.of(userFileRepository.save(userFile));
    }
}
