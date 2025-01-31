package com.cyphernet.api.worker.model.processTypes.Cipher;

import com.cyphernet.api.worker.model.ProcessTaskType;
import com.cyphernet.api.worker.model.processTypes.Process;
import com.cyphernet.api.worker.model.processTypes.ProcessRabbitData;
import com.cyphernet.api.worker.service.WorkerTaskProcessService;
import lombok.Getter;
import lombok.Setter;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.stream.IntStream;

@Getter
@Setter
public abstract class CipherProcess extends Process {
    private final byte[] key;
    private byte[] salt;

    public CipherProcess(ProcessTaskType type, String password, Integer keyLength, WorkerTaskProcessService workerTaskProcessService) throws NoSuchAlgorithmException, InvalidKeySpecException {
        super(type);
        this.salt = workerTaskProcessService.getSalt();
        this.key = workerTaskProcessService.getKey(password, this.salt, keyLength);
    }

    public ProcessRabbitData toRabbitData() {
        int[] intKeyArray = IntStream.range(0, getKey().length).map(i -> getKey()[i]).toArray();
        return new ProcessRabbitData()
                .setType(this.getType())
                .setKey(intKeyArray);
    }
}
