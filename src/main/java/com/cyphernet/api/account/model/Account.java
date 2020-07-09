package com.cyphernet.api.account.model;

import com.cyphernet.api.accountFriend.model.AccountFriend;
import com.cyphernet.api.accountRole.model.Role;
import com.cyphernet.api.mail.model.ConfirmationCollaboratorToken;
import com.cyphernet.api.storage.model.UserFile;
import com.cyphernet.api.storage.model.UserFileCollaborator;
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

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password;

    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinTable
    private Set<Role> roles = new HashSet<>();

    @OneToMany(
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            mappedBy = "account"
    )
    private List<ConfirmationCollaboratorToken> confirmationCollaboratorTokens;

    @OneToMany(
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            mappedBy = "account"
    )
    private List<UserFile> userFiles;

    @OneToMany(mappedBy = "account")
    private List<UserFileCollaborator> userFileCollaborator;

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
    public Set<Role> getRoles() {
        return this.roles;
    }

    public void addRole(Role role) {
        this.getRoles().add(role);
        role.getAccounts().add(this);
    }

    public void removeRole(Role role) {
        this.getRoles().remove(role);
        role.getAccounts().remove(this);
    }

    public void addAccountFriends(AccountFriend accountFriend) {
        if (CollectionUtils.isEmpty(this.accountFriends)) {
            this.accountFriends = new ArrayList<>();
        }
        this.accountFriends.add(accountFriend);
    }

    public List<String> rolesToString() {
        return this.roles.stream().map(Role::getName).collect(Collectors.toList());
    }

    public AccountDTO toDTO() {
        return new AccountDTO()
                .setUuid(this.uuid)
                .setEmail(this.email)
                .setUsername(this.username);
    }
}
