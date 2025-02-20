package io.jrb.labs.rtl433.router.service.ingester.mqtt

import io.jrb.labs.rtl433.router.datafill.MqttBroker
import io.jrb.labs.rtl433.router.service.ingester.Source
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.reactive.asFlow
import org.eclipse.paho.client.mqttv3.MqttClient
import org.eclipse.paho.client.mqttv3.MqttConnectOptions
import reactor.core.publisher.Flux

class MqttSource(
    private val datafill: MqttBroker
) : Source {

    private var mqttClient: MqttClient? = null

    override fun connect() {
        mqttClient = MqttClient(datafill.brokerUrl, datafill.clientId)
        val options = MqttConnectOptions()
        mqttClient?.connect(options)
    }

    override fun disconnect() {
        mqttClient?.disconnect()
    }

    override fun source(): String {
        return "MQTT"
    }

    fun subscribe(topic: String = datafill.topic): Flow<String> {
        return Flux.create{ sink ->
            mqttClient?.subscribe(topic) { _, message ->
                val payload = String(message.payload)
                sink.next(payload)
            }
        }.asFlow()
    }

}