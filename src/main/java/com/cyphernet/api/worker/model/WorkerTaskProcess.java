package com.cyphernet.api.worker.model;

import lombok.Data;

import java.util.List;

@Data
public class WorkerTaskProcess {
    private ProcessTaskType type;
    private List<Integer> key;
}
