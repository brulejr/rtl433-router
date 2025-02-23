package io.jrb.labs.rtl433.router.datafill

data class MqttBroker(
    val name: String,
    val brokerUrl: String,
    val clientId: String,
    val topic: String
)