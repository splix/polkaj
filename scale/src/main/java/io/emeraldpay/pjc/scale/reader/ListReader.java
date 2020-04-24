package io.emeraldpay.pjc.scale.reader;

import io.emeraldpay.pjc.scale.ItemReader;
import io.emeraldpay.pjc.scale.ScaleCodecReader;

import java.util.ArrayList;
import java.util.List;

public class ListReader<T> implements ItemReader<List<T>> {

    private ItemReader<T> itemReader;

    public ListReader(ItemReader<T> itemReader) {
        this.itemReader = itemReader;
    }

    @Override
    public List<T> read(ScaleCodecReader rdr) {
        int size = rdr.readCompactInt();
        List<T> result = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            result.add(rdr.read(itemReader));
        }
        return result;
    }
}
