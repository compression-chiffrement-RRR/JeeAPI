package com.cyphernet.api.worker.model;

import lombok.Getter;
import lombok.experimental.Accessors;

import java.util.List;

@Getter
@Accessors(chain = true)
public class WorkerTaskCreationDTO {
    String accountUuid;
    List<TypeProcessDTO> types;
    String password;
}
