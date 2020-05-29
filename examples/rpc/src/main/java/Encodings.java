import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.emeraldpay.polkaj.json.jackson.HexLongDeserializer;
import io.emeraldpay.polkaj.json.jackson.HexLongSerializer;
import io.emeraldpay.polkaj.json.jackson.PolkadotModule;
import io.emeraldpay.polkaj.types.Hash256;

public class Encodings {

    public static void main(String[] args) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new PolkadotModule());

        Something x = new Something();
        x.setNumber(123L);

        System.out.println(objectMapper.writeValueAsString(x));
    }

    static class Something {
        // When converted to JSON it will be serialized as a hexadecimal string with 0x prefix
        // And can be read from the same format automatically
        private Hash256 hash;

        // Configure Jackson to read and write number in hex format with 0x prefix
        @JsonDeserialize(using = HexLongDeserializer.class)
        @JsonSerialize(using = HexLongSerializer.class)
        private Long number;

        public Hash256 getHash() {
            return hash;
        }

        public void setHash(Hash256 hash) {
            this.hash = hash;
        }

        public Long getNumber() {
            return number;
        }

        public void setNumber(Long number) {
            this.number = number;
        }
    }
}
