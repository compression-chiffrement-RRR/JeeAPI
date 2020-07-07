package com.cyphernet.api.worker.model.processTypes.Cipher.IV;

import com.cyphernet.api.worker.model.ProcessTaskType;
import com.cyphernet.api.worker.service.WorkerTaskProcessService;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

public class CipherProcessEncryptAES256CBC extends CipherProcessWithIV {
    public CipherProcessEncryptAES256CBC(String password, WorkerTaskProcessService workerTaskProcessService) throws NoSuchAlgorithmException, InvalidKeySpecException {
        super(ProcessTaskType.ENCRYPT_AES_256_CBC, password, 32, workerTaskProcessService);
    }
}
