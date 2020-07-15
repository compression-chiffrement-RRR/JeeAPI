package com.cyphernet.api.accountFriend.model;

import com.cyphernet.api.account.model.Account;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.util.Date;

@Setter
@Getter
@Accessors(chain = true)
@Entity
@Table(name = "account_friends")
@EntityListeners(AuditingEntityListener.class)
public class AccountFriend {
    @Id
    @GeneratedValue
    private int accountFriendId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "accountUuid", nullable = false)
    private Account account;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "friendUuid", nullable = false)
    private Account friend;

    @Column
    private Boolean pending = true;

    @Column
    private Boolean ignore = false;

    @Column
    private Boolean deleted = false;

    @Column(nullable = false, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    @CreatedDate
    private Date createdAt;

    @Column(nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    @LastModifiedDate
    private Date updatedAt;
}
