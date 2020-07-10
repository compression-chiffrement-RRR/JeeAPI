package com.cyphernet.api.storage.model;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class UserFileDTO {
    private String uuid;

    private String name;

    private Boolean isTreated;

    private Boolean isError;

    private Boolean isTemporary;

    private String creationDate;
}
