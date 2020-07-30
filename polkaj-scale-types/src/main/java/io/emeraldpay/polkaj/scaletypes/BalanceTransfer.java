package io.emeraldpay.polkaj.scaletypes;

import io.emeraldpay.polkaj.types.Address;
import io.emeraldpay.polkaj.types.DotAmount;

import java.util.Objects;

/**
 * Call to transfer [part of] balance to another address
 */
public class BalanceTransfer extends ExtrinsicCall {

    /**
     * Destionation address
     */
    private Address destination;
    /**
     * Balance to transfer
     */
    private DotAmount balance;

    public BalanceTransfer() {
        this(4, 0);
    }

    public BalanceTransfer(int moduleIndex, int callIndex) {
        super(moduleIndex, callIndex);
    }

    public Address getDestination() {
        return destination;
    }

    public void setDestination(Address destination) {
        this.destination = destination;
    }

    public DotAmount getBalance() {
        return balance;
    }

    public void setBalance(DotAmount balance) {
        this.balance = balance;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BalanceTransfer)) return false;
        if (!super.equals(o)) return false;
        BalanceTransfer that = (BalanceTransfer) o;
        return Objects.equals(destination, that.destination) &&
                Objects.equals(balance, that.balance);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), destination, balance);
    }

    @Override
    public boolean canEquals(Object o) {
        return (o instanceof BalanceTransfer);
    }

}
