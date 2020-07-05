package com.cyphernet.api.worker.model.processTypes.Compress;

import com.cyphernet.api.worker.model.ProcessTaskType;
import com.cyphernet.api.worker.model.processTypes.Process;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class CompressProcess extends Process {
    public CompressProcess(ProcessTaskType type) {
        super(type);
    }
}
