package com.cyphernet.api.worker.model.processTypes.Compress;

import com.cyphernet.api.worker.model.ProcessTaskType;

public class DecompressProcessHUFFMAN extends CompressProcess {
    public DecompressProcessHUFFMAN() {
        super(ProcessTaskType.DECOMPRESS_HUFFMAN);
    }
}
