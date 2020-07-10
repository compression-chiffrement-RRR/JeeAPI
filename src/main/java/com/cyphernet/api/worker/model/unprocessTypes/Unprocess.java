package com.cyphernet.api.worker.model.unprocessTypes;

import com.cyphernet.api.worker.model.UnprocessTaskType;
import lombok.Data;

@Data
public abstract class Unprocess {
    UnprocessTaskType type;

    public Unprocess(UnprocessTaskType type) {
        this.type = type;
    }

    public abstract UnprocessRabbitData toRabbitData();
}
