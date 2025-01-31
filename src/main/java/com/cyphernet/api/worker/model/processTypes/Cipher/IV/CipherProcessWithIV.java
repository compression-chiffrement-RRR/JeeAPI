package com.cyphernet.api.worker.model.processTypes.Cipher.IV;

import com.cyphernet.api.worker.model.ProcessTaskType;
import com.cyphernet.api.worker.model.processTypes.Cipher.CipherProcess;
import com.cyphernet.api.worker.model.processTypes.ProcessRabbitData;
import com.cyphernet.api.worker.service.WorkerTaskProcessService;
import lombok.Getter;
import lombok.Setter;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.stream.IntStream;

@Getter
@Setter
public abstract class CipherProcessWithIV extends CipherProcess {
    private byte[] iv;

    public CipherProcessWithIV(ProcessTaskType type, String password, Integer keyLength, WorkerTaskProcessService workerTaskProcessService) throws InvalidKeySpecException, NoSuchAlgorithmException {
        super(type, password, keyLength, workerTaskProcessService);
        this.iv = workerTaskProcessService.getIv();
    }

    public ProcessRabbitData toRabbitData() {
        int[] intKeyArray = IntStream.range(0, getKey().length).map(i -> getKey()[i]).toArray();
        int[] intIVArray = IntStream.range(0, getIv().length).map(i -> getIv()[i]).toArray();
        return new ProcessRabbitData()
                .setType(this.getType())
                .setKey(intKeyArray)
                .setIv(intIVArray);
    }
}
