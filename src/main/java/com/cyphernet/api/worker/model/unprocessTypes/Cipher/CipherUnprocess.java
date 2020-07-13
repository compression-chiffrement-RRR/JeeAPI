package com.cyphernet.api.worker.model.unprocessTypes.Cipher;

import com.cyphernet.api.worker.model.UnprocessTaskType;
import com.cyphernet.api.worker.model.unprocessTypes.Unprocess;
import com.cyphernet.api.worker.model.unprocessTypes.UnprocessRabbitData;
import com.cyphernet.api.worker.service.WorkerTaskProcessService;
import lombok.Getter;
import lombok.Setter;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.stream.IntStream;

@Getter
@Setter
public abstract class CipherUnprocess extends Unprocess {
    private final byte[] key;
    private byte[] salt;

    public CipherUnprocess(UnprocessTaskType type, String password, byte[] salt, Integer keyLength, WorkerTaskProcessService workerTaskProcessService) throws NoSuchAlgorithmException, InvalidKeySpecException {
        super(type);
        this.salt = salt;
        this.key = workerTaskProcessService.getKey(password, this.salt, keyLength);
    }

    public UnprocessRabbitData toRabbitData() {
        int[] intKeyArray = IntStream.range(0, getKey().length).map(i -> getKey()[i]).toArray();
        return new UnprocessRabbitData()
                .setType(this.getType())
                .setKey(intKeyArray);
    }
}
