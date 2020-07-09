package com.cyphernet.api.mail.model;

import com.cyphernet.api.account.model.Account;
import com.cyphernet.api.storage.model.UserFileCollaborator;
import lombok.Data;
import lombok.experimental.Accessors;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.util.Date;
import java.util.Set;

@Data
@Entity
@Accessors(chain = true)
@EntityListeners(AuditingEntityListener.class)
public class ConfirmationCollaboratorToken {
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
            name = "UUID",
            strategy = "org.hibernate.id.UUIDGenerator"
    )
    @Column(updatable = false, nullable = false)
    private String uuid;

    @Column(nullable = false, updatable = false)
    private String confirmationToken;

    @Column(nullable = false)
    private Boolean validated = false;

    @ManyToOne
    private Account account;

    @OneToMany(mappedBy = "confirmationCollaboratorToken")
    private Set<UserFileCollaborator> userFileCollaborators;

    @Column(nullable = false, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    @CreatedDate
    private Date createdAt;
}
