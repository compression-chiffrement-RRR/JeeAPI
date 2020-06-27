package com.cyphernet.api.storage.web;

import com.cyphernet.api.storage.model.UserFile;
import com.cyphernet.api.storage.model.UserFileDTO;
import com.cyphernet.api.storage.service.UserFileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.http.ResponseEntity.ok;

@RestController
@RequestMapping("/api/file")
public class UserFileController {
    private final UserFileService userFileService;

    @Autowired
    public UserFileController(UserFileService userFileService) {
        this.userFileService = userFileService;
    }

    @GetMapping("/{accountUuid}")
    public ResponseEntity<List<UserFileDTO>> getFiles(@PathVariable String accountUuid) {
        List<UserFileDTO> userFiles = userFileService.getUserFilesOfAccount(accountUuid)
                .stream()
                .map(UserFile::toDTO)
                .collect(Collectors.toList());
        return ok(userFiles);
    }
}
