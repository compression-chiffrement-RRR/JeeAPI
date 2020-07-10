package com.cyphernet.api.worker.model.unprocessTypes.Cipher.IV;

import com.cyphernet.api.worker.model.UnprocessTaskType;
import com.cyphernet.api.worker.service.WorkerTaskProcessService;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

public class CipherProcessDecryptAES256CBC extends CipherUnprocessWithIV {
    public CipherProcessDecryptAES256CBC(String password, byte[] salt, byte[] iv, WorkerTaskProcessService workerTaskProcessService) throws NoSuchAlgorithmException, InvalidKeySpecException {
        super(UnprocessTaskType.DECRYPT_AES_256_CBC, password, salt, 32, iv, workerTaskProcessService);
    }
}
