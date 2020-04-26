package io.emeraldpay.pjc.scale.writer;

import io.emeraldpay.pjc.scale.ScaleWriter;
import io.emeraldpay.pjc.scale.ScaleCodecWriter;
import io.emeraldpay.pjc.scale.UnionValue;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class UnionWriter<T> implements ScaleWriter<UnionValue<T>> {

    private List<ScaleWriter<T>> mapping;

    public UnionWriter(List<ScaleWriter<T>> mapping) {
        this.mapping = mapping;
    }

    @SuppressWarnings("unchecked")
    public UnionWriter(ScaleWriter<T>... mapping) {
        this(Arrays.asList(mapping));
    }

    @Override
    public void write(ScaleCodecWriter wrt, UnionValue<T> value) throws IOException {
        wrt.write(value.getIndex());
        mapping.get(value.getIndex()).write(wrt, value.getValue());
    }

}
