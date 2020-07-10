package com.cyphernet.api.worker.model.unprocessTypes.Cipher;

import com.cyphernet.api.worker.model.UnprocessTaskType;
import com.cyphernet.api.worker.model.unprocessTypes.Unprocess;
import com.cyphernet.api.worker.model.unprocessTypes.UnprocessRabbitData;
import com.cyphernet.api.worker.service.WorkerTaskProcessService;
import lombok.Getter;
import lombok.Setter;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

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
        return new UnprocessRabbitData()
                .setType(this.getType())
                .setKey(this.getKey());
    }
}
