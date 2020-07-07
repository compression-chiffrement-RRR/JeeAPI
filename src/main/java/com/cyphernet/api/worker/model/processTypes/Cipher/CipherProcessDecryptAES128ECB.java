package com.cyphernet.api.worker.model.processTypes.Cipher;

import com.cyphernet.api.worker.model.ProcessTaskType;
import com.cyphernet.api.worker.service.WorkerTaskProcessService;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

public class CipherProcessDecryptAES128ECB extends CipherProcess {
    public CipherProcessDecryptAES128ECB(String password, WorkerTaskProcessService workerTaskProcessService) throws NoSuchAlgorithmException, InvalidKeySpecException {
        super(ProcessTaskType.DECRYPT_AES_128_ECB, password, 16, workerTaskProcessService);
    }
}
