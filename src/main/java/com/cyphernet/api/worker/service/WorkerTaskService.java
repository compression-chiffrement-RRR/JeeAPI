package com.cyphernet.api.worker.service;

import com.cyphernet.api.worker.model.WorkerTask;
import com.cyphernet.api.worker.model.processTypes.Process;
import com.cyphernet.api.worker.model.processTypes.ProcessRabbitData;
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

    public void createAndSendNewWorkerTask(String responseUrl, String fileUrl, String fileID, List<Process> processes) {
        List<ProcessRabbitData> processRabbitData = processes.stream().map(Process::toRabbitData).collect(Collectors.toList());
        var workerTask = new WorkerTask(fileUrl, responseUrl, fileID, processRabbitData);
        rabbitTemplate.convertAndSend(queueName, workerTask);
    }
}
