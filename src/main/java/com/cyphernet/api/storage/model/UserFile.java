package com.cyphernet.api.storage.model;

import com.cyphernet.api.account.model.Account;
import lombok.Data;
import lombok.experimental.Accessors;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

@Data
@Entity
@Accessors(chain = true)
@EntityListeners(AuditingEntityListener.class)
public class UserFile {
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
            name = "UUID",
            strategy = "org.hibernate.id.UUIDGenerator"
    )
    @Column(updatable = false, nullable = false)
    private String uuid;

    @Column(nullable = false)
    private String fileName;

    @Column
    private String fileUrl;

    @Column(nullable = false)
    private Boolean isTreated = false;

    @ManyToOne
    private Account account;

    @Column(nullable = false, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    @CreatedDate
    private Date createdAt;

    @Column(nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    @LastModifiedDate
    private Date updatedAt;

    public UserFileDTO toDTO() {
        return new UserFileDTO()
                .setUuid(this.uuid)
                .setName(this.fileName)
                .setIsTreated(this.isTreated)
                .setUrl(this.fileUrl)
                .setCreationDate(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX").format(this.createdAt));
    }
}
