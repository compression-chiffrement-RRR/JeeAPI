package com.cyphernet.api.worker.model;

import com.cyphernet.api.worker.model.unprocessTypes.Cipher.CipherProcessDecryptAES128ECB;
import com.cyphernet.api.worker.model.unprocessTypes.Cipher.CipherProcessDecryptAES192ECB;
import com.cyphernet.api.worker.model.unprocessTypes.Cipher.CipherProcessDecryptAES256ECB;
import com.cyphernet.api.worker.model.unprocessTypes.Cipher.IV.CipherProcessDecryptAES128CBC;
import com.cyphernet.api.worker.model.unprocessTypes.Cipher.IV.CipherProcessDecryptAES192CBC;
import com.cyphernet.api.worker.model.unprocessTypes.Cipher.IV.CipherProcessDecryptAES256CBC;
import com.cyphernet.api.worker.model.unprocessTypes.Compress.DecompressProcessHUFFMAN;
import com.cyphernet.api.worker.model.unprocessTypes.Unprocess;
import com.cyphernet.api.worker.service.WorkerTaskProcessService;

public class UnprocessFactory {
    public static Unprocess Create(ProcessTaskType type, String password, byte[] salt, byte[] iv, WorkerTaskProcessService workerTaskProcessService) throws Exception {
        switch (type) {
            case ENCRYPT_AES_128_ECB:
                return new CipherProcessDecryptAES128ECB(password, salt, workerTaskProcessService);
            case ENCRYPT_AES_192_ECB:
                return new CipherProcessDecryptAES192ECB(password, salt, workerTaskProcessService);
            case ENCRYPT_AES_256_ECB:
                return new CipherProcessDecryptAES256ECB(password, salt, workerTaskProcessService);
            case ENCRYPT_AES_128_CBC:
                return new CipherProcessDecryptAES128CBC(password, salt, iv, workerTaskProcessService);
            case ENCRYPT_AES_192_CBC:
                return new CipherProcessDecryptAES192CBC(password, salt, iv, workerTaskProcessService);
            case ENCRYPT_AES_256_CBC:
                return new CipherProcessDecryptAES256CBC(password, salt, iv, workerTaskProcessService);
            case COMPRESS_HUFFMAN:
                return new DecompressProcessHUFFMAN();
            default:
                throw new Error("Not implemented process method");
        }
    }
}
