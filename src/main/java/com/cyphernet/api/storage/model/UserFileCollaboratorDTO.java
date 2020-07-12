package com.cyphernet.api.storage.model;

import com.cyphernet.api.account.model.AccountDTO;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class UserFileCollaboratorDTO {
    private String userFileUuid;
    private AccountDTO account;
    private Boolean pending;
    private String creationDate;
}
