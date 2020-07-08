package com.cyphernet.api.storage.model;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.List;

@Getter
@Setter
@Accessors(chain = true)
public class UserFileAccountDTO {
    List<UserFileDTO> userFilesAuthor;

    List<UserFileDTO> userFilesCollaborator;
}
