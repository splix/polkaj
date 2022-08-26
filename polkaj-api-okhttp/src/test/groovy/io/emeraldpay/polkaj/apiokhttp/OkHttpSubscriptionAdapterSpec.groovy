package io.emeraldpay.polkaj.apiokhttp

import io.emeraldpay.polkaj.api.SubscriptionAdapter
import io.emeraldpay.polkaj.api.SubscriptionAdapterSpec

import java.time.Duration

class OkHttpSubscriptionAdapterSpec extends SubscriptionAdapterSpec {

    @Override
    SubscriptionAdapter provideAdapter(String connectTo) {
        return OkHttpSubscriptionAdapter.newBuilder().connectTo(connectTo).timeout(Duration.ofSeconds(15)).build()
    }

}