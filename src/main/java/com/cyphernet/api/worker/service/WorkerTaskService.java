package com.cyphernet.api.worker.service;

import com.cyphernet.api.worker.model.WorkerTask;
import com.cyphernet.api.worker.model.processTypes.Process;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

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
        var workerTask = new WorkerTask(fileUrl, responseUrl, fileID, processes);
        rabbitTemplate.convertAndSend(queueName, workerTask);

        //Secure random - lib
    }
}
