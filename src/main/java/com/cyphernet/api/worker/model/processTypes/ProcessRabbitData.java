package com.cyphernet.api.worker.model.processTypes;

import com.cyphernet.api.worker.model.ProcessTaskType;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProcessRabbitData {
    private ProcessTaskType type;
    private byte[] key;
    private byte[] iv;
}
