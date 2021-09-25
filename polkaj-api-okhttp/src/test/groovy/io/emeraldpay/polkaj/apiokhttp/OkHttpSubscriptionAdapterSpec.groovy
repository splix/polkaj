package io.emeraldpay.polkaj.apiokhttp

import io.emeraldpay.polkaj.api.SubscriptionAdapter
import io.emeraldpay.polkaj.api.SubscriptionAdapterSpec
import io.emeraldpay.polkaj.apiokhttp.OkHttpSubscriptionAdapter

import java.time.Duration

class OkHttpSubscriptionAdapterSpec extends SubscriptionAdapterSpec {

    @Override
    SubscriptionAdapter provideAdapter(String connectTo) {
        return OkHttpSubscriptionAdapter.Builder.@Companion.invoke({ builder ->
            builder.target(connectTo).timeout(Duration.ofSeconds(15))
        })
    }

}