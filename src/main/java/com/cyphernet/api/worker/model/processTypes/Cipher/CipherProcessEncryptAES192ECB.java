package com.cyphernet.api.worker.model.processTypes.Cipher;

import com.cyphernet.api.worker.model.ProcessTaskType;
import com.cyphernet.api.worker.service.WorkerTaskProcessService;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

public class CipherProcessEncryptAES192ECB extends CipherProcess {
    public CipherProcessEncryptAES192ECB(String password, WorkerTaskProcessService workerTaskProcessService) throws NoSuchAlgorithmException, InvalidKeySpecException {
        super(ProcessTaskType.ENCRYPT_AES_192_ECB, password, 24, workerTaskProcessService);
    }
}
