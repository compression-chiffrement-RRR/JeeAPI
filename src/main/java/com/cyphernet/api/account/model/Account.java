package com.cyphernet.api.account.model;

import com.cyphernet.api.storage.model.UserFile;
import com.cyphernet.api.accountRole.model.AccountRole;
import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.Data;
import lombok.experimental.Accessors;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Data
@Entity
@Accessors(chain = true)
@EntityListeners(AuditingEntityListener.class)
public class Account {
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
            name = "UUID",
            strategy = "org.hibernate.id.UUIDGenerator"
    )
    @Column(updatable = false, nullable = false)
    private String uuid;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.MERGE)
    @JoinTable()
    private Set<AccountRole> accountRoles = new HashSet<>();

    @JsonBackReference
    public Set<AccountRole> getAccountRoles(){
        return this.accountRoles;
    }

    @OneToMany(
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            mappedBy = "account"
    )
    private List<UserFile> userFiles;

    @Column(nullable = false, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    @CreatedDate
    private Date createdAt;

    @Column(nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    @LastModifiedDate
    private Date updatedAt;

    public void addRole(AccountRole role) {
        this.getAccountRoles().add(role);
        role.getAccounts().add(this);
    }

    public void removeRole(AccountRole role) {
        this.getAccountRoles().remove(role);
        role.getAccounts().remove(this);
    }

    public List<String> rolesToString(){
        return this.accountRoles.stream().map(role -> role.getName()).collect(Collectors.toList());
    }

    public AccountDTO toDTO() {
        return new AccountDTO()
                .setUuid(this.uuid)
                .setEmail(this.email)
                .setUsername(this.username)
                .setPassword(this.password);
    }
}
