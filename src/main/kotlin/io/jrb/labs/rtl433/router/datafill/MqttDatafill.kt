package io.jrb.labs.rtl433.router.datafill

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "application.router.mqtt")
data class MqttDatafill(
    val source: MqttBroker? = null,
    val target: MqttBroker? = null
)

data class MqttBroker(
    val brokerUrl: String,
    val clientId: String,
    val topic: String
)