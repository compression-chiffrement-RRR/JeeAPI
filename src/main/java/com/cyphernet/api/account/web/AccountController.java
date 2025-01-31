package com.cyphernet.api.account.web;

import com.cyphernet.api.account.model.*;
import com.cyphernet.api.account.service.AccountService;
import com.cyphernet.api.accountRole.model.AccountRoleType;
import com.cyphernet.api.accountRole.model.Role;
import com.cyphernet.api.accountRole.service.AccountRoleService;
import com.cyphernet.api.exception.AccountNotFoundException;
import com.cyphernet.api.exception.CreationException;
import com.cyphernet.api.exception.MissingParamException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;
import java.util.List;

import static java.util.stream.Collectors.toList;
import static org.springframework.http.ResponseEntity.*;


@RestController
@RequestMapping("/api/account")
public class AccountController {
    private final AccountService accountService;
    private final AccountRoleService accountRoleService;

    @Autowired
    public AccountController(AccountService accountService, AccountRoleService accountRoleService) {
        this.accountService = accountService;
        this.accountRoleService = accountRoleService;
    }

    @Secured("ROLE_USER")
    @GetMapping("/me")
    public ResponseEntity<AccountDTO> getSelfUser(@AuthenticationPrincipal AccountDetail userLogged) {
        String accountUuid = userLogged.getUuid();
        Account account = accountService.getAccountByUuid(accountUuid)
                .orElseThrow(() -> new AccountNotFoundException("uuid", accountUuid));
        return ok(account.toDTO());
    }

    @Secured("ROLE_USER")
    @GetMapping
    public ResponseEntity<AccountDTO> getUser(@RequestParam(value = "username", required = false) String username, @RequestParam(value = "uuid", required = false) String uuid) {
        Account account;
        if (uuid != null) {
            account = accountService.getAccountByUuid(uuid)
                    .orElseThrow(() -> new AccountNotFoundException("uuid", uuid));
        } else if (username != null) {
            account = accountService.getAccountByUsername(username)
                    .orElseThrow(() -> new AccountNotFoundException("username", username));
        } else {
            throw new MissingParamException();
        }

        return ok(account.toDTO());
    }

    @Secured("ROLE_ADMIN")
    @GetMapping("/all")
    public ResponseEntity<List<AccountDTO>> getAccounts() {
        List<AccountDTO> accounts = accountService.getAccounts()
                .stream()
                .map(Account::toDTO)
                .collect(toList());
        return ok(accounts);
    }

    @PostMapping
    public ResponseEntity<Void> createAccount(@Valid @RequestBody AccountCreationDTO accountDTO, UriComponentsBuilder uriBuilder) {
        Account account = accountService.createAccount(accountDTO.getEmail(), accountDTO.getUsername(), accountDTO.getPassword());

        Role role = accountRoleService.getAccountRoleByName(AccountRoleType.USER.name())
                .orElseThrow(CreationException::new);

        accountService.addRole(account.getUuid(), role);

        URI uri = uriBuilder.path("/api/account?uuid={accountUuid}").buildAndExpand(account.getUuid()).toUri();

        return created(uri).build();
    }

    @Secured("ROLE_USER")
    @PutMapping
    public ResponseEntity<AccountDTO> updateAccount(@Valid @RequestBody AccountUpdateDTO accountDTO, @AuthenticationPrincipal AccountDetail userLogged) {
        String accountUuid = userLogged.getUuid();
        Account accountUpdated = accountService.updateAccount(accountUuid, accountDTO.getEmail(), accountDTO.getUsername())
                .orElseThrow(() -> new AccountNotFoundException("uuid", accountUuid));
        return ok(accountUpdated.toDTO());
    }

    @Secured("ROLE_USER")
    @PutMapping("/password")
    public ResponseEntity<Void> updatePassword(@RequestBody AccountPasswordDTO accountPasswordDTO, @AuthenticationPrincipal AccountDetail userLogged) {
        String accountUuid = userLogged.getUuid();
        accountService.updatePassword(accountUuid, accountPasswordDTO.getPassword())
                .orElseThrow(() -> new AccountNotFoundException("uuid", accountUuid));

        return noContent().build();
    }

    @Secured("ROLE_USER")
    @DeleteMapping
    public ResponseEntity<Void> deleteAccount(@AuthenticationPrincipal AccountDetail userLogged) {
        String accountUuid = userLogged.getUuid();
        Account account = accountService.getAccountByUuid(accountUuid)
                .orElseThrow(() -> new AccountNotFoundException("uuid", accountUuid));

        accountService.deleteAccount(account);

        return noContent().build();
    }
}
