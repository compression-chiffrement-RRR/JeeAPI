package com.cyphernet.api.worker.model;

import com.cyphernet.api.exception.ProcessTypeUnknownException;
import com.cyphernet.api.worker.model.processTypes.Cipher.CipherProcessEncryptAES128ECB;
import com.cyphernet.api.worker.model.processTypes.Cipher.CipherProcessEncryptAES192ECB;
import com.cyphernet.api.worker.model.processTypes.Cipher.CipherProcessEncryptAES256ECB;
import com.cyphernet.api.worker.model.processTypes.Cipher.IV.CipherProcessEncryptAES128CBC;
import com.cyphernet.api.worker.model.processTypes.Cipher.IV.CipherProcessEncryptAES192CBC;
import com.cyphernet.api.worker.model.processTypes.Cipher.IV.CipherProcessEncryptAES256CBC;
import com.cyphernet.api.worker.model.processTypes.Compress.CompressProcessHUFFMAN;
import com.cyphernet.api.worker.model.processTypes.Process;
import com.cyphernet.api.worker.service.WorkerTaskProcessService;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

public class ProcessFactory {
    public static Process Create(ProcessTaskType type, String password, WorkerTaskProcessService workerTaskProcessService) throws ProcessTypeUnknownException, InvalidKeySpecException, NoSuchAlgorithmException {
        switch (type) {
            case ENCRYPT_AES_128_ECB:
                return new CipherProcessEncryptAES128ECB(password, workerTaskProcessService);
            case ENCRYPT_AES_192_ECB:
                return new CipherProcessEncryptAES192ECB(password, workerTaskProcessService);
            case ENCRYPT_AES_256_ECB:
                return new CipherProcessEncryptAES256ECB(password, workerTaskProcessService);
            case ENCRYPT_AES_128_CBC:
                return new CipherProcessEncryptAES128CBC(password, workerTaskProcessService);
            case ENCRYPT_AES_192_CBC:
                return new CipherProcessEncryptAES192CBC(password, workerTaskProcessService);
            case ENCRYPT_AES_256_CBC:
                return new CipherProcessEncryptAES256CBC(password, workerTaskProcessService);
            case COMPRESS_HUFFMAN:
                return new CompressProcessHUFFMAN();
            default:
                throw new ProcessTypeUnknownException();
        }
    }
}
