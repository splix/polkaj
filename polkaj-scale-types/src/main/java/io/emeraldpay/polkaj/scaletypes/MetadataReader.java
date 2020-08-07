package io.emeraldpay.polkaj.scaletypes;

import io.emeraldpay.polkaj.scale.ScaleCodecReader;
import io.emeraldpay.polkaj.scale.ScaleReader;
import io.emeraldpay.polkaj.scale.reader.EnumReader;
import io.emeraldpay.polkaj.scale.reader.ListReader;
import io.emeraldpay.polkaj.scale.reader.UnionReader;

import java.util.List;

public class MetadataReader implements ScaleReader<Metadata> {

    public static final ListReader<Metadata.Module> MODULE_LIST_READER = new ListReader<>(new ModulesReader());
    public static final ListReader<String> STRING_LIST_READER = new ListReader<>(ScaleCodecReader.STRING);
    public static final EnumReader<Metadata.Storage.Hasher> HASHER_ENUM_READER = new EnumReader<>(Metadata.Storage.Hasher.values());

    @Override
    public Metadata read(ScaleCodecReader rdr) {
        Metadata result = new Metadata();
        result.setMagic(ScaleCodecReader.INT32.read(rdr));
        result.setVersion(rdr.readUByte());
        if (result.getVersion() != 11) {
            throw new IllegalStateException("Unsupported metadata version: " + result.getVersion());
        }
        result.setModules(MODULE_LIST_READER.read(rdr));
        List<Metadata.Module> modules = result.getModules();

        int moduleIndex = 0;
        for (Metadata.Module m: modules) {
            List<Metadata.Call> calls = m.getCalls();
            if (calls != null) {
                for (int j = 0; j < calls.size(); j++) {
                    calls.get(j).setIndex((moduleIndex << 8) + j);
                }
                moduleIndex++;
            }
        }
        return result;
    }

    static class ModulesReader implements ScaleReader<Metadata.Module> {

        public static final StorageReader STORAGE_READER = new StorageReader();
        public static final ListReader<Metadata.Call> CALL_LIST_READER = new ListReader<>(new CallReader());
        public static final ListReader<Metadata.Event> EVENT_LIST_READER = new ListReader<>(new EventReader());
        public static final ListReader<Metadata.Constant> CONSTANT_LIST_READER = new ListReader<>(new ConstantReader());
        public static final ListReader<Metadata.Error> ERROR_LIST_READER = new ListReader<>(new ErrorReader());

        @Override
        public Metadata.Module read(ScaleCodecReader rdr) {
            Metadata.Module result = new Metadata.Module();
            result.setName(rdr.readString());
            rdr.readOptional(STORAGE_READER).ifPresent(result::setStorage);
            rdr.readOptional(CALL_LIST_READER).ifPresent(result::setCalls);
            rdr.readOptional(EVENT_LIST_READER).ifPresent(result::setEvents);
            result.setConstants(CONSTANT_LIST_READER.read(rdr));
            result.setErrors(ERROR_LIST_READER.read(rdr));
            return result;
        }
    }

    static class StorageReader implements ScaleReader<Metadata.Storage> {

        public static final ListReader<Metadata.Storage.Entry> ENTRY_LIST_READER = new ListReader<>(new StorageEntryReader());

        @Override
        public Metadata.Storage read(ScaleCodecReader rdr) {
            Metadata.Storage result = new Metadata.Storage();
            result.setPrefix(rdr.readString());
            result.setEntries(ENTRY_LIST_READER.read(rdr));
            return result;
        }
    }

    static class StorageEntryReader implements ScaleReader<Metadata.Storage.Entry> {

        public static final EnumReader<Metadata.Storage.Modifier> MODIFIER_ENUM_READER = new EnumReader<>(Metadata.Storage.Modifier.values());
        public static final TypeReader TYPE_READER = new TypeReader();

        @Override
        public Metadata.Storage.Entry read(ScaleCodecReader rdr) {
            Metadata.Storage.Entry result = new Metadata.Storage.Entry();
            result.setName(rdr.readString());
            result.setModifier(MODIFIER_ENUM_READER.read(rdr));
            result.setType(rdr.read(TYPE_READER));
            result.setDefaults(rdr.readByteArray());
            result.setDocumentation(STRING_LIST_READER.read(rdr));
            return result;
        }
    }

    static class TypeReader implements ScaleReader<Metadata.Storage.Type<?>> {

        @SuppressWarnings("unchecked")
        private static final UnionReader<Metadata.Storage.Type<?>> TYPE_UNION_READER = new UnionReader<>(
                new TypePlainReader(),
                new TypeMapReader(),
                new TypeDoubleMapReader()
        );

        @Override
        public Metadata.Storage.Type<?> read(ScaleCodecReader rdr) {
            return TYPE_UNION_READER.read(rdr).getValue();
        }
    }

    static class TypePlainReader implements ScaleReader<Metadata.Storage.PlainType> {
        @Override
        public Metadata.Storage.PlainType read(ScaleCodecReader rdr) {
            return new Metadata.Storage.PlainType(rdr.readString());
        }
    }

    static class TypeMapReader implements ScaleReader<Metadata.Storage.MapType> {

        @Override
        public Metadata.Storage.MapType read(ScaleCodecReader rdr) {
            Metadata.Storage.MapDefinition definition = new Metadata.Storage.MapDefinition();
            definition.setHasher(HASHER_ENUM_READER.read(rdr));
            definition.setKey(rdr.readString());
            definition.setType(rdr.readString());
            definition.setIterable(rdr.readBoolean());
            return new Metadata.Storage.MapType(definition);
        }
    }

    static class TypeDoubleMapReader implements ScaleReader<Metadata.Storage.DoubleMapType> {

        @Override
        public Metadata.Storage.DoubleMapType read(ScaleCodecReader rdr) {
            Metadata.Storage.DoubleMapDefinition definition = new Metadata.Storage.DoubleMapDefinition();
            definition.setFirstHasher(HASHER_ENUM_READER.read(rdr));
            definition.setFirstKey(rdr.readString());
            definition.setSecondKey(rdr.readString());
            definition.setType(rdr.readString());
            definition.setSecondHasher(HASHER_ENUM_READER.read(rdr));
            return new Metadata.Storage.DoubleMapType(definition);
        }
    }

    static class CallReader implements ScaleReader<Metadata.Call> {

        public static final ListReader<Metadata.Call.Arg> ARG_LIST_READER = new ListReader<>(new ArgReader());

        @Override
        public Metadata.Call read(ScaleCodecReader rdr) {
            Metadata.Call result = new Metadata.Call();
            result.setName(rdr.readString());
            result.setArguments(ARG_LIST_READER.read(rdr));
            result.setDocumentation(STRING_LIST_READER.read(rdr));
            return result;
        }
    }

    static class ArgReader implements ScaleReader<Metadata.Call.Arg> {

        @Override
        public Metadata.Call.Arg read(ScaleCodecReader rdr) {
            Metadata.Call.Arg result = new Metadata.Call.Arg();
            result.setName(rdr.readString());
            result.setType(rdr.readString());
            return result;
        }
    }

    static class EventReader implements ScaleReader<Metadata.Event> {

        @Override
        public Metadata.Event read(ScaleCodecReader rdr) {
            Metadata.Event result = new Metadata.Event();
            result.setName(rdr.readString());
            result.setArguments(STRING_LIST_READER.read(rdr));
            result.setDocumentation(STRING_LIST_READER.read(rdr));
            return result;
        }
    }

    static class ConstantReader implements ScaleReader<Metadata.Constant> {

        @Override
        public Metadata.Constant read(ScaleCodecReader rdr) {
            Metadata.Constant result = new Metadata.Constant();
            result.setName(rdr.readString());
            result.setType(rdr.readString());
            result.setValue(rdr.readByteArray());
            result.setDocumentation(STRING_LIST_READER.read(rdr));
            return result;
        }
    }

    static class ErrorReader implements ScaleReader<Metadata.Error> {

        @Override
        public Metadata.Error read(ScaleCodecReader rdr) {
            Metadata.Error result = new Metadata.Error();
            result.setName(rdr.readString());
            result.setDocumentation(STRING_LIST_READER.read(rdr));
            return result;
        }
    }
}
