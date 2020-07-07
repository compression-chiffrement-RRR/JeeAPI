package com.cyphernet.api.worker.model;

import com.cyphernet.api.worker.model.processTypes.ProcessRabbitData;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class WorkerTask {
    private String fileUrlGET;
    private String fileUrlPUT;
    private String responseUrl;
    private String fileID;
    private List<ProcessRabbitData> processes;
}
