package com.cyphernet.api.worker.model.unprocessTypes.Cipher;

import com.cyphernet.api.worker.model.UnprocessTaskType;
import com.cyphernet.api.worker.service.WorkerTaskProcessService;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

public class CipherProcessDecryptAES192ECB extends CipherUnprocess {
    public CipherProcessDecryptAES192ECB(String password, byte[] salt, WorkerTaskProcessService workerTaskProcessService) throws NoSuchAlgorithmException, InvalidKeySpecException {
        super(UnprocessTaskType.DECRYPT_AES_192_ECB, password, salt, 24, workerTaskProcessService);
    }
}
