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
package io.jrb.labs.rtl433.router.service.procesor

import io.jrb.labs.commons.eventbus.EventBus
import io.jrb.labs.commons.eventbus.SystemEvent
import io.jrb.labs.commons.logging.LoggerDelegate
import io.jrb.labs.commons.workflow.Outcome
import io.jrb.labs.commons.workflow.WorkflowDefinition
import io.jrb.labs.commons.workflow.WorkflowService
import io.jrb.labs.rtl433.router.model.FilteredDataEvent
import io.jrb.labs.rtl433.router.model.RawDataEvent
import io.jrb.labs.rtl433.router.service.procesor.workflow.FilterDataContext
import io.jrb.labs.rtl433.router.service.procesor.workflow.FilterDataWorkflowDefinition.Companion.WORKFLOW_NAME
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import org.springframework.context.SmartLifecycle
import org.springframework.stereotype.Service
import java.util.concurrent.atomic.AtomicBoolean

@Service
class DataProcessorService(
    private val filterDataWorkflow: WorkflowDefinition<FilterDataContext>,
    private val workflowService: WorkflowService,
    private val eventBus: EventBus
) : SmartLifecycle {

    private val log by LoggerDelegate()

    private val _serviceName = javaClass.simpleName
    private val _running: AtomicBoolean = AtomicBoolean()

    private val _scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    override fun start() {
        log.info("Starting {}...", _serviceName)
        _scope.launch {
            eventBus.events(RawDataEvent::class)
                .map { Pair(it, processRawDataEvent(it.data as String)) }
                .collectLatest { (event, context) ->
                    if (context.status == FilterDataContext.Status.DATA_ACCEPTED_BY_ID) {
                        eventBus.invokeEvent(FilteredDataEvent(event, context))
                    }
                }
        }
        eventBus.sendEvent(SystemEvent("service.start", _serviceName))
        _running.getAndSet(true)
    }

    override fun stop() {
        log.info("Stopping {}...", _serviceName)
        eventBus.sendEvent(SystemEvent("service.stop", _serviceName))
        _running.getAndSet(true)
    }

    override fun isRunning(): Boolean {
        return _running.get()
    }

    private fun processRawDataEvent(message: String): FilterDataContext {
        val initialContext = FilterDataContext(workflowName = WORKFLOW_NAME, rawJson = message)
        return when (val result: Outcome<FilterDataContext> = workflowService.run(filterDataWorkflow, initialContext)) {
            is Outcome.Success -> result.value
            is Outcome.Skipped -> result.value
            is Outcome.Failure -> {
                log.warn("FAILURE: reason=${result.reason}")
                result.value
            }
            is Outcome.Error -> throw RuntimeException(result.message, result.cause)
        }
    }

}