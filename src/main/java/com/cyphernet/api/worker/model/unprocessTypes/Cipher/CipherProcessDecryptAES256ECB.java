package com.cyphernet.api.worker.model.unprocessTypes.Cipher;

import com.cyphernet.api.worker.model.UnprocessTaskType;
import com.cyphernet.api.worker.service.WorkerTaskProcessService;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

public class CipherProcessDecryptAES256ECB extends CipherUnprocess {
    public CipherProcessDecryptAES256ECB(String password, byte[] salt, WorkerTaskProcessService workerTaskProcessService) throws NoSuchAlgorithmException, InvalidKeySpecException {
        super(UnprocessTaskType.DECRYPT_AES_256_ECB, password, salt, 32, workerTaskProcessService);
    }
}
