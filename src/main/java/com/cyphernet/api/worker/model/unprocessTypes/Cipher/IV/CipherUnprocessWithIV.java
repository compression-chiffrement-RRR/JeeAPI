package com.cyphernet.api.worker.model.unprocessTypes.Cipher.IV;

import com.cyphernet.api.worker.model.UnprocessTaskType;
import com.cyphernet.api.worker.model.unprocessTypes.Cipher.CipherUnprocess;
import com.cyphernet.api.worker.model.unprocessTypes.UnprocessRabbitData;
import com.cyphernet.api.worker.service.WorkerTaskProcessService;
import lombok.Getter;
import lombok.Setter;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.stream.IntStream;

@Getter
@Setter
public abstract class CipherUnprocessWithIV extends CipherUnprocess {
    private byte[] iv;

    public CipherUnprocessWithIV(UnprocessTaskType type, String password, byte[] salt, Integer keyLength, byte[] iv, WorkerTaskProcessService workerTaskProcessService) throws InvalidKeySpecException, NoSuchAlgorithmException {
        super(type, password, salt, keyLength, workerTaskProcessService);
        this.iv = iv;
    }

    public UnprocessRabbitData toRabbitData() {
        int[] intKeyArray = IntStream.range(0, getKey().length).map(i -> getKey()[i]).toArray();
        int[] intIVArray = IntStream.range(0, getIv().length).map(i -> getIv()[i]).toArray();
        return new UnprocessRabbitData()
                .setType(this.getType())
                .setKey(intKeyArray)
                .setIv(intIVArray);
    }
}
