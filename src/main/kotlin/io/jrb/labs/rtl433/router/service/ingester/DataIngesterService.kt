package io.jrb.labs.rtl433.router.service.ingester

import io.jrb.labs.commons.eventbus.DataEvent
import io.jrb.labs.commons.eventbus.EventBus
import io.jrb.labs.commons.eventbus.SystemEvent
import io.jrb.labs.commons.logging.LoggerDelegate
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
        subscribe { message -> processMessage(message) }
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

    private fun processMessage(message: DataMessage) {
        eventBus.sendEvent(DataEvent(message.source, message.type, message))
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

    private fun subscribe(handler: (DataMessage) -> Unit): Set<String> {
        sources.forEach { source ->
            val guid: String = UUID.randomUUID().toString()
            log.info("Subscribing to source - sourceType=${source.type},sourceName=${source.name},subscriptionId=$guid...")
            _subscriptions[guid] = source.subscribe(source.topic) { m ->
                handler(DataMessage(source = source.name, topic = source.topic, payload = m))
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