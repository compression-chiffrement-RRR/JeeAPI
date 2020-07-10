package com.cyphernet.api.storage.model;

import com.cyphernet.api.worker.model.ProcessTaskType;
import lombok.Data;
import lombok.experimental.Accessors;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.util.Date;

@Data
@Entity
@Accessors(chain = true)
@EntityListeners(AuditingEntityListener.class)
public class UserFileProcess implements Comparable<UserFileProcess> {
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
            name = "UUID",
            strategy = "org.hibernate.id.UUIDGenerator"
    )
    @Column(updatable = false, nullable = false)
    private String uuid;

    @Column(nullable = false)
    private ProcessTaskType processTaskType;

    @Column
    @Type(type = "org.hibernate.type.BinaryType")
    private byte[] salt;

    @Column
    @Type(type = "org.hibernate.type.BinaryType")
    private byte[] iv;

    @Column
    private Integer processOrder;

    @ManyToOne
    private UserFile userFile;

    @Column(nullable = false, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    @CreatedDate
    private Date createdAt;

    @Column(nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    @LastModifiedDate
    private Date updatedAt;

    public UserFileProcessDTO toDTO() {
        return new UserFileProcessDTO()
                .setUuid(this.uuid)
                .setProcessTaskType(this.processTaskType)
                .setOrder(this.processOrder);
    }

    @Override
    public int compareTo(UserFileProcess u) {
        if (getProcessOrder() == null || u.getProcessOrder() == null) {
            return 0;
        }
        return getProcessOrder().compareTo(u.getProcessOrder());
    }
}
