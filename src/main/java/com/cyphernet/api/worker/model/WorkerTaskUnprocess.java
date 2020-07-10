package com.cyphernet.api.worker.model;

import com.cyphernet.api.worker.model.unprocessTypes.UnprocessRabbitData;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class WorkerTaskUnprocess {
    private String fileUrl;
    private String uploadUrl;
    private String responseUrl;
    private String fileID;
    private List<UnprocessRabbitData> processes;
}
