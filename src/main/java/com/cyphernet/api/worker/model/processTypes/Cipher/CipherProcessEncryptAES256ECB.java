package com.cyphernet.api.worker.model.processTypes.Cipher;

import com.cyphernet.api.worker.model.ProcessTaskType;
import com.cyphernet.api.worker.service.WorkerTaskProcessService;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

public class CipherProcessEncryptAES256ECB extends CipherProcess {
    public CipherProcessEncryptAES256ECB(String password, WorkerTaskProcessService workerTaskProcessService) throws NoSuchAlgorithmException, InvalidKeySpecException {
        super(ProcessTaskType.ENCRYPT_AES_256_ECB, password, 32, workerTaskProcessService);
    }
}
