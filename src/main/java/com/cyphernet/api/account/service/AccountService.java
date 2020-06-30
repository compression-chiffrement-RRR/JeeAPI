package com.cyphernet.api.account.service;

import com.cyphernet.api.account.model.Account;
import com.cyphernet.api.account.model.AccountDetail;
import com.cyphernet.api.account.repository.AccountRepository;
import com.cyphernet.api.accountRole.model.AccountRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AccountService implements UserDetailsService {

    private final AccountRepository accountRepository;

    @Autowired
    AccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<Account> account = accountRepository.findByUsername(username);
        if (account.isEmpty()) {
            throw new UsernameNotFoundException("account not found");
        }
        return new AccountDetail(account.get());
    }

    public Optional<Account> getAccountByUuid(String uuid) {
        return accountRepository.findByUuid(uuid);
    }

    public Optional<Account> getAccountByUsername(String username) {
        return accountRepository.findByUsername(username);
    }

    public List<Account> getAccounts() {
        return accountRepository.findAll();
    }

    public Account createAccount(String email, String username, String password) {
        Account account = new Account();
        account.setEmail(email);
        account.setUsername(username);
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        account.setPassword(passwordEncoder.encode(password));
        return accountRepository.save(account);
    }

    public Optional<Account> addRole(String uuid, AccountRole role) {
        Optional<Account> optionalAccount = getAccountByUuid(uuid);
        if (optionalAccount.isEmpty()) {
            return Optional.empty();
        }

        Account account = optionalAccount.get();
        account.addRole(role);

        return Optional.of(accountRepository.save(account));
    }

    public Optional<Account> removeRole(String uuid, AccountRole role) {
        Optional<Account> optionalAccount = getAccountByUuid(uuid);
        if (optionalAccount.isEmpty()) {
            return Optional.empty();
        }

        Account account = optionalAccount.get();
        account.removeRole(role);

        return Optional.of(accountRepository.save(account));
    }

    public Optional<Account> updateAccount(String uuid, String email, String username) {
        Optional<Account> optionalUser = getAccountByUuid(uuid);
        if (optionalUser.isEmpty()) {
            return Optional.empty();
        }
        Account account = optionalUser.get();
        account.setEmail(email);
        account.setUsername(username);
        return Optional.of(accountRepository.save(account));
    }

    public Optional<Account> updatePassword(String accountUuid, String password) {
        Optional<Account> optionalAccount = this.getAccountByUuid(accountUuid);
        if (optionalAccount.isEmpty()) {
            return Optional.empty();
        }

        Account account = optionalAccount.get();

        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        account.setPassword(passwordEncoder.encode(password));

        accountRepository.save(account);

        return Optional.of(accountRepository.save(account));
    }

    public void deleteAccount(Account account) {
        accountRepository.delete(account);
    }
}
