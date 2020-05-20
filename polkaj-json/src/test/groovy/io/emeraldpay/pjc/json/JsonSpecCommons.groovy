package io.emeraldpay.pjc.json

import com.fasterxml.jackson.databind.ObjectMapper
import io.emeraldpay.pjc.json.jackson.PolkadotModule

class JsonSpecCommons {

    static ObjectMapper objectMapper = new ObjectMapper().tap {
        registerModule(new PolkadotModule())
    }
}
