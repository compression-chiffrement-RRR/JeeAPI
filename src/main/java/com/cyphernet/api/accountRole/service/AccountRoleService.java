package com.cyphernet.api.accountRole.service;

import com.cyphernet.api.accountRole.model.Role;
import com.cyphernet.api.accountRole.repository.AccountRoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AccountRoleService {
    private final AccountRoleRepository accountRoleRepository;

    @Autowired
    AccountRoleService(AccountRoleRepository accountRoleRepository) {
        this.accountRoleRepository = accountRoleRepository;
    }

    public List<Role> getAccountRoles() {
        return accountRoleRepository.findAll();
    }

    public Optional<Role> getAccountRoleByName(String name) {
        return accountRoleRepository.findByName(name);
    }

    public Role createAccountRole(String name) {
        Role role = new Role();
        role.setName(name);
        return accountRoleRepository.save(role);
    }
}
