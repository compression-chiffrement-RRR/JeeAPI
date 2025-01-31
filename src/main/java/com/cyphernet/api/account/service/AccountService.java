package com.cyphernet.api.account.service;

import com.cyphernet.api.account.model.Account;
import com.cyphernet.api.account.model.AccountDetail;
import com.cyphernet.api.account.repository.AccountRepository;
import com.cyphernet.api.accountRole.model.Role;
import com.cyphernet.api.exception.AccountNotFoundException;
import com.cyphernet.api.storage.model.UserFile;
import com.cyphernet.api.storage.model.UserFileCollaborator;
import com.cyphernet.api.storage.model.UserFileDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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

    public Optional<Account> addRole(String uuid, Role role) {
        Optional<Account> optionalAccount = getAccountByUuid(uuid);
        if (optionalAccount.isEmpty()) {
            return Optional.empty();
        }

        Account account = optionalAccount.get();
        account.addRole(role);

        return Optional.of(accountRepository.save(account));
    }

    public Optional<Account> removeRole(String uuid, Role role) {
        Optional<Account> optionalAccount = getAccountByUuid(uuid);
        if (optionalAccount.isEmpty()) {
            return Optional.empty();
        }

        Account account = optionalAccount.get();
        account.removeRole(role);

        return Optional.of(accountRepository.save(account));
    }

    @Transactional
    public List<UserFileDTO> getFilesAuthorDTO(String accountUuid) {
        return this.getFilesAuthor(accountUuid)
                .orElseThrow(() -> new AccountNotFoundException("uuid", accountUuid))
                .stream()
                .map(UserFile::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public List<UserFileDTO> getFilesCollaboratorDTO(String accountUuid) {
        return this.getFilesCollaborator(accountUuid)
                .orElseThrow(() -> new AccountNotFoundException("uuid", accountUuid))
                .stream()
                .map(UserFile::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public Optional<List<UserFile>> getFilesAuthor(String accountUuid) {
        Optional<Account> optionalUser = accountRepository.findByUuid(accountUuid);
        if (optionalUser.isEmpty()) {
            return Optional.empty();
        }
        Account account = optionalUser.get();
        return Optional.of(account.getUserFiles());
    }

    @Transactional
    public Optional<List<UserFile>> getFilesCollaborator(String accountUuid) {
        Optional<Account> optionalUser = accountRepository.findByUuid(accountUuid);
        if (optionalUser.isEmpty()) {
            return Optional.empty();
        }
        Account account = optionalUser.get();

        List<UserFileCollaborator> userFileCollaborator = account.getUserFileCollaborator().stream().filter(fileCollaboNazi -> !fileCollaboNazi.getPending()).collect(Collectors.toList());

        List<UserFile> userFiles = userFileCollaborator.stream().map(UserFileCollaborator::getUserFile).collect(Collectors.toList());

        return Optional.of(userFiles);
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
