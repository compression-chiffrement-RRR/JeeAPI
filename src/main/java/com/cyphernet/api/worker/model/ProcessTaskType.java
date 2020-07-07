package com.cyphernet.api.worker.model;

public enum ProcessTaskType {
    ENCRYPT_AES_128_ECB,
    ENCRYPT_AES_192_ECB,
    ENCRYPT_AES_256_ECB,
    DECRYPT_AES_128_ECB,
    DECRYPT_AES_192_ECB,
    DECRYPT_AES_256_ECB,
    ENCRYPT_AES_128_CBC,
    ENCRYPT_AES_192_CBC,
    ENCRYPT_AES_256_CBC,
    DECRYPT_AES_128_CBC,
    DECRYPT_AES_192_CBC,
    DECRYPT_AES_256_CBC,
    COMPRESS_LZ78,
    DECOMPRESS_LZ78
}
