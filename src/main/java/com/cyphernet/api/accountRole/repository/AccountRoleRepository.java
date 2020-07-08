package com.cyphernet.api.accountRole.repository;

import com.cyphernet.api.accountRole.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AccountRoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(String name);
}
