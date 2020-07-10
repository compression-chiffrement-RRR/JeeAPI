package com.cyphernet.api.worker.service;

import com.cyphernet.api.worker.model.WorkerTaskProcess;
import com.cyphernet.api.worker.model.WorkerTaskUnprocess;
import com.cyphernet.api.worker.model.processTypes.Process;
import com.cyphernet.api.worker.model.processTypes.ProcessRabbitData;
import com.cyphernet.api.worker.model.unprocessTypes.Unprocess;
import com.cyphernet.api.worker.model.unprocessTypes.UnprocessRabbitData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class WorkerTaskService {
    private final RabbitTemplate rabbitTemplate;

    @Value("${spring.rabbitmq.queue-name}")
    private String queueName;

    @Autowired
    public WorkerTaskService(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void createAndSendNewWorkerTaskProcess(String responseUrl, String fileUrlGET, String fileUrlPUT, String fileID, List<Process> processes) {
        List<ProcessRabbitData> processRabbitData = processes.stream().map(Process::toRabbitData).collect(Collectors.toList());
        var workerTask = new WorkerTaskProcess(fileUrlGET, fileUrlPUT, responseUrl, fileID, processRabbitData);
        rabbitTemplate.convertAndSend(queueName, workerTask);
    }

    public void createAndSendNewWorkerTaskUnprocess(String responseUrl, String fileUrlGET, String fileUrlPUT, String fileID, List<Unprocess> processes) {
        List<UnprocessRabbitData> processRabbitData = processes.stream().map(Unprocess::toRabbitData).collect(Collectors.toList());
        var workerTask = new WorkerTaskUnprocess(fileUrlGET, fileUrlPUT, responseUrl, fileID, processRabbitData);
        rabbitTemplate.convertAndSend(queueName, workerTask);
    }
}
