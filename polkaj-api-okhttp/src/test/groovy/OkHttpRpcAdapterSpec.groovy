import io.emeraldpay.polkaj.api.RpcAdapterSpec
import io.emeraldpay.polkaj.api.RpcCallAdapter
import io.emeraldpay.polkaj.apiokhttp.OkHttpRpcAdapter

import java.time.Duration

class OkHttpRpcAdapterSpec extends RpcAdapterSpec {

    @Override
    RpcCallAdapter provideAdapter(String connectTo, String username, String password, Duration timeout) {
        return OkHttpRpcAdapter.Builder.@Companion.invoke({ builder ->
            builder.target(connectTo)
            .timeout(timeout)
            .basicAuth(username, password)
        })
    }
}
