package com.cyphernet.api.storage.web;

import com.cyphernet.api.account.model.Account;
import com.cyphernet.api.account.model.AccountDetail;
import com.cyphernet.api.account.service.AccountService;
import com.cyphernet.api.exception.AccountNotFoundException;
import com.cyphernet.api.exception.UserFileNotFoundException;
import com.cyphernet.api.storage.model.UserFile;
import com.cyphernet.api.storage.model.UserFileCollaboratorDTO;
import com.cyphernet.api.storage.service.UserFileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.http.ResponseEntity.ok;

@RestController
@RequestMapping("/api/file/share")
public class UserFileShareController {
    private final UserFileService userFileService;
    private final AccountService accountService;

    @Autowired
    public UserFileShareController(UserFileService userFileService, AccountService accountService) {
        this.userFileService = userFileService;
        this.accountService = accountService;
    }

    @Secured("ROLE_USER")
    @PostMapping("/{fileUuid}")
    public ResponseEntity<UserFile> addCollaborators(@PathVariable String fileUuid, @RequestBody UserFileCollaboratorDTO fileCollaboratorDTO, @AuthenticationPrincipal AccountDetail currentAccount) {
        //TODO: failed to lazily initialize a collection of role: com.cyphernet.api.storage.model.UserFile.collaborators, could not initialize proxy - no Session
        List<Account> collaborators = new ArrayList<>();

        fileCollaboratorDTO.getCollaboratorsUuid().forEach(accountUuid -> {
            Account account = accountService.getAccountByUuid(accountUuid)
                    .orElseThrow(() -> new AccountNotFoundException("uuid", accountUuid));
            collaborators.add(account);
        });

        UserFile userFile = userFileService.getUserFileByUuidAndAccountUuid(fileUuid, currentAccount.getUuid())
                .orElseThrow(() -> new UserFileNotFoundException("uuid", fileUuid));

        userFile = userFileService.addCollaborators(userFile.getUuid(), currentAccount.getUuid(), collaborators)
                .orElseThrow(() -> new UserFileNotFoundException("uuid", fileUuid));

        return ok(userFile);
    }

    @Secured("ROLE_USER")
    @DeleteMapping("/{fileUuid}")
    public ResponseEntity<UserFile> removeCollaborators(@PathVariable String fileUuid, @RequestBody UserFileCollaboratorDTO fileCollaboratorDTO, @AuthenticationPrincipal AccountDetail currentAccount) {
        List<Account> collaborators = new ArrayList<>();

        fileCollaboratorDTO.getCollaboratorsUuid().forEach(accountUuid -> {
            Account account = accountService.getAccountByUuid(accountUuid)
                    .orElseThrow(() -> new AccountNotFoundException("uuid", accountUuid));
            collaborators.add(account);
        });

        UserFile userFile = userFileService.getUserFileByUuidAndAccountUuid(fileUuid, currentAccount.getUuid())
                .orElseThrow(() -> new UserFileNotFoundException("uuid", fileUuid));

        userFile = userFileService.removeCollaborators(userFile.getUuid(), currentAccount.getUuid(), collaborators)
                .orElseThrow(() -> new UserFileNotFoundException("uuid", fileUuid));

        return ok(userFile);
    }
}
