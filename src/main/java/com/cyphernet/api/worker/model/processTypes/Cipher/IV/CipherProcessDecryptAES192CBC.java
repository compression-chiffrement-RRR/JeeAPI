package com.cyphernet.api.worker.model.processTypes.Cipher.IV;

import com.cyphernet.api.worker.model.ProcessTaskType;
import com.cyphernet.api.worker.service.WorkerTaskProcessService;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

public class CipherProcessDecryptAES192CBC extends CipherProcessWithIV {
    public CipherProcessDecryptAES192CBC(String password, WorkerTaskProcessService workerTaskProcessService) throws NoSuchAlgorithmException, InvalidKeySpecException {
        super(ProcessTaskType.DECRYPT_AES_192_CBC, password, 24, workerTaskProcessService);
    }
}
