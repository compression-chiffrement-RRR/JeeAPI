package com.cyphernet.api.account.model;

import com.cyphernet.api.accountFriend.model.AccountFriend;
import com.cyphernet.api.accountRole.model.AccountRole;
import com.cyphernet.api.storage.model.UserFile;
import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.Data;
import lombok.experimental.Accessors;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.util.CollectionUtils;

import javax.persistence.*;
import java.util.*;
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
    @JoinTable
    private Set<AccountRole> accountRoles = new HashSet<>();

    @OneToMany(
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            mappedBy = "account"
    )
    private List<UserFile> userFiles;

    @ManyToMany
    @JoinTable(name = "account_friends", joinColumns = @JoinColumn(name = "accountUuid"), inverseJoinColumns = @JoinColumn(name = "friendUuid"))
    private List<AccountFriend> accountFriends;

    @Column(nullable = false, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    @CreatedDate
    private Date createdAt;

    @Column(nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    @LastModifiedDate
    private Date updatedAt;

    @JsonBackReference
    public Set<AccountRole> getAccountRoles() {
        return this.accountRoles;
    }

    public void addRole(AccountRole role) {
        this.getAccountRoles().add(role);
        role.getAccounts().add(this);
    }

    public void removeRole(AccountRole role) {
        this.getAccountRoles().remove(role);
        role.getAccounts().remove(this);
    }

    public void addAccountFriends(AccountFriend accountFriend) {
        if (CollectionUtils.isEmpty(this.accountFriends)) {
            this.accountFriends = new ArrayList<>();
        }
        this.accountFriends.add(accountFriend);
    }

    public List<String> rolesToString() {
        return this.accountRoles.stream().map(AccountRole::getName).collect(Collectors.toList());
    }

    public AccountDTO toDTO() {
        return new AccountDTO()
                .setUuid(this.uuid)
                .setEmail(this.email)
                .setUsername(this.username);
    }
}
