package com.cyphernet.api;

import com.cyphernet.api.account.service.AccountService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
class BootstrapAccounts {

    private final AccountService accountService;

    @Value("${security.admin.password}")
    private String password;

    BootstrapAccounts(AccountService accountService) {
        this.accountService = accountService;
    }

    @EventListener
    public void onStartup(ApplicationReadyEvent event) {
        if (accountService.getAccountByUsername("admin").isPresent()) {
            return;
        }

        accountService.createAccount("admin@mail.com", "admin", password);
        log.trace("Admin account created.");
    }
}
