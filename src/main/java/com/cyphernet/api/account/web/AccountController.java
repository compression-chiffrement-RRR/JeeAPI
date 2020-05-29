package com.cyphernet.api.account.web;

import com.cyphernet.api.account.model.Account;
import com.cyphernet.api.account.model.AccountDTO;
import com.cyphernet.api.account.service.AccountService;
import com.cyphernet.api.exception.AccountNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;
import java.util.List;

import static java.util.stream.Collectors.toList;
import static org.springframework.http.ResponseEntity.created;
import static org.springframework.http.ResponseEntity.ok;


@RestController
@RequestMapping("/account")
public class AccountController {
    private final AccountService accountService;

    @Autowired
    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @GetMapping("/{accountUuid}")
    public ResponseEntity<AccountDTO> getUser(@PathVariable String accountUuid) {
        Account account = accountService.getAccountByUuid(accountUuid)
                .orElseThrow(() -> new AccountNotFoundException("uuid", accountUuid));
        return ok(account.toDTO());
    }

    @GetMapping
    public List<AccountDTO> getAccounts() {
        return accountService.getAccounts()
                .stream()
                .map(Account::toDTO)
                .collect(toList());
    }

    @PostMapping
    public ResponseEntity<?> createAccount(@Valid @RequestBody AccountDTO accountDTO, UriComponentsBuilder uriBuilder) {

        Account account = accountService.createAccount(accountDTO.getEmail(), accountDTO.getUsername(), accountDTO.getPassword());

        URI uri = uriBuilder.path("/user/{accountUuid}").buildAndExpand(account.getUuid()).toUri();

        return created(uri).build();
    }

    @PutMapping("/{accountUuid}")
    public ResponseEntity<AccountDTO> updateAccount(@PathVariable String accountUuid, @Valid @RequestBody AccountDTO accountDTO) {
        Account account = accountService.updateAccount(accountUuid, accountDTO.getEmail(), accountDTO.getUsername(), accountDTO.getPassword())
                .orElseThrow(() -> new AccountNotFoundException("uuid", accountUuid));
        return ok(account.toDTO());
    }

    @DeleteMapping("/{accountUuid}")
    public ResponseEntity<AccountDTO> deleteAccount(@PathVariable String accountUuid) {
        Account account = accountService.getAccountByUuid(accountUuid)
                .orElseThrow(() -> new AccountNotFoundException("uuid", accountUuid));

        accountService.deleteAccount(account);

        return ok().build();
    }
}
