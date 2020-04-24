package io.emeraldpay.pjc.scale.reader;

import io.emeraldpay.pjc.scale.ItemReader;
import io.emeraldpay.pjc.scale.ScaleCodecReader;

import java.util.*;

public class UnionReader<T> implements ItemReader<T> {

    private List<ItemReader<Object>> mapping;

    public UnionReader(List<ItemReader<Object>> mapping) {
        this.mapping = mapping;
    }

    @SuppressWarnings("unchecked")
    public UnionReader(ItemReader<Object>... mapping) {
        this(Arrays.asList(mapping));
    }

    @Override
    @SuppressWarnings("unchecked")
    public T read(ScaleCodecReader rdr) {
        int index = rdr.readUByte();
        if (mapping.size() <= index) {
            throw new IllegalStateException("Unknown type index: " + index);
        }
        return (T) mapping.get(index).read(rdr);
    }
}
