package io.emeraldpay.polkaj.json

import com.fasterxml.jackson.databind.ObjectMapper
import io.emeraldpay.polkaj.json.jackson.PolkadotModule

class JsonSpecCommons {

    static ObjectMapper objectMapper = new ObjectMapper().tap {
        registerModule(new PolkadotModule())
    }
}
