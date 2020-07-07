package com.cyphernet.api.storage.model;

import com.cyphernet.api.worker.model.ProcessTaskType;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class UserFileProcessDTO {
    private String uuid;

    private ProcessTaskType processTaskType;

    private Integer order;
}
