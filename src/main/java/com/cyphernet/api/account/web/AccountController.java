package com.cyphernet.api.account.web;

import com.cyphernet.api.account.model.*;
import com.cyphernet.api.account.service.AccountService;
import com.cyphernet.api.exception.AccountNotFoundException;
import com.cyphernet.api.storage.model.UserFile;
import com.cyphernet.api.storage.model.UserFileDTO;
import com.cyphernet.api.storage.service.UserFileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;
import static org.springframework.http.ResponseEntity.*;


@RestController
@RequestMapping("/api/account")
public class AccountController {
    private final AccountService accountService;
    private final UserFileService userFileService;

    @Autowired
    public AccountController(AccountService accountService, UserFileService userFileService) {
        this.accountService = accountService;
        this.userFileService = userFileService;
    }

    @GetMapping("/{accountUuid}")
    public ResponseEntity<AccountDTO> getUser(@PathVariable String accountUuid) {
        Account account = accountService.getAccountByUuid(accountUuid)
                .orElseThrow(() -> new AccountNotFoundException("uuid", accountUuid));
        return ok(account.toDTO());
    }

    @GetMapping
    public ResponseEntity<List<AccountDTO>> getAccounts() {
        List<AccountDTO> accounts = accountService.getAccounts()
                .stream()
                .map(Account::toDTO)
                .collect(toList());
        return ok(accounts);
    }

    @GetMapping("/files/{accountUuid}")
    public ResponseEntity<List<UserFileDTO>> getFiles(@PathVariable String accountUuid) {
        List<UserFileDTO> userFiles = userFileService.getUserFilesOfAccount(accountUuid)
                .stream()
                .map(UserFile::toDTO)
                .collect(Collectors.toList());
        return ok(userFiles);
    }

    @PostMapping
    public ResponseEntity<Void> createAccount(@Valid @RequestBody AccountCreationDTO accountDTO, UriComponentsBuilder uriBuilder) {
        Account account = accountService.createAccount(accountDTO.getEmail(), accountDTO.getUsername(), accountDTO.getPassword());

        URI uri = uriBuilder.path("/user/{accountUuid}").buildAndExpand(account.getUuid()).toUri();

        return created(uri).build();
    }

    @PutMapping("/{accountUuid}")
    public ResponseEntity<AccountDTO> updateAccount(@PathVariable String accountUuid, @Valid @RequestBody AccountUpdateDTO accountDTO) {
        Account account = accountService.updateAccount(accountUuid, accountDTO.getEmail(), accountDTO.getUsername())
                .orElseThrow(() -> new AccountNotFoundException("uuid", accountUuid));
        return ok(account.toDTO());
    }

    @PutMapping("/password/{accountUuid}")
    public ResponseEntity<Void> updatePassword(@PathVariable String accountUuid, @RequestBody AccountPasswordDTO accountPasswordDTO) {
        accountService.updatePassword(accountUuid, accountPasswordDTO.getPassword())
                .orElseThrow(() -> new AccountNotFoundException("uuid", accountUuid));

        return noContent().build();
    }

    @DeleteMapping("/{accountUuid}")
    public ResponseEntity<Void> deleteAccount(@PathVariable String accountUuid) {
        Account account = accountService.getAccountByUuid(accountUuid)
                .orElseThrow(() -> new AccountNotFoundException("uuid", accountUuid));

        accountService.deleteAccount(account);

        return noContent().build();
    }
}
