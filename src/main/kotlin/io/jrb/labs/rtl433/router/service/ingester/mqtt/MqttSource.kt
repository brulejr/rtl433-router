/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2025 Jon Brule <brulejr@gmail.com>
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
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

    override val name: String
        get() = datafill.name

    override val topic: String
        get() = datafill.topic

    override val type: String
        get() = "MQTT"

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

    override fun subscribe(topic: String, handler: (String) -> Unit): Disposable {
        return Flux.create{ sink ->
            mqttClient?.subscribe(topic) { _, message ->
                val payload = String(message.payload)
                sink.next(payload)
            }
        }.subscribe(handler)
    }

}