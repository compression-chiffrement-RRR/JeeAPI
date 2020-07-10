package com.cyphernet.api.worker.model.unprocessTypes.Cipher.IV;

import com.cyphernet.api.worker.model.UnprocessTaskType;
import com.cyphernet.api.worker.service.WorkerTaskProcessService;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

public class CipherProcessDecryptAES192CBC extends CipherUnprocessWithIV {
    public CipherProcessDecryptAES192CBC(String password, byte[] salt, byte[] iv, WorkerTaskProcessService workerTaskProcessService) throws NoSuchAlgorithmException, InvalidKeySpecException {
        super(UnprocessTaskType.DECRYPT_AES_192_CBC, password, salt, 24, iv, workerTaskProcessService);
    }
}
