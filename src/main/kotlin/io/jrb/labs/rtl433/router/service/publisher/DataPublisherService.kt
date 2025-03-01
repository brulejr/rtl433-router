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
package io.jrb.labs.rtl433.router.service.publisher

import com.fasterxml.jackson.databind.ObjectMapper
import io.jrb.labs.commons.eventbus.EventBus
import io.jrb.labs.commons.eventbus.SystemEvent
import io.jrb.labs.commons.logging.LoggerDelegate
import io.jrb.labs.rtl433.router.model.FilteredDataEvent
import io.jrb.labs.rtl433.router.service.procesor.workflow.Rtl433Data
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.springframework.context.SmartLifecycle
import org.springframework.stereotype.Service
import java.util.concurrent.atomic.AtomicBoolean

@Service
class DataPublisherService(
    private val targets: List<Target>,
    private val objectMapper: ObjectMapper,
    private val eventBus: EventBus
): SmartLifecycle {

    private val log by LoggerDelegate()

    private val _serviceName = javaClass.simpleName
    private val _running: AtomicBoolean = AtomicBoolean()

    private val _scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    override fun start() {
        log.info("Starting {}...", _serviceName)
        startTargets()
        _scope.launch {
            eventBus.events(FilteredDataEvent::class)
                .collectLatest { event ->
                    log.info("Publishing data {}", event.data)
                    publishMessage(event.data as Rtl433Data)
                }
        }
        eventBus.sendEvent(SystemEvent("service.start", _serviceName))
        _running.getAndSet(true)
    }

    override fun stop() {
        log.info("Stopping {}...", _serviceName)
        stopTargets()
        eventBus.sendEvent(SystemEvent("service.stop", _serviceName))
        _running.getAndSet(true)
    }

    override fun isRunning(): Boolean {
        return _running.get()
    }

    private fun publishMessage(data: Rtl433Data) {
        val target = targets.find { it.name == "ha" }
        val topic = "ha_events/${data.area}/${data.device}"
        val message = objectMapper.writeValueAsString(data)
        target?.publish(topic, message)
    }

    private fun startTargets() {
        targets.forEach { target ->
            log.info("Starting target - sourceType=${target.type},sourceName=${target.name}...")
            target.connect()
        }
    }

    private fun stopTargets() {
        targets.forEach { target ->
            log.info("Stopping target - sourceType=${target.type},sourceName=${target.name}...")
            target.disconnect()
        }
    }

}