package com.cyphernet.api.storage.model;

import com.cyphernet.api.account.model.Account;
import com.cyphernet.api.mail.model.ConfirmationCollaboratorToken;
import lombok.Data;
import lombok.experimental.Accessors;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.text.SimpleDateFormat;
import java.util.Date;

@Data
@Entity
@Accessors(chain = true)
@EntityListeners(AuditingEntityListener.class)
public class UserFileCollaborator {
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
            name = "UUID",
            strategy = "org.hibernate.id.UUIDGenerator"
    )
    @Column(updatable = false, nullable = false)
    private String uuid;

    @ManyToOne
    @JoinColumn(name = "userFileUuid", nullable = false)
    private UserFile userFile;

    @ManyToOne
    @JoinColumn(name = "accountUuid", nullable = false)
    private Account account;

    @Column
    private Boolean pending = true;

    @ManyToOne(optional = false)
    @JoinColumn(name = "confirmationCollaboratorTokenUuid", nullable = false)
    private ConfirmationCollaboratorToken confirmationCollaboratorToken;

    @Column(nullable = false, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    @CreatedDate
    private Date createdAt;

    @Column(nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    @LastModifiedDate
    private Date updatedAt;

    public UserFileCollaboratorDTO toDTO() {
        return new UserFileCollaboratorDTO()
                .setUserFileUuid(this.userFile.getUuid())
                .setAccount(this.account.toDTO())
                .setPending(this.pending)
                .setCreationDate(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX").format(this.createdAt));
    }
}
