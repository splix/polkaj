package io.emeraldpay.polkaj.scaletypes;

import io.emeraldpay.polkaj.scale.UnionValue;
import io.emeraldpay.polkaj.scaletypes.MetadataV14.Call;
import io.emeraldpay.polkaj.types.DotAmount;
import java.util.Objects;

public class ExecContract<T> extends ExtrinsicCall {

  private UnionValue<MultiAddress> destination;

  private DotAmount value;

  private long gasLimit;

  private T data;

  public ExecContract() {
    super();
  }

  public ExecContract(int moduleIndex, int callIndex) {
    super(moduleIndex, callIndex);
  }

  public ExecContract(MetadataV14 metadata) {
    this();
    init(metadata, "call");
  }

  /**
   * Initialize call index for given call of the Balance module from Runtime Metadata
   *
   * @param metadata current Runtime
   * @param callName name of the call to execute, e.g. transfer, transfer_keep_alive, or
   *                 transfer_all
   */
  public void init(MetadataV14 metadata, String callName) {
    Call call = metadata.findCall("Contracts", callName)
        .orElseThrow(
            () -> new IllegalStateException("Call 'Contracts." + callName + "' doesn't exist"));
    init(call);
  }

  public UnionValue<MultiAddress> getDestination() {
    return destination;
  }

  public void setDestination(
      UnionValue<MultiAddress> destination) {
    this.destination = destination;
  }

  public DotAmount getValue() {
    return value;
  }

  public void setValue(DotAmount value) {
    this.value = value;
  }

  public long getGasLimit() {
    return gasLimit;
  }

  public void setGasLimit(long gasLimit) {
    this.gasLimit = gasLimit;
  }

  public T getData() {
    return data;
  }

  public void setData(T data) {
    this.data = data;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    if (!super.equals(o)) {
      return false;
    }
    ExecContract<?> that = (ExecContract<?>) o;
    return gasLimit == that.gasLimit && Objects.equals(destination, that.destination)
        && Objects.equals(value, that.value) && Objects.equals(data, that.data);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), destination, value, gasLimit, data);
  }
}
