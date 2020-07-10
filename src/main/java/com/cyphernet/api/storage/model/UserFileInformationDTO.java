package com.cyphernet.api.storage.model;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@Accessors(chain = true)
public class UserFileInformationDTO {
    private String uuid;

    private String uuidParent;

    private String name;

    private Boolean isTreated;

    private Boolean isError;

    private Boolean isTemporary;

    private List<UserFileProcessDTO> processes;

    private String creationDate;
}
