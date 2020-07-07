package com.cyphernet.api.worker.model.processTypes;

import com.cyphernet.api.worker.model.ProcessTaskType;
import lombok.Data;

@Data
public abstract class Process {
    ProcessTaskType type;

    public Process(ProcessTaskType type) {
        this.type = type;
    }

    public abstract ProcessRabbitData toRabbitData();
}
