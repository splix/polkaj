package io.emeraldpay.polkaj.json;

import io.emeraldpay.polkaj.types.ByteData;

import java.util.Objects;

public class ContractExecResultJson {

    private Object error;
    private Success success;

    public ContractExecResultJson() {
    }

    public ContractExecResultJson(Success success) {
        this();
        this.success = success;
    }

    public ContractExecResultJson(Object error) {
        this();
        this.error = error;
    }

    public Object getError() {
        return error;
    }

    public void setError(Object error) {
        this.error = error;
    }

    public Success getSuccess() {
        return success;
    }

    public void setSuccess(Success success) {
        this.success = success;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ContractExecResultJson)) return false;
        ContractExecResultJson that = (ContractExecResultJson) o;
        return Objects.equals(error, that.error) &&
                Objects.equals(success, that.success);
    }

    @Override
    public int hashCode() {
        return Objects.hash(error, success);
    }

    static class Success {
        private int status;
        private ByteData data;

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }

        public ByteData getData() {
            return data;
        }

        public void setData(ByteData data) {
            this.data = data;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Success)) return false;
            Success success = (Success) o;
            return status == success.status &&
                    Objects.equals(data, success.data);
        }

        @Override
        public int hashCode() {
            return Objects.hash(status, data);
        }
    }
}
