package com.cyphernet.api;

import com.cyphernet.api.account.model.Account;
import com.cyphernet.api.account.repository.AccountRepository;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
class BootstrapAccounts {

    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;

    BootstrapAccounts(AccountRepository accountRepository, PasswordEncoder passwordEncoder) {
        this.accountRepository = accountRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /*@EventListener
    public void onStartup(ApplicationReadyEvent event) {
        List.of(
                new Account().setEmail("under@test.fr").setUsername("under").setPassword(passwordEncoder.encode("test")).setRoles(List.of("USER")),
                new Account().setEmail("admin@admintest.fr").setUsername("admin").setPassword(passwordEncoder.encode("admintest")).setRoles(List.of("USER", "ADMIN"))
        ).forEach(accountRepository::save);
    }*/
}
