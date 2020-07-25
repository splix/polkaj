package io.emeraldpay.polkaj.tx;

import io.emeraldpay.polkaj.types.ByteData;

import java.util.function.Function;

public interface StorageRequest<T> extends Function<ByteData, T> {

    ByteData requestData();

}
