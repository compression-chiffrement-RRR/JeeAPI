package com.cyphernet.api.worker.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class WorkerTask {
    private String fileUrl;
    private String responseUrl;
    private String fileID;
    private List<WorkerTaskProcess> processes;
}
