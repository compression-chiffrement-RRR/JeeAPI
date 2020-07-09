package com.cyphernet.api.storage.model;

import com.cyphernet.api.account.model.Account;
import lombok.Data;
import lombok.experimental.Accessors;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
    private String fileNamePublic;

    @Column(nullable = false)
    private String fileNamePrivate;

    @Column(nullable = false)
    private Boolean isTreated = false;

    @OneToMany(
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            mappedBy = "userFile"
    )
    private List<UserFileProcess> fileProcesses = new ArrayList<>();

    @ManyToOne
    private Account account;

    @OneToMany(mappedBy = "userFile")
    private List<UserFileCollaborator> userFileCollaborator;

    /*@ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "file_collaborators", joinColumns = @JoinColumn(name = "fileUuid"), inverseJoinColumns = @JoinColumn(name = "collaboratorUuid"))
    private Set<Account> collaborators = new HashSet<>();*/

    @Column(nullable = false, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    @CreatedDate
    private Date createdAt;

    @Column(nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    @LastModifiedDate
    private Date updatedAt;

    public void addFileProcess(UserFileProcess userFileProcess) {
        this.getFileProcesses().add(userFileProcess);
        userFileProcess.setUserFile(this);
    }

    public UserFileDTO toDTO() {
        return new UserFileDTO()
                .setUuid(this.uuid)
                .setName(this.fileNamePublic)
                .setIsTreated(this.isTreated)
                .setCreationDate(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX").format(this.createdAt));
    }
}
