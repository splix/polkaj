package io.emeraldpay.polkaj.tx;

import io.emeraldpay.polkaj.types.ByteData;

import java.io.IOException;

public interface EncodeRequest {

    ByteData encodeRequest() throws IOException;

}
