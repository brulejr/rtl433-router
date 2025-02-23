package io.jrb.labs.rtl433.router.datafill

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "application.router.sources")
data class SourcesDatafill(
    val mqtt: List<MqttBroker>
)

