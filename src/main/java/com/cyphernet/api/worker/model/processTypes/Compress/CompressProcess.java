package com.cyphernet.api.worker.model.processTypes.Compress;

import com.cyphernet.api.worker.model.ProcessTaskType;
import com.cyphernet.api.worker.model.processTypes.Process;
import com.cyphernet.api.worker.model.processTypes.ProcessRabbitData;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class CompressProcess extends Process {
    public CompressProcess(ProcessTaskType type) {
        super(type);
    }

    public ProcessRabbitData toRabbitData() {
        return new ProcessRabbitData().setType(this.getType());
    }
}
