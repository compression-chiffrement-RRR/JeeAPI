package com.cyphernet.api.worker.model;

import com.cyphernet.api.worker.model.processTypes.Cipher.*;
import com.cyphernet.api.worker.model.processTypes.Cipher.IV.*;
import com.cyphernet.api.worker.model.processTypes.Compress.CompressProcessLZ78;
import com.cyphernet.api.worker.model.processTypes.Compress.DecompressProcessLZ78;
import com.cyphernet.api.worker.model.processTypes.Process;
import com.cyphernet.api.worker.service.WorkerTaskProcessService;

public class ProcessFactory {
    public static Process Create(ProcessTaskType type, String password, WorkerTaskProcessService workerTaskProcessService) throws Exception {
        switch (type) {
            case ENCRYPT_AES_128_ECB:
                return new CipherProcessEncryptAES128ECB(password, workerTaskProcessService);
            case ENCRYPT_AES_192_ECB:
                return new CipherProcessEncryptAES192ECB(password, workerTaskProcessService);
            case ENCRYPT_AES_256_ECB:
                return new CipherProcessEncryptAES256ECB(password, workerTaskProcessService);
            case DECRYPT_AES_128_ECB:
                return new CipherProcessDecryptAES128ECB(password, workerTaskProcessService);
            case DECRYPT_AES_192_ECB:
                return new CipherProcessDecryptAES192ECB(password, workerTaskProcessService);
            case DECRYPT_AES_256_ECB:
                return new CipherProcessDecryptAES256ECB(password, workerTaskProcessService);
            case ENCRYPT_AES_128_CBC:
                return new CipherProcessEncryptAES128CBC(password, workerTaskProcessService);
            case ENCRYPT_AES_192_CBC:
                return new CipherProcessEncryptAES192CBC(password, workerTaskProcessService);
            case ENCRYPT_AES_256_CBC:
                return new CipherProcessEncryptAES256CBC(password, workerTaskProcessService);
            case DECRYPT_AES_128_CBC:
                return new CipherProcessDecryptAES128CBC(password, workerTaskProcessService);
            case DECRYPT_AES_192_CBC:
                return new CipherProcessDecryptAES192CBC(password, workerTaskProcessService);
            case DECRYPT_AES_256_CBC:
                return new CipherProcessDecryptAES256CBC(password, workerTaskProcessService);
            case COMPRESS_LZ78:
                return new CompressProcessLZ78();
            case DECOMPRESS_LZ78:
                return new DecompressProcessLZ78();
            default:
                throw new Error("Not implemented process method");
        }
    }
}
