package com.cyphernet.api.worker.model;

import com.cyphernet.api.worker.model.processTypes.ProcessRabbitData;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class WorkerTaskProcess {
    private String fileUrl;
    private String uploadUrl;
    private String responseUrl;
    private String fileID;
    private List<ProcessRabbitData> processes;
}
