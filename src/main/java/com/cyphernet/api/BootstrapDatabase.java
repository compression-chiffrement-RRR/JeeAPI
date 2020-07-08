package com.cyphernet.api;

import com.cyphernet.api.account.model.Account;
import com.cyphernet.api.account.service.AccountService;
import com.cyphernet.api.accountRole.model.AccountRoleType;
import com.cyphernet.api.accountRole.model.Role;
import com.cyphernet.api.accountRole.service.AccountRoleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Slf4j
@Component
class BootstrapDatabase {

    private final AccountService accountService;
    private final AccountRoleService accountRoleService;

    @Value("${security.admin.password}")
    private String password;

    BootstrapDatabase(AccountService accountService, AccountRoleService accountRoleService) {
        this.accountService = accountService;
        this.accountRoleService = accountRoleService;
    }

    @EventListener
    public void onStartup(ApplicationReadyEvent event) {
        Optional<Account> adminOptional = accountService.getAccountByUsername("admin");
        Account admin;
        if (adminOptional.isEmpty()) {
            admin = accountService.createAccount("admin@mail.com", "admin", password);
        } else {
            admin = adminOptional.get();
        }

        if (accountRoleService.getAccountRoleByName(AccountRoleType.ADMIN.name()).isEmpty()) {
            Role role = accountRoleService.createAccountRole(AccountRoleType.ADMIN.name());
            accountService.addRole(admin.getUuid(), role);
        }

        if (accountRoleService.getAccountRoleByName(AccountRoleType.USER.name()).isEmpty()) {
            Role role = accountRoleService.createAccountRole(AccountRoleType.USER.name());
            accountService.addRole(admin.getUuid(), role);
        }

        log.trace("Database initialized");
    }
}
