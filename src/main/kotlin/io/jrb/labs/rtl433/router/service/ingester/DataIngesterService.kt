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
package io.jrb.labs.rtl433.router.service.ingester

import io.jrb.labs.commons.eventbus.EventBus
import io.jrb.labs.commons.eventbus.SystemEvent
import io.jrb.labs.commons.logging.LoggerDelegate
import io.jrb.labs.rtl433.router.events.RawDataEvent
import org.springframework.context.SmartLifecycle
import org.springframework.stereotype.Service
import reactor.core.Disposable
import java.util.UUID
import java.util.concurrent.atomic.AtomicBoolean

@Service
class DataIngesterService(
    private val sources: List<Source>,
    private val eventBus: EventBus
) : SmartLifecycle {

    private val log by LoggerDelegate()

    private val _serviceName = javaClass.simpleName
    private val _running: AtomicBoolean = AtomicBoolean()

    private val _subscriptions: MutableMap<String, Disposable?> = mutableMapOf()

    override fun start() {
        log.info("Starting {}...", _serviceName)
        startSources()
        subscribe { source, payload -> processMessage(source, payload) }
        eventBus.sendEvent(SystemEvent("service.start", _serviceName))
        _running.getAndSet(true)
    }

    override fun stop() {
        log.info("Stopping {}...", _serviceName)
        unsubscribe()
        stopSources()
        eventBus.sendEvent(SystemEvent("service.stop", _serviceName))
        _running.getAndSet(true)
    }

    override fun isRunning(): Boolean {
        return _running.get()
    }

    private fun processMessage(source: Source, payload: Any) {
        eventBus.sendEvent(RawDataEvent(source = source.name, topic = source.topic, data = payload))
    }

    private fun startSources() {
        sources.forEach { source ->
            log.info("Starting source - sourceType=${source.type},sourceName=${source.name}...")
            source.connect()
        }
    }

    private fun stopSources() {
        sources.forEach { source ->
            log.info("Stopping source - sourceType=${source.type},sourceName=${source.name}...")
            source.disconnect()
        }
    }

    private fun subscribe(handler: (Source, Any) -> Unit): Set<String> {
        sources.forEach { source ->
            val guid: String = UUID.randomUUID().toString()
            log.info("Subscribing to source - sourceType=${source.type},sourceName=${source.name},subscriptionId=$guid...")
            _subscriptions[guid] = source.subscribe(source.topic) { m ->
                handler(source, m)
            }
        }
        return _subscriptions.keys
    }

    fun unsubscribe() {
        _subscriptions.forEach { subscription ->
            log.info("Unsubscribing from source - subscriptionId=${subscription.key}...")
            subscription.value?.dispose()
        }
    }

}