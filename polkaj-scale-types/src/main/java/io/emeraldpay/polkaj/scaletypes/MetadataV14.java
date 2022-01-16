package io.emeraldpay.polkaj.scaletypes;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.IntStream;

/**
 * Runtime Metadata, which defines all available actions and types for the blockchain. Available
 * through state_getMetadata RPC.
 * <p>
 * Reference: https://github.com/polkadot-js/api/blob/master/packages/types/src/interfaces/metadata/definitions.ts
 */
public class MetadataV14 {

  private Integer magic;
  private Integer version;
  private PortableRegistry lookup;
  private List<PalletMetadataV14> pallets;
  private ExtrinsicMetadataV14 extrinsic;
  private SiLookupTypeId type;

  public Integer getMagic() {
    return magic;
  }

  public void setMagic(Integer magic) {
    this.magic = magic;
  }

  public Integer getVersion() {
    return version;
  }

  public void setVersion(Integer version) {
    this.version = version;
  }

  public PortableRegistry getLookup() {
    return lookup;
  }

  public void setLookup(PortableRegistry lookup) {
    this.lookup = lookup;
  }

  public List<PalletMetadataV14> getPallets() {
    return pallets;
  }

  public void setPallets(List<PalletMetadataV14> pallets) {
    this.pallets = pallets;
  }

  public ExtrinsicMetadataV14 getExtrinsic() {
    return extrinsic;
  }

  public void setExtrinsic(ExtrinsicMetadataV14 extrinsic) {
    this.extrinsic = extrinsic;
  }

  public SiLookupTypeId getType() {
    return type;
  }

  public void setType(SiLookupTypeId type) {
    this.type = type;
  }

  public Optional<WithIdx<PalletMetadataV14>> findModule(String name) {
    if (pallets == null) {
      return Optional.empty();
    }
    return IntStream.range(0, pallets.size())
        .mapToObj(idx -> WithIdx.of(idx, pallets.get(idx)))
        .filter(m -> m.value.getName().equals(name))
        .findFirst();
  }

  public Optional<Call> findCall(String moduleName, String callName) {
    return findModule(moduleName)
        .flatMap((m) -> lookup.getSiVariant(m.value.getCall().getType(), callName)
            .map(siVariant -> Call.of(m, siVariant)));
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    MetadataV14 that = (MetadataV14) o;
    return Objects.equals(magic, that.magic) && Objects.equals(version,
        that.version) && Objects.equals(lookup, that.lookup) && Objects.equals(
        pallets, that.pallets) && Objects.equals(extrinsic, that.extrinsic)
        && Objects.equals(type, that.type);
  }

  @Override
  public int hashCode() {
    return Objects.hash(magic, version, lookup, pallets, extrinsic, type);
  }

  @Override
  public String toString() {
    return "Metadata{" +
        "magic=" + magic +
        ", version=" + version +
        '}';
  }

  public enum SiTypeDefPrimitive implements SiTypeDef {
    Bool,
    Char,
    Str,
    U8,
    U16,
    U32,
    U64,
    U128,
    U256,
    I8,
    I16,
    I32,
    I64,
    I128,
    I256,
  }

  public interface SiTypeDef {

  }

  public static class Call {

    WithIdx<PalletMetadataV14> metadataV14WithIdx;
    SiVariant siVariant;

    public Call(
        WithIdx<PalletMetadataV14> metadataV14WithIdx,
        SiVariant siVariant) {
      this.metadataV14WithIdx = metadataV14WithIdx;
      this.siVariant = siVariant;
    }

    public static Call of(
        WithIdx<PalletMetadataV14> metadataV14WithIdx,
        SiVariant siVariant) {
      return new Call(metadataV14WithIdx, siVariant);
    }
  }

  public static class WithIdx<T> {

    int index;
    T value;

    public WithIdx(int index, T value) {
      this.index = index;
      this.value = value;
    }

    public static <T> WithIdx<T> of(int index, T value) {
      return new WithIdx<>(index, value);
    }
  }

  public static class PortableRegistry {

    private List<PortableType> portableTypes;

    public List<PortableType> getPortableTypes() {
      return portableTypes;
    }

    public void setPortableTypes(
        List<PortableType> portableTypes) {
      this.portableTypes = portableTypes;
    }

    public Optional<SiVariant> getSiVariant(SiLookupTypeId id, String name) {
      return portableTypes.stream()
          .filter(type -> type.getId().equals(id))
          .map(PortableType::getType)
          .findFirst()
          .flatMap(type -> {
            if (type.getDef() instanceof SiTypeDefVariant) {
              return ((SiTypeDefVariant) type.getDef()).getSiVariant(name);
            }
            return Optional.empty();
          });
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) {
        return true;
      }
      if (o == null || getClass() != o.getClass()) {
        return false;
      }
      PortableRegistry that = (PortableRegistry) o;
      return Objects.equals(portableTypes, that.portableTypes);
    }

    @Override
    public int hashCode() {
      return Objects.hash(portableTypes);
    }
  }

  public static class PortableType {

    private SiLookupTypeId id;
    private SiType type;

    public SiLookupTypeId getId() {
      return id;
    }

    public void setId(SiLookupTypeId id) {
      this.id = id;
    }

    public SiType getType() {
      return type;
    }

    public void setType(SiType type) {
      this.type = type;
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) {
        return true;
      }
      if (o == null || getClass() != o.getClass()) {
        return false;
      }
      PortableType that = (PortableType) o;
      return Objects.equals(id, that.id) && Objects.equals(type, that.type);
    }

    @Override
    public int hashCode() {
      return Objects.hash(id, type);
    }
  }

  public static class SiType {

    private SiPath path;
    private List<SiTypeParameter> params;
    private SiTypeDef def;
    private List<String> docs;

    public SiPath getPath() {
      return path;
    }

    public void setPath(SiPath path) {
      this.path = path;
    }

    public List<SiTypeParameter> getParams() {
      return params;
    }

    public void setParams(List<SiTypeParameter> params) {
      this.params = params;
    }

    public SiTypeDef getDef() {
      return def;
    }

    public void setDef(SiTypeDef def) {
      this.def = def;
    }

    public List<String> getDocs() {
      return docs;
    }

    public void setDocs(List<String> docs) {
      this.docs = docs;
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) {
        return true;
      }
      if (o == null || getClass() != o.getClass()) {
        return false;
      }
      SiType siType = (SiType) o;
      return Objects.equals(path, siType.path) && Objects.equals(params,
          siType.params) && Objects.equals(def, siType.def) && Objects.equals(docs,
          siType.docs);
    }

    @Override
    public int hashCode() {
      return Objects.hash(path, params, def, docs);
    }
  }

  public static class SiPath {

    private List<String> path;

    public List<String> getPath() {
      return path;
    }

    public void setPath(List<String> path) {
      this.path = path;
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) {
        return true;
      }
      if (o == null || getClass() != o.getClass()) {
        return false;
      }
      SiPath siPath = (SiPath) o;
      return Objects.equals(path, siPath.path);
    }

    @Override
    public int hashCode() {
      return Objects.hash(path);
    }
  }

  public static class SiTypeParameter {

    private String name;
    private SiLookupTypeId type;

    public String getName() {
      return name;
    }

    public void setName(String name) {
      this.name = name;
    }

    public SiLookupTypeId getType() {
      return type;
    }

    public void setType(SiLookupTypeId type) {
      this.type = type;
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) {
        return true;
      }
      if (o == null || getClass() != o.getClass()) {
        return false;
      }
      SiTypeParameter that = (SiTypeParameter) o;
      return Objects.equals(name, that.name) && Objects.equals(type, that.type);
    }

    @Override
    public int hashCode() {
      return Objects.hash(name, type);
    }
  }

  public static class SiTypeComposite implements SiTypeDef {

    private List<SiField> fields;

    public List<SiField> getFields() {
      return fields;
    }

    public void setFields(List<SiField> fields) {
      this.fields = fields;
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) {
        return true;
      }
      if (o == null || getClass() != o.getClass()) {
        return false;
      }
      SiTypeComposite that = (SiTypeComposite) o;
      return Objects.equals(fields, that.fields);
    }

    @Override
    public int hashCode() {
      return Objects.hash(fields);
    }
  }

  public static class SiTypeDefVariant implements SiTypeDef {

    private List<SiVariant> variants;

    public List<SiVariant> getVariants() {
      return variants;
    }

    public void setVariants(List<SiVariant> variants) {
      this.variants = variants;
    }

    public Optional<SiVariant> getSiVariant(String name) {
      return variants.stream()
          .filter(variant -> variant.getName().equals(name))
          .findFirst();
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) {
        return true;
      }
      if (o == null || getClass() != o.getClass()) {
        return false;
      }
      SiTypeDefVariant that = (SiTypeDefVariant) o;
      return Objects.equals(variants, that.variants);
    }

    @Override
    public int hashCode() {
      return Objects.hash(variants);
    }
  }

  public static class SiTypeDefSequence implements SiTypeDef {

    private SiLookupTypeId type;

    public SiLookupTypeId getType() {
      return type;
    }

    public void setType(SiLookupTypeId type) {
      this.type = type;
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) {
        return true;
      }
      if (o == null || getClass() != o.getClass()) {
        return false;
      }
      SiTypeDefSequence that = (SiTypeDefSequence) o;
      return Objects.equals(type, that.type);
    }

    @Override
    public int hashCode() {
      return Objects.hash(type);
    }
  }

  public static class SiTypeDefArray implements SiTypeDef {

    private Long len;
    private SiLookupTypeId type;

    public Long getLen() {
      return len;
    }

    public void setLen(Long len) {
      this.len = len;
    }

    public SiLookupTypeId getType() {
      return type;
    }

    public void setType(SiLookupTypeId type) {
      this.type = type;
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) {
        return true;
      }
      if (o == null || getClass() != o.getClass()) {
        return false;
      }
      SiTypeDefArray that = (SiTypeDefArray) o;
      return Objects.equals(len, that.len) && Objects.equals(type, that.type);
    }

    @Override
    public int hashCode() {
      return Objects.hash(len, type);
    }
  }

  public static class SiTypeDefTuple implements SiTypeDef {

    private List<SiLookupTypeId> values;

    public List<SiLookupTypeId> getValues() {
      return values;
    }

    public void setValues(List<SiLookupTypeId> values) {
      this.values = values;
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) {
        return true;
      }
      if (o == null || getClass() != o.getClass()) {
        return false;
      }
      SiTypeDefTuple that = (SiTypeDefTuple) o;
      return Objects.equals(values, that.values);
    }

    @Override
    public int hashCode() {
      return Objects.hash(values);
    }
  }

  public static class SiTypeDefCompact implements SiTypeDef {

    private SiLookupTypeId type;

    public SiLookupTypeId getType() {
      return type;
    }

    public void setType(SiLookupTypeId type) {
      this.type = type;
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) {
        return true;
      }
      if (o == null || getClass() != o.getClass()) {
        return false;
      }
      SiTypeDefCompact that = (SiTypeDefCompact) o;
      return Objects.equals(type, that.type);
    }

    @Override
    public int hashCode() {
      return Objects.hash(type);
    }
  }

  public static class SiTypeDefBitSequence implements SiTypeDef {

    private SiLookupTypeId bitStoreType;
    private SiLookupTypeId bitOrderType;

    public SiLookupTypeId getBitStoreType() {
      return bitStoreType;
    }

    public void setBitStoreType(SiLookupTypeId bitStoreType) {
      this.bitStoreType = bitStoreType;
    }

    public SiLookupTypeId getBitOrderType() {
      return bitOrderType;
    }

    public void setBitOrderType(SiLookupTypeId bitOrderType) {
      this.bitOrderType = bitOrderType;
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) {
        return true;
      }
      if (o == null || getClass() != o.getClass()) {
        return false;
      }
      SiTypeDefBitSequence that = (SiTypeDefBitSequence) o;
      return Objects.equals(bitStoreType, that.bitStoreType) && Objects.equals(
          bitOrderType, that.bitOrderType);
    }

    @Override
    public int hashCode() {
      return Objects.hash(bitStoreType, bitOrderType);
    }
  }

  public static class SiTypeDefRange implements SiTypeDef {

    private String start;
    private String end;
    private Boolean inclusive;

    public String getStart() {
      return start;
    }

    public void setStart(String start) {
      this.start = start;
    }

    public String getEnd() {
      return end;
    }

    public void setEnd(String end) {
      this.end = end;
    }

    public Boolean getInclusive() {
      return inclusive;
    }

    public void setInclusive(Boolean inclusive) {
      this.inclusive = inclusive;
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) {
        return true;
      }
      if (o == null || getClass() != o.getClass()) {
        return false;
      }
      SiTypeDefRange that = (SiTypeDefRange) o;
      return Objects.equals(start, that.start) && Objects.equals(end, that.end)
          && Objects.equals(inclusive, that.inclusive);
    }

    @Override
    public int hashCode() {
      return Objects.hash(start, end, inclusive);
    }
  }

  public static class HistoricMetaCompat implements SiTypeDef {

    private String value;

    public String getValue() {
      return value;
    }

    public void setValue(String value) {
      this.value = value;
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) {
        return true;
      }
      if (o == null || getClass() != o.getClass()) {
        return false;
      }
      HistoricMetaCompat that = (HistoricMetaCompat) o;
      return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
      return Objects.hash(value);
    }
  }

  public static class SiField {

    private String name;
    private BigInteger type;
    private String typeName;
    private List<String> docs;

    public String getName() {
      return name;
    }

    public void setName(String name) {
      this.name = name;
    }

    public BigInteger getType() {
      return type;
    }

    public void setType(BigInteger type) {
      this.type = type;
    }

    public String getTypeName() {
      return typeName;
    }

    public void setTypeName(String typeName) {
      this.typeName = typeName;
    }

    public List<String> getDocs() {
      return docs;
    }

    public void setDocs(List<String> docs) {
      this.docs = docs;
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) {
        return true;
      }
      if (o == null || getClass() != o.getClass()) {
        return false;
      }
      SiField siField = (SiField) o;
      return Objects.equals(name, siField.name) && Objects.equals(type,
          siField.type) && Objects.equals(typeName, siField.typeName)
          && Objects.equals(docs, siField.docs);
    }

    @Override
    public int hashCode() {
      return Objects.hash(name, type, typeName, docs);
    }
  }

  public static class SiVariant {

    private String name;
    private List<SiField> fields;
    private Integer index;
    private List<String> docs;

    public String getName() {
      return name;
    }

    public void setName(String name) {
      this.name = name;
    }

    public List<SiField> getFields() {
      return fields;
    }

    public void setFields(List<SiField> fields) {
      this.fields = fields;
    }

    public Integer getIndex() {
      return index;
    }

    public void setIndex(Integer index) {
      this.index = index;
    }

    public List<String> getDocs() {
      return docs;
    }

    public void setDocs(List<String> docs) {
      this.docs = docs;
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) {
        return true;
      }
      if (o == null || getClass() != o.getClass()) {
        return false;
      }
      SiVariant siVariant = (SiVariant) o;
      return Objects.equals(name, siVariant.name) && Objects.equals(fields,
          siVariant.fields) && Objects.equals(index, siVariant.index)
          && Objects.equals(docs, siVariant.docs);
    }

    @Override
    public int hashCode() {
      return Objects.hash(name, fields, index, docs);
    }
  }

  public static class PalletMetadataV14 {

    private String name;
    private PalletStorageMetadataV14 storage;
    private PalletCallMetadataV14 call;
    private PalletEventMetadataV14 event;
    private List<PalletConstantMetadataV14> constants;
    private PalletErrorMetadataV14 error;
    private Integer index;

    public String getName() {
      return name;
    }

    public void setName(String name) {
      this.name = name;
    }

    public PalletStorageMetadataV14 getStorage() {
      return storage;
    }

    public void setStorage(PalletStorageMetadataV14 storage) {
      this.storage = storage;
    }

    public PalletCallMetadataV14 getCall() {
      return call;
    }

    public void setCall(PalletCallMetadataV14 call) {
      this.call = call;
    }

    public PalletEventMetadataV14 getEvent() {
      return event;
    }

    public void setEvent(PalletEventMetadataV14 event) {
      this.event = event;
    }

    public List<PalletConstantMetadataV14> getConstants() {
      return constants;
    }

    public void setConstants(List<PalletConstantMetadataV14> constants) {
      this.constants = constants;
    }

    public PalletErrorMetadataV14 getError() {
      return error;
    }

    public void setError(PalletErrorMetadataV14 error) {
      this.error = error;
    }

    public Integer getIndex() {
      return index;
    }

    public void setIndex(Integer index) {
      this.index = index;
    }

    @Override
    public final boolean equals(Object o) {
      if (this == o) {
        return true;
      }
      if (!(o instanceof PalletMetadataV14)) {
        return false;
      }
      PalletMetadataV14 module = (PalletMetadataV14) o;
      return Objects.equals(name, module.name) &&
          Objects.equals(storage, module.storage) &&
          Objects.equals(call, module.call) &&
          Objects.equals(event, module.event) &&
          Objects.equals(constants, module.constants) &&
          Objects.equals(error, module.error) &&
          Objects.equals(index, module.index);
    }

    @Override
    public final int hashCode() {
      return Objects.hash(name, storage, call, event, constants, error, index);
    }
  }

  public static class PalletStorageMetadataV14 {

    private String prefix;
    private List<StorageEntryMetadataV14> items;

    public String getPrefix() {
      return prefix;
    }

    public void setPrefix(String prefix) {
      this.prefix = prefix;
    }

    public List<StorageEntryMetadataV14> getItems() {
      return items;
    }

    public void setItems(List<StorageEntryMetadataV14> items) {
      this.items = items;
    }

    @Override
    public final boolean equals(Object o) {
      if (this == o) {
        return true;
      }
      if (!(o instanceof PalletStorageMetadataV14)) {
        return false;
      }
      PalletStorageMetadataV14 storage = (PalletStorageMetadataV14) o;
      return Objects.equals(prefix, storage.prefix) &&
          Objects.equals(items, storage.items);
    }

    @Override
    public final int hashCode() {
      return Objects.hash(prefix, items);
    }

    public enum Hasher {
      BLAKE2_128,
      BLAKE2_256,
      BLAKE2_128_CONCAT,
      TWOX_128,
      TWOX_256,
      TWOX_64_CONCAT,
      IDENTITY
    }

    public static enum TypeId {
      PLAIN(SiLookupTypeId.class),
      MAP(MapDefinition.class);

      private final Class<?> clazz;

      TypeId(Class<?> clazz) {
        this.clazz = clazz;
      }

      public Class<?> getClazz() {
        return clazz;
      }
    }

    public static class StorageEntryMetadataV14 {

      private String name;
      private StorageEntryModifierV12 modifier;
      private Type<?> type;
      private byte[] fallback;
      private List<String> docs;

      public String getName() {
        return name;
      }

      public void setName(String name) {
        this.name = name;
      }

      public StorageEntryModifierV12 getModifier() {
        return modifier;
      }

      public void setModifier(StorageEntryModifierV12 modifier) {
        this.modifier = modifier;
      }

      public Type<?> getType() {
        return type;
      }

      public void setType(Type<?> type) {
        this.type = type;
      }

      public byte[] getFallback() {
        return fallback;
      }

      public void setFallback(byte[] fallback) {
        this.fallback = fallback;
      }

      public List<String> getDocs() {
        return docs;
      }

      public void setDocs(List<String> docs) {
        this.docs = docs;
      }

      @Override
      public final boolean equals(Object o) {
        if (this == o) {
          return true;
        }
        if (!(o instanceof StorageEntryMetadataV14)) {
          return false;
        }
        StorageEntryMetadataV14 entry = (StorageEntryMetadataV14) o;
        return Objects.equals(name, entry.name) &&
            modifier == entry.modifier &&
            Objects.equals(type, entry.type) &&
            Arrays.equals(fallback, entry.fallback) &&
            Objects.equals(docs, entry.docs);
      }

      @Override
      public final int hashCode() {
        int result = Objects.hash(name, modifier, type, docs);
        result = 31 * result + Arrays.hashCode(fallback);
        return result;
      }
    }

    public abstract static class Type<T> {

      private final T value;

      public Type(T value) {
        this.value = value;
      }

      public abstract TypeId getId();

      public T get() {
        return value;
      }

      @SuppressWarnings("unchecked")
      public <X> Type<X> cast(Class<X> clazz) {
        if (clazz.isAssignableFrom(getId().getClazz())) {
          return (Type<X>) this;
        }
        throw new ClassCastException("Cannot cast " + getId().getClazz() + " to " + clazz);
      }

      @Override
      public final boolean equals(Object o) {
        if (this == o) {
          return true;
        }
        if (!(o instanceof Type)) {
          return false;
        }
        Type<?> type = (Type<?>) o;
        return Objects.equals(value, type.value);
      }

      @Override
      public final int hashCode() {
        return Objects.hash(value);
      }
    }

    public static class PlainType extends Type<SiLookupTypeId> {

      public PlainType(SiLookupTypeId value) {
        super(value);
      }

      @Override
      public TypeId getId() {
        return TypeId.PLAIN;
      }
    }

    public static class MapDefinition {

      private List<Hasher> hashers;
      private SiLookupTypeId key;
      private SiLookupTypeId type;

      public List<Hasher> getHashers() {
        return hashers;
      }

      public void setHashers(
          List<Hasher> hashers) {
        this.hashers = hashers;
      }

      public SiLookupTypeId getKey() {
        return key;
      }

      public void setKey(SiLookupTypeId key) {
        this.key = key;
      }

      public SiLookupTypeId getType() {
        return type;
      }

      public void setType(SiLookupTypeId type) {
        this.type = type;
      }

      @Override
      public boolean equals(Object o) {
        if (this == o) {
          return true;
        }
        if (o == null || getClass() != o.getClass()) {
          return false;
        }
        MapDefinition that = (MapDefinition) o;
        return Objects.equals(hashers, that.hashers) && Objects.equals(key, that.key)
            && Objects.equals(type, that.type);
      }

      @Override
      public int hashCode() {
        return Objects.hash(hashers, key, type);
      }
    }

    public static class MapType extends Type<MapDefinition> {

      public MapType(MapDefinition value) {
        super(value);
      }

      @Override
      public TypeId getId() {
        return TypeId.MAP;
      }
    }

    public static class DoubleMapDefinition {

      private Hasher firstHasher;
      private String firstKey;
      private Hasher secondHasher;
      private String secondKey;
      private SiLookupTypeId type;

      public Hasher getFirstHasher() {
        return firstHasher;
      }

      public void setFirstHasher(Hasher firstHasher) {
        this.firstHasher = firstHasher;
      }

      public String getFirstKey() {
        return firstKey;
      }

      public void setFirstKey(String firstKey) {
        this.firstKey = firstKey;
      }

      public String getSecondKey() {
        return secondKey;
      }

      public void setSecondKey(String secondKey) {
        this.secondKey = secondKey;
      }

      public SiLookupTypeId getType() {
        return type;
      }

      public void setType(SiLookupTypeId type) {
        this.type = type;
      }

      public Hasher getSecondHasher() {
        return secondHasher;
      }

      public void setSecondHasher(Hasher secondHasher) {
        this.secondHasher = secondHasher;
      }

      @Override
      public final boolean equals(Object o) {
        if (this == o) {
          return true;
        }
        if (!(o instanceof DoubleMapDefinition)) {
          return false;
        }
        DoubleMapDefinition that = (DoubleMapDefinition) o;
        return firstHasher == that.firstHasher &&
            Objects.equals(firstKey, that.firstKey) &&
            Objects.equals(secondKey, that.secondKey) &&
            Objects.equals(type, that.type) &&
            secondHasher == that.secondHasher;
      }

      @Override
      public final int hashCode() {
        return Objects.hash(firstHasher, firstKey, secondKey, type, secondHasher);
      }
    }

  }

  public static class PalletCallMetadataV14 {

    private SiLookupTypeId type;

    public SiLookupTypeId getType() {
      return type;
    }

    public void setType(SiLookupTypeId type) {
      this.type = type;
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) {
        return true;
      }
      if (o == null || getClass() != o.getClass()) {
        return false;
      }
      PalletCallMetadataV14 call = (PalletCallMetadataV14) o;
      return Objects.equals(type, call.type);
    }

    @Override
    public int hashCode() {
      return Objects.hash(type);
    }
  }

  public static class PalletEventMetadataV14 {

    private SiLookupTypeId type;

    public SiLookupTypeId getType() {
      return type;
    }

    public void setType(SiLookupTypeId type) {
      this.type = type;
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) {
        return true;
      }
      if (o == null || getClass() != o.getClass()) {
        return false;
      }
      PalletEventMetadataV14 event = (PalletEventMetadataV14) o;
      return Objects.equals(type, event.type);
    }

    @Override
    public int hashCode() {
      return Objects.hash(type);
    }
  }

  public static class PalletConstantMetadataV14 {

    private String name;
    private SiLookupTypeId type;
    private byte[] value;
    private List<String> docs;

    public String getName() {
      return name;
    }

    public void setName(String name) {
      this.name = name;
    }

    public SiLookupTypeId getType() {
      return type;
    }

    public void setType(SiLookupTypeId type) {
      this.type = type;
    }

    public byte[] getValue() {
      return value;
    }

    public void setValue(byte[] value) {
      this.value = value;
    }

    public List<String> getDocs() {
      return docs;
    }

    public void setDocs(List<String> docs) {
      this.docs = docs;
    }

    @Override
    public final boolean equals(Object o) {
      if (this == o) {
        return true;
      }
      if (!(o instanceof PalletConstantMetadataV14)) {
        return false;
      }
      PalletConstantMetadataV14 constant = (PalletConstantMetadataV14) o;
      return Objects.equals(name, constant.name) &&
          Objects.equals(type, constant.type) &&
          Arrays.equals(value, constant.value) &&
          Objects.equals(docs, constant.docs);
    }

    @Override
    public final int hashCode() {
      int result = Objects.hash(name, type, docs);
      result = 31 * result + Arrays.hashCode(value);
      return result;
    }
  }

  public static class PalletErrorMetadataV14 {

    private SiLookupTypeId type;

    public SiLookupTypeId getType() {
      return type;
    }

    public void setType(SiLookupTypeId type) {
      this.type = type;
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) {
        return true;
      }
      if (o == null || getClass() != o.getClass()) {
        return false;
      }
      PalletErrorMetadataV14 error = (PalletErrorMetadataV14) o;
      return Objects.equals(type, error.type);
    }

    @Override
    public int hashCode() {
      return Objects.hash(type);
    }
  }

  public static class ExtrinsicMetadataV14 {

    private SiLookupTypeId type;
    private Integer version;
    private List<SignedExtensionMetadataV14> signedExtensions;

    public SiLookupTypeId getType() {
      return type;
    }

    public void setType(SiLookupTypeId type) {
      this.type = type;
    }

    public Integer getVersion() {
      return version;
    }

    public void setVersion(Integer version) {
      this.version = version;
    }

    public List<SignedExtensionMetadataV14> getSignedExtensions() {
      return signedExtensions;
    }

    public void setSignedExtensions(
        List<SignedExtensionMetadataV14> signedExtensions) {
      this.signedExtensions = signedExtensions;
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) {
        return true;
      }
      if (o == null || getClass() != o.getClass()) {
        return false;
      }
      ExtrinsicMetadataV14 that = (ExtrinsicMetadataV14) o;
      return Objects.equals(type, that.type) && Objects.equals(version,
          that.version) && Objects.equals(signedExtensions, that.signedExtensions);
    }

    @Override
    public int hashCode() {
      return Objects.hash(type, version, signedExtensions);
    }
  }

  public static class SignedExtensionMetadataV14 {

    private String identifier;
    private SiLookupTypeId type;
    private SiLookupTypeId additionalSigned;

    public String getIdentifier() {
      return identifier;
    }

    public void setIdentifier(String identifier) {
      this.identifier = identifier;
    }

    public SiLookupTypeId getType() {
      return type;
    }

    public void setType(SiLookupTypeId type) {
      this.type = type;
    }

    public SiLookupTypeId getAdditionalSigned() {
      return additionalSigned;
    }

    public void setAdditionalSigned(SiLookupTypeId additionalSigned) {
      this.additionalSigned = additionalSigned;
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) {
        return true;
      }
      if (o == null || getClass() != o.getClass()) {
        return false;
      }
      SignedExtensionMetadataV14 that = (SignedExtensionMetadataV14) o;
      return Objects.equals(identifier, that.identifier) && Objects.equals(type,
          that.type) && Objects.equals(additionalSigned, that.additionalSigned);
    }

    @Override
    public int hashCode() {
      return Objects.hash(identifier, type, additionalSigned);
    }
  }

  public static class SiLookupTypeId {

    private BigInteger id;

    public static SiLookupTypeId of(long id) {
      SiLookupTypeId result = new SiLookupTypeId();
      result.setId(BigInteger.valueOf(id));
      return result;
    }

    public BigInteger getId() {
      return id;
    }

    public void setId(BigInteger id) {
      this.id = id;
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) {
        return true;
      }
      if (o == null || getClass() != o.getClass()) {
        return false;
      }
      SiLookupTypeId that = (SiLookupTypeId) o;
      return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
      return Objects.hash(id);
    }
  }
}
