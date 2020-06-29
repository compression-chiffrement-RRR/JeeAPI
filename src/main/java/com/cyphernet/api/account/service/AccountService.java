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
        Account user = new Account();
        user.setEmail(email);
        user.setUsername(username);
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        user.setPassword(passwordEncoder.encode(password));
        return accountRepository.save(user);
    }

    public Optional<Account> addRole(String uuid, AccountRole role) {
        Optional<Account> optionalAccount = getAccountByUuid(uuid);
        if (optionalAccount.isEmpty()) {
            return Optional.empty();
        }

        Account user = optionalAccount.get();
        user.addRole(role);

        return Optional.of(accountRepository.save(user));
    }

    public Optional<Account> removeRole(String uuid, AccountRole role) {
        Optional<Account> optionalAccount = getAccountByUuid(uuid);
        if (optionalAccount.isEmpty()) {
            return Optional.empty();
        }

        Account user = optionalAccount.get();
        user.removeRole(role);

        return Optional.of(accountRepository.save(user));
    }

    /*public Optional<Account> addFriend(String uuid, String uuidFriend) {
        Optional<Account> optionalAccount = getAccountByUuid(uuid);
        Optional<Account> optionalAccountFriend = getAccountByUuid(uuid);
        if (optionalAccount.isEmpty() || optionalAccountFriend.isEmpty()) {
            return Optional.empty();
        }

        Account user = optionalAccount.get();
        Account userFriend = optionalAccountFriend.get();
        user.getFriends().add(userFriend);

        return Optional.of(accountRepository.save(user));
    }*/

    public Optional<Account> updateAccount(String uuid, String email, String username, String password) {
        Optional<Account> optionalUser = getAccountByUuid(uuid);
        if (optionalUser.isEmpty()) {
            return Optional.empty();
        }
        Account user = optionalUser.get();
        user.setEmail(email);
        user.setUsername(username);
        user.setPassword(password);
        return Optional.of(accountRepository.save(user));
    }

    public void deleteAccount(Account account) {
        accountRepository.delete(account);
    }
}
