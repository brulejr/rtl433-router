package io.jrb.labs.rtl433.router.service.ingester.mqtt

import io.jrb.labs.commons.logging.LoggerDelegate
import io.jrb.labs.rtl433.router.datafill.MqttBroker
import io.jrb.labs.rtl433.router.service.ingester.Source
import org.eclipse.paho.client.mqttv3.MqttClient
import org.eclipse.paho.client.mqttv3.MqttConnectOptions
import reactor.core.Disposable
import reactor.core.publisher.Flux

class MqttSource(
    private val datafill: MqttBroker
) : Source {

    private val log by LoggerDelegate()

    private var mqttClient: MqttClient? = null

    override fun connect() {
        log.info("Connecting to '${datafill.brokerUrl}' as '${datafill.clientId}'...")
        mqttClient = MqttClient(datafill.brokerUrl, datafill.clientId)
        val options = MqttConnectOptions()
        mqttClient?.connect(options)
    }

    override fun disconnect() {
        log.info("Disconnecting from '${datafill.brokerUrl}' as '${datafill.clientId}'...")
        mqttClient?.disconnect()
    }

    override fun name(): String {
        return datafill.name
    }

    override fun type(): String {
        return "MQTT"
    }

    override fun topic(): String {
        return datafill.topic
    }

    override fun subscribe(topic: String, handler: (String) -> Unit): Disposable {
        return Flux.create{ sink ->
            mqttClient?.subscribe(topic) { _, message ->
                val payload = String(message.payload)
                sink.next(payload)
            }
        }.subscribe(handler)
    }

}