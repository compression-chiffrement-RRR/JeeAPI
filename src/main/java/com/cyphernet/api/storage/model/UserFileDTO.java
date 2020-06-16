package com.cyphernet.api.storage.model;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class UserFileDTO {
    private String uuid;

    private String name;

    private String url;

    private Boolean isTreated;

    private String creationDate;
}
