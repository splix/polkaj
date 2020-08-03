package io.emeraldpay.polkaj.tx;

import io.emeraldpay.polkaj.types.ByteData;

import java.io.IOException;

public interface ExtrinsicRequest {

    ByteData requestData() throws IOException;

}
