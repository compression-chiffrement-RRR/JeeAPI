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

    public List<UserFile> getUserFiles() {
        return userFileRepository.findAll();
    }

    public UserFile createUserFile(String name, Account account) {
        UserFile userFile = new UserFile();
        userFile.setFileName(name);
        userFile.setAccount(account);

        return userFileRepository.save(userFile);
    }

    public Optional<UserFile> updateUserFile(String userFileUuid, String name, String fileUrl, Boolean isTreated) {
        Optional<UserFile> optionalUserFile = getUserFileByUuid(userFileUuid);
        if (optionalUserFile.isEmpty()) {
            return Optional.empty();
        }
        UserFile userFile = optionalUserFile.get();
        userFile.setFileName(name);
        userFile.setFileUrl(fileUrl);
        userFile.setIsTreated(isTreated);

        return Optional.of(userFileRepository.save(userFile));
    }

    public Optional<UserFile> setUserFileAsTreated(String userFileUuid, String fileUrl) {
        Optional<UserFile> optionalUserFile = getUserFileByUuid(userFileUuid);
        if (optionalUserFile.isEmpty()) {
            return Optional.empty();
        }
        UserFile userFile = optionalUserFile.get();
        userFile.setFileUrl(fileUrl);
        userFile.setIsTreated(true);

        return Optional.of(userFileRepository.save(userFile));
    }

    public void deleteUserFile(UserFile campus) {
        userFileRepository.delete(campus);
    }
}
