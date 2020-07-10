package com.cyphernet.api.worker.model.unprocessTypes.Compress;

import com.cyphernet.api.worker.model.UnprocessTaskType;
import com.cyphernet.api.worker.model.unprocessTypes.Unprocess;
import com.cyphernet.api.worker.model.unprocessTypes.UnprocessRabbitData;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class DecompressProcess extends Unprocess {
    public DecompressProcess(UnprocessTaskType type) {
        super(type);
    }

    public UnprocessRabbitData toRabbitData() {
        return new UnprocessRabbitData().setType(this.getType());
    }
}
