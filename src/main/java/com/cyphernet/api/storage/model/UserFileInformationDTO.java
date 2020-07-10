package com.cyphernet.api.storage.model;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@Accessors(chain = true)
public class UserFileInformationDTO {
    private String uuid;

    private String name;

    private Boolean isTreated;

    private List<UserFileProcessDTO> processes;

    private String creationDate;
}
