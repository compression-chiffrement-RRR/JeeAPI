package com.cyphernet.api.worker.model.unprocessTypes;

import com.cyphernet.api.worker.model.UnprocessTaskType;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UnprocessRabbitData {
    private UnprocessTaskType type;
    private byte[] key;
    private byte[] iv;
}
