package com.cyphernet.api.worker.model;

import lombok.Data;

@Data
public class WorkerTaskResult {
    private String error;
    private String fileID;
    private Boolean success;
}
