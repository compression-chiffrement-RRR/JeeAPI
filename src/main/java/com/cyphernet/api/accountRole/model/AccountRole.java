package com.cyphernet.api.accountRole.model;

import com.cyphernet.api.account.model.Account;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Data
@Entity
@EntityListeners(AuditingEntityListener.class)
public class AccountRole {
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
            name = "UUID",
            strategy = "org.hibernate.id.UUIDGenerator"
    )
    @Column(updatable = false, nullable = false)
    private String uuid;

    @Column(nullable = false)
    private String name;

    @ManyToMany(mappedBy = "accountRoles", fetch = FetchType.EAGER, cascade = CascadeType.MERGE)
    private Set<Account> accounts = new HashSet<>();

    @JsonManagedReference
    public Set<Account> getAccounts() {
        return this.accounts;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AccountRole accountRole = (AccountRole) o;
        return Objects.equals(name, accountRole.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

}
