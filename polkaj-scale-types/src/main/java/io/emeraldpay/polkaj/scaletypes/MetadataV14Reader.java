package io.emeraldpay.polkaj.scaletypes;

import io.emeraldpay.polkaj.scale.ScaleCodecReader;
import io.emeraldpay.polkaj.scale.ScaleReader;
import io.emeraldpay.polkaj.scale.reader.EnumReader;
import io.emeraldpay.polkaj.scale.reader.ListReader;
import io.emeraldpay.polkaj.scale.reader.UnionReader;
import io.emeraldpay.polkaj.scaletypes.MetadataV14.ExtrinsicMetadataV14;
import io.emeraldpay.polkaj.scaletypes.MetadataV14.HistoricMetaCompat;
import io.emeraldpay.polkaj.scaletypes.MetadataV14.PalletCallMetadataV14;
import io.emeraldpay.polkaj.scaletypes.MetadataV14.PalletConstantMetadataV14;
import io.emeraldpay.polkaj.scaletypes.MetadataV14.PalletErrorMetadataV14;
import io.emeraldpay.polkaj.scaletypes.MetadataV14.PalletEventMetadataV14;
import io.emeraldpay.polkaj.scaletypes.MetadataV14.PalletMetadataV14;
import io.emeraldpay.polkaj.scaletypes.MetadataV14.PalletStorageMetadataV14;
import io.emeraldpay.polkaj.scaletypes.MetadataV14.PalletStorageMetadataV14.StorageEntryMetadataV14;
import io.emeraldpay.polkaj.scaletypes.MetadataV14.PortableRegistry;
import io.emeraldpay.polkaj.scaletypes.MetadataV14.PortableType;
import io.emeraldpay.polkaj.scaletypes.MetadataV14.SiField;
import io.emeraldpay.polkaj.scaletypes.MetadataV14.SiLookupTypeId;
import io.emeraldpay.polkaj.scaletypes.MetadataV14.SiPath;
import io.emeraldpay.polkaj.scaletypes.MetadataV14.SiType;
import io.emeraldpay.polkaj.scaletypes.MetadataV14.SiTypeComposite;
import io.emeraldpay.polkaj.scaletypes.MetadataV14.SiTypeDef;
import io.emeraldpay.polkaj.scaletypes.MetadataV14.SiTypeDefArray;
import io.emeraldpay.polkaj.scaletypes.MetadataV14.SiTypeDefBitSequence;
import io.emeraldpay.polkaj.scaletypes.MetadataV14.SiTypeDefCompact;
import io.emeraldpay.polkaj.scaletypes.MetadataV14.SiTypeDefPrimitive;
import io.emeraldpay.polkaj.scaletypes.MetadataV14.SiTypeDefRange;
import io.emeraldpay.polkaj.scaletypes.MetadataV14.SiTypeDefSequence;
import io.emeraldpay.polkaj.scaletypes.MetadataV14.SiTypeDefTuple;
import io.emeraldpay.polkaj.scaletypes.MetadataV14.SiTypeDefVariant;
import io.emeraldpay.polkaj.scaletypes.MetadataV14.SiTypeParameter;
import io.emeraldpay.polkaj.scaletypes.MetadataV14.SiVariant;
import io.emeraldpay.polkaj.scaletypes.MetadataV14.SignedExtensionMetadataV14;
import java.util.List;

public class MetadataV14Reader implements ScaleReader<MetadataV14> {

  public static final PortableRegistryReader PORTABLE_REGISTRY_READER = new PortableRegistryReader();
  public static final ListReader<PalletMetadataV14> PALLET_METADATA_LIST_READER = new ListReader<>(
      new PalletMetadataV14Reader());
  public static final ListReader<String> STRING_LIST_READER = new ListReader<>(
      ScaleCodecReader.STRING);
  public static final EnumReader<PalletStorageMetadataV14.Hasher> HASHER_ENUM_READER = new EnumReader<>(
      PalletStorageMetadataV14.Hasher.values());
  public static final ExtrinsicMetadataV14Reader EXTRINSIC_METADATA_READER = new ExtrinsicMetadataV14Reader();
  public static final SiLookupTypeIdReader SI_LOOKUP_TYPE_ID_READER = new SiLookupTypeIdReader();
  public static final ListReader<SiLookupTypeId> SI_LOOKUP_TYPE_ID_LIST_READER = new ListReader<>(
      SI_LOOKUP_TYPE_ID_READER);

  @Override
  public MetadataV14 read(ScaleCodecReader rdr) {
    MetadataV14 result = new MetadataV14();
    result.setMagic(ScaleCodecReader.INT32.read(rdr));
    result.setVersion(rdr.readUByte());
    if (result.getVersion() != 14) {
      throw new IllegalStateException("Unsupported metadata version: " + result.getVersion());
    }
    result.setLookup(PORTABLE_REGISTRY_READER.read(rdr));
    result.setPallets(PALLET_METADATA_LIST_READER.read(rdr));
    List<PalletMetadataV14> pallets = result.getPallets();

    result.setExtrinsic(rdr.read(EXTRINSIC_METADATA_READER));
    result.setType(rdr.read(SI_LOOKUP_TYPE_ID_READER));
    return result;
  }

  static class PalletMetadataV14Reader implements ScaleReader<PalletMetadataV14> {

    public static final PalletStorageMetadataV14Reader STORAGE_READER = new PalletStorageMetadataV14Reader();
    public static final PalletCallMetadataV14Reader CALL_READER = new PalletCallMetadataV14Reader();
    public static final PalletEventMetadataV14Reader EVENT_READER = new PalletEventMetadataV14Reader();
    public static final ListReader<PalletConstantMetadataV14> CONSTANT_LIST_READER = new ListReader<>(
        new PalletConstantMetadataV14Reader());
    public static final PalletErrorMetadataV14Reader ERROR_LIST_READER = new PalletErrorMetadataV14Reader();

    @Override
    public PalletMetadataV14 read(ScaleCodecReader rdr) {
      PalletMetadataV14 result = new PalletMetadataV14();
      result.setName(rdr.readString());
      try {
        rdr.readOptional(STORAGE_READER).ifPresent(result::setStorage);
      } catch (RuntimeException e) {
        throw new RuntimeException(e);
      }
      rdr.readOptional(CALL_READER).ifPresent(result::setCall);
      rdr.readOptional(EVENT_READER).ifPresent(result::setEvent);
      result.setConstants(CONSTANT_LIST_READER.read(rdr));
      rdr.readOptional(ERROR_LIST_READER).ifPresent(result::setError);
      result.setIndex(rdr.readUByte());
      return result;
    }
  }

  static class PortableRegistryReader implements ScaleReader<PortableRegistry> {

    public static final ListReader<PortableType> PORTABLE_TYPE_LIST_READER = new ListReader<>(
        new PortableTypeReader());

    @Override
    public PortableRegistry read(ScaleCodecReader rdr) {
      PortableRegistry result = new PortableRegistry();
      result.setPortableTypes(rdr.read(PORTABLE_TYPE_LIST_READER));
      return result;
    }
  }

  static class PortableTypeReader implements ScaleReader<PortableType> {

    public static final SiTypeReader SI_TYPE_READER = new SiTypeReader();

    @Override
    public PortableType read(ScaleCodecReader rdr) {
      PortableType result = new PortableType();
      result.setId(rdr.read(SI_LOOKUP_TYPE_ID_READER));
      result.setType(rdr.read(SI_TYPE_READER));
      return result;
    }
  }

  static class SiTypeReader implements ScaleReader<SiType> {

    public static final SiPathReader SI_PATH_READER = new SiPathReader();
    public static final ListReader<SiTypeParameter> SI_TYPE_PARAMETER_LIST_READER =
        new ListReader<>(new SiTypeParameterReader());
    @SuppressWarnings("unchecked")
    public static final UnionReader<SiTypeDef> SI_TYPE_DEF_UNION_READER =
        new UnionReader<>(
            new SiTypeCompositeReader(),
            new SiTypeDefVariantReader(),
            new SiTypeDefSequenceReader(),
            new SiTypeDefArrayReader(),
            new SiTypeDefTupleReader(),
            new EnumReader<>(SiTypeDefPrimitive.values()),
            new SiTypeDefCompactReader(),
            new SiTypeDefBitSequenceReader(),
            new SiTypeDefRangeReader(),
            new HistoricMetaCompatReader()
        );

    @Override
    public SiType read(ScaleCodecReader rdr) {
      SiType result = new SiType();
      result.setPath(rdr.read(SI_PATH_READER));
      result.setParams(rdr.read(SI_TYPE_PARAMETER_LIST_READER));
      result.setDef(rdr.read(SI_TYPE_DEF_UNION_READER).getValue());
      try {
        result.setDocs(rdr.read(STRING_LIST_READER));
      } catch (RuntimeException e) {
        throw new RuntimeException(e);
      }
      return result;
    }
  }

  static class SiTypeCompositeReader implements ScaleReader<SiTypeComposite> {

    public static final ListReader<SiField> SI_FIELD_LIST_READER = new ListReader<>(
        new SiFieldReader());

    @Override
    public SiTypeComposite read(ScaleCodecReader rdr) {
      SiTypeComposite result = new SiTypeComposite();
      result.setFields(rdr.read(SI_FIELD_LIST_READER));
      return result;
    }
  }

  static class SiTypeDefVariantReader implements ScaleReader<SiTypeDefVariant> {

    public static final ListReader<SiVariant> SI_VARIANT_LIST_READER =
        new ListReader<>(new SiVariantReader());

    @Override
    public SiTypeDefVariant read(ScaleCodecReader rdr) {
      SiTypeDefVariant result = new SiTypeDefVariant();
      result.setVariants(rdr.read(SI_VARIANT_LIST_READER));
      return result;
    }
  }

  static class SiTypeDefSequenceReader implements ScaleReader<SiTypeDefSequence> {

    @Override
    public SiTypeDefSequence read(ScaleCodecReader rdr) {
      SiTypeDefSequence result = new SiTypeDefSequence();
      result.setType(rdr.read(SI_LOOKUP_TYPE_ID_READER));
      return result;
    }
  }

  static class SiTypeDefArrayReader implements ScaleReader<SiTypeDefArray> {

    @Override
    public SiTypeDefArray read(ScaleCodecReader rdr) {
      SiTypeDefArray result = new SiTypeDefArray();
      result.setLen(rdr.readUint32());
      result.setType(rdr.read(SI_LOOKUP_TYPE_ID_READER));
      return result;
    }
  }

  static class SiTypeDefTupleReader implements ScaleReader<SiTypeDefTuple> {

    @Override
    public SiTypeDefTuple read(ScaleCodecReader rdr) {
      SiTypeDefTuple result = new SiTypeDefTuple();
      result.setValues(rdr.read(SI_LOOKUP_TYPE_ID_LIST_READER));
      return result;
    }
  }

  static class SiTypeDefCompactReader implements ScaleReader<SiTypeDefCompact> {

    @Override
    public SiTypeDefCompact read(ScaleCodecReader rdr) {
      SiTypeDefCompact result = new SiTypeDefCompact();
      result.setType(rdr.read(SI_LOOKUP_TYPE_ID_READER));
      return result;
    }
  }

  static class SiTypeDefBitSequenceReader implements ScaleReader<SiTypeDefBitSequence> {

    @Override
    public SiTypeDefBitSequence read(ScaleCodecReader rdr) {
      SiTypeDefBitSequence result = new SiTypeDefBitSequence();
      result.setBitStoreType(rdr.read(SI_LOOKUP_TYPE_ID_READER));
      result.setBitOrderType(rdr.read(SI_LOOKUP_TYPE_ID_READER));
      return result;
    }
  }

  static class SiTypeDefRangeReader implements ScaleReader<SiTypeDefRange> {

    @Override
    public SiTypeDefRange read(ScaleCodecReader rdr) {
      SiTypeDefRange result = new SiTypeDefRange();
      result.setStart(rdr.readString());
      result.setEnd(rdr.readString());
      result.setInclusive(rdr.readBoolean());
      return result;
    }
  }

  static class HistoricMetaCompatReader implements ScaleReader<HistoricMetaCompat> {

    @Override
    public HistoricMetaCompat read(ScaleCodecReader rdr) {
      HistoricMetaCompat result = new HistoricMetaCompat();
      result.setValue(rdr.readString());
      return result;
    }
  }

  static class SiVariantReader implements ScaleReader<SiVariant> {

    public static final ListReader<SiField> SI_FIELD_LIST_READER = new ListReader<>(
        new SiFieldReader());

    @Override
    public SiVariant read(ScaleCodecReader rdr) {
      SiVariant result = new SiVariant();
      result.setName(rdr.readString());
      result.setFields(rdr.read(SI_FIELD_LIST_READER));
      result.setIndex(rdr.read(ScaleCodecReader.UBYTE));
      result.setDocs(rdr.read(STRING_LIST_READER));
      return result;
    }
  }

  static class SiFieldReader implements ScaleReader<SiField> {

    @Override
    public SiField read(ScaleCodecReader rdr) {
      SiField result = new SiField();
      result.setName(rdr.readOptional(ScaleCodecReader.STRING).orElse(null));
      result.setType(rdr.read(ScaleCodecReader.COMPACT_BIGINT));
      result.setTypeName(rdr.readOptional(ScaleCodecReader.STRING).orElse(null));
      result.setDocs(rdr.read(STRING_LIST_READER));
      return result;
    }
  }

  static class SiPathReader implements ScaleReader<SiPath> {

    @Override
    public SiPath read(ScaleCodecReader rdr) {
      SiPath result = new SiPath();
      result.setPath(rdr.read(STRING_LIST_READER));
      return result;
    }
  }

  static class SiTypeParameterReader implements ScaleReader<SiTypeParameter> {

    @Override
    public SiTypeParameter read(ScaleCodecReader rdr) {
      SiTypeParameter result = new SiTypeParameter();
      result.setName(rdr.readString());
      result.setType(rdr.readOptional(SI_LOOKUP_TYPE_ID_READER).orElse(null));
      return result;
    }
  }

  static class PalletStorageMetadataV14Reader implements ScaleReader<PalletStorageMetadataV14> {

    public static final ListReader<StorageEntryMetadataV14> ENTRY_LIST_READER = new ListReader<>(
        new StorageEntryMetadataV14Reader());

    @Override
    public PalletStorageMetadataV14 read(ScaleCodecReader rdr) {
      PalletStorageMetadataV14 result = new PalletStorageMetadataV14();
      result.setPrefix(rdr.readString());
      result.setItems(ENTRY_LIST_READER.read(rdr));
      return result;
    }
  }

  static class StorageEntryMetadataV14Reader implements ScaleReader<StorageEntryMetadataV14> {

    public static final EnumReader<StorageEntryModifierV12> MODIFIER_ENUM_READER = new EnumReader<>(
        StorageEntryModifierV12.values());
    public static final TypeReader TYPE_READER = new TypeReader();

    @Override
    public StorageEntryMetadataV14 read(ScaleCodecReader rdr) {
      StorageEntryMetadataV14 result = new StorageEntryMetadataV14();
      result.setName(rdr.readString());
      result.setModifier(MODIFIER_ENUM_READER.read(rdr));
      result.setType(rdr.read(TYPE_READER));
      result.setFallback(rdr.readByteArray());
      result.setDocs(STRING_LIST_READER.read(rdr));
      return result;
    }
  }

  static class TypeReader implements ScaleReader<PalletStorageMetadataV14.Type<?>> {

    @SuppressWarnings("unchecked")
    private static final UnionReader<PalletStorageMetadataV14.Type<?>> TYPE_UNION_READER = new UnionReader<>(
        new TypePlainReader(),
        new TypeMapReader()
    );

    @Override
    public PalletStorageMetadataV14.Type<?> read(ScaleCodecReader rdr) {
      return TYPE_UNION_READER.read(rdr).getValue();
    }
  }

  static class TypePlainReader implements ScaleReader<PalletStorageMetadataV14.PlainType> {

    @Override
    public PalletStorageMetadataV14.PlainType read(ScaleCodecReader rdr) {
      return new PalletStorageMetadataV14.PlainType(rdr.read(SI_LOOKUP_TYPE_ID_READER));
    }
  }

  static class TypeMapReader implements ScaleReader<PalletStorageMetadataV14.MapType> {

    @Override
    public PalletStorageMetadataV14.MapType read(ScaleCodecReader rdr) {
      PalletStorageMetadataV14.MapDefinition definition = new PalletStorageMetadataV14.MapDefinition();
      definition.setHashers(new ListReader<>(HASHER_ENUM_READER).read(rdr));
      definition.setKey(rdr.read(SI_LOOKUP_TYPE_ID_READER));
      definition.setType(rdr.read(SI_LOOKUP_TYPE_ID_READER));
      return new PalletStorageMetadataV14.MapType(definition);
    }
  }

  static class PalletCallMetadataV14Reader implements ScaleReader<PalletCallMetadataV14> {

    @Override
    public PalletCallMetadataV14 read(ScaleCodecReader rdr) {
      PalletCallMetadataV14 result = new PalletCallMetadataV14();
      result.setType(rdr.read(SI_LOOKUP_TYPE_ID_READER));
      return result;
    }
  }

  static class PalletEventMetadataV14Reader implements ScaleReader<PalletEventMetadataV14> {

    @Override
    public PalletEventMetadataV14 read(ScaleCodecReader rdr) {
      PalletEventMetadataV14 result = new PalletEventMetadataV14();
      result.setType(rdr.read(SI_LOOKUP_TYPE_ID_READER));
      return result;
    }
  }

  static class PalletConstantMetadataV14Reader implements ScaleReader<PalletConstantMetadataV14> {

    @Override
    public PalletConstantMetadataV14 read(ScaleCodecReader rdr) {
      PalletConstantMetadataV14 result = new PalletConstantMetadataV14();
      result.setName(rdr.readString());
      result.setType(rdr.read(SI_LOOKUP_TYPE_ID_READER));
      result.setValue(rdr.readByteArray());
      result.setDocs(STRING_LIST_READER.read(rdr));
      return result;
    }
  }

  static class PalletErrorMetadataV14Reader implements ScaleReader<PalletErrorMetadataV14> {

    @Override
    public PalletErrorMetadataV14 read(ScaleCodecReader rdr) {
      PalletErrorMetadataV14 result = new PalletErrorMetadataV14();
      result.setType(rdr.read(SI_LOOKUP_TYPE_ID_READER));
      return result;
    }
  }

  static class ExtrinsicMetadataV14Reader implements ScaleReader<ExtrinsicMetadataV14> {

    public static final ListReader<SignedExtensionMetadataV14> SIGNED_EXTENSION_LIST_READER = new ListReader<>(
        new SignedExtensionMetadataV14Reader());

    @Override
    public ExtrinsicMetadataV14 read(ScaleCodecReader rdr) {
      ExtrinsicMetadataV14 result = new ExtrinsicMetadataV14();
      result.setType(rdr.read(SI_LOOKUP_TYPE_ID_READER));
      result.setVersion(rdr.readUByte());
      result.setSignedExtensions(rdr.read(SIGNED_EXTENSION_LIST_READER));
      return result;
    }
  }

  static class SignedExtensionMetadataV14Reader implements ScaleReader<SignedExtensionMetadataV14> {

    @Override
    public SignedExtensionMetadataV14 read(ScaleCodecReader rdr) {
      SignedExtensionMetadataV14 result = new SignedExtensionMetadataV14();
      result.setIdentifier(rdr.readString());
      result.setType(rdr.read(SI_LOOKUP_TYPE_ID_READER));
      result.setAdditionalSigned(rdr.read(SI_LOOKUP_TYPE_ID_READER));
      return result;
    }
  }

  static class SiLookupTypeIdReader implements ScaleReader<SiLookupTypeId> {

    @Override
    public SiLookupTypeId read(ScaleCodecReader rdr) {
      SiLookupTypeId result = new SiLookupTypeId();
      result.setId(rdr.read(ScaleCodecReader.COMPACT_BIGINT));
      return result;
    }
  }
}
