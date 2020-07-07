package com.cyphernet.api.storage.service;

import com.cyphernet.api.storage.model.UserFile;
import com.cyphernet.api.storage.model.UserFileProcess;
import com.cyphernet.api.storage.repository.UserFileProcessRepository;
import com.cyphernet.api.worker.model.ProcessTaskType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserFileProcessService {
    private final UserFileProcessRepository userFileProcessRepository;

    @Autowired
    public UserFileProcessService(UserFileProcessRepository userFileProcessRepository) {
        this.userFileProcessRepository = userFileProcessRepository;
    }

    public Optional<UserFileProcess> getUserFileProcessByUuid(String uuid) {
        return userFileProcessRepository.findById(uuid);
    }

    public List<UserFileProcess> getUserFiles() {
        return userFileProcessRepository.findAll();
    }

    public UserFileProcess createUserFileProcess(ProcessTaskType processTaskType, Integer order, byte[] salt, byte[] iv, UserFile userFile) {
        UserFileProcess userFileProcess = new UserFileProcess();
        userFileProcess.setProcessTaskType(processTaskType);
        userFileProcess.setProcessOrder(order);
        userFileProcess.setSalt(salt);
        userFileProcess.setIv(iv);
        userFileProcess.setUserFile(userFile);

        return userFileProcessRepository.save(userFileProcess);
    }

    public void deleteUserFileProcess(String userFileProcessUuid) {
        Optional<UserFileProcess> optionalUserFileProcess = getUserFileProcessByUuid(userFileProcessUuid);
        if (optionalUserFileProcess.isEmpty()) {
            return;
        }
        UserFileProcess userFileProcess = optionalUserFileProcess.get();
        userFileProcessRepository.delete(userFileProcess);
    }
}
