package com.cyphernet.api.worker.model.unprocessTypes.Cipher.IV;

import com.cyphernet.api.worker.model.UnprocessTaskType;
import com.cyphernet.api.worker.model.unprocessTypes.Cipher.CipherUnprocess;
import com.cyphernet.api.worker.model.unprocessTypes.UnprocessRabbitData;
import com.cyphernet.api.worker.service.WorkerTaskProcessService;
import lombok.Getter;
import lombok.Setter;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

@Getter
@Setter
public abstract class CipherUnprocessWithIV extends CipherUnprocess {
    private byte[] iv;

    public CipherUnprocessWithIV(UnprocessTaskType type, String password, byte[] salt, Integer keyLength, byte[] iv, WorkerTaskProcessService workerTaskProcessService) throws InvalidKeySpecException, NoSuchAlgorithmException {
        super(type, password, salt, keyLength, workerTaskProcessService);
        this.iv = iv;
    }

    public UnprocessRabbitData toRabbitData() {
        return new UnprocessRabbitData()
                .setType(this.getType())
                .setKey(this.getKey())
                .setIv(this.getIv());
    }
}
