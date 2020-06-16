package com.cyphernet.api.accountRole.service;

import com.cyphernet.api.accountRole.model.AccountRole;
import com.cyphernet.api.accountRole.repository.AccountRoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AccountRoleService {
    private AccountRoleRepository accountRoleRepository;

    @Autowired
    AccountRoleService(AccountRoleRepository accountRoleRepository) {
        this.accountRoleRepository = accountRoleRepository;
    }

    public List<AccountRole> getAccountRoles() {
        return accountRoleRepository.findAll();
    }

    public Optional<AccountRole> getAccountRoleByName(String name) {
        return accountRoleRepository.findByName(name);
    }

    public AccountRole createAccountRole(String name) {
        AccountRole accountRole = new AccountRole();
        accountRole.setName(name);
        return accountRoleRepository.save(accountRole);
    }
}
