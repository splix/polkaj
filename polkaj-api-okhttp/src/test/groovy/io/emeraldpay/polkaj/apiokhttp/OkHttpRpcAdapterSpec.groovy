package io.emeraldpay.polkaj.apiokhttp

import io.emeraldpay.polkaj.api.RpcAdapterSpec
import io.emeraldpay.polkaj.api.RpcCallAdapter

import java.time.Duration

class OkHttpRpcAdapterSpec extends RpcAdapterSpec {

    @Override
    RpcCallAdapter provideAdapter(String connectTo, String username, String password, Duration timeout) {
        def builder = OkHttpRpcAdapter.newBuilder()
        return builder.target(connectTo)
            .timeout(timeout)
            .basicAuth(username, password).build()
    }
}
