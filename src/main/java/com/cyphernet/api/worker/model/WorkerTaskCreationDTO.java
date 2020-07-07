package com.cyphernet.api.worker.model;

import lombok.Getter;
import lombok.experimental.Accessors;

@Getter
@Accessors(chain = true)
public class WorkerTaskCreationDTO {
    String accountUuid;
    TypeProcessDTO[] types;
}
