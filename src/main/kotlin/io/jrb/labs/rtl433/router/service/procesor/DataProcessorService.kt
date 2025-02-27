package io.jrb.labs.rtl433.router.service.procesor

import io.jrb.labs.commons.eventbus.EventBus
import io.jrb.labs.commons.eventbus.SystemEvent
import io.jrb.labs.commons.logging.LoggerDelegate
import io.jrb.labs.commons.workflow.Outcome
import io.jrb.labs.commons.workflow.WorkflowDefinition
import io.jrb.labs.commons.workflow.WorkflowService
import io.jrb.labs.rtl433.router.events.FilteredDataEvent
import io.jrb.labs.rtl433.router.events.RawDataEvent
import io.jrb.labs.rtl433.router.service.ingester.DataMessage
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
                .map { Pair(it.source, processRawDataEvent(it.data as DataMessage)) }
                .collectLatest { (source, context) ->
                    if (context.status == FilterDataContext.Status.DATA_ACCEPTED_BY_ID) {
                        eventBus.invokeEvent(FilteredDataEvent(source, "FILTERED", context.rtl433Data))
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

    private fun processRawDataEvent(message: DataMessage): FilterDataContext {
        val payload = message.payload as String
        val initialContext = FilterDataContext(workflowName = WORKFLOW_NAME, rawJson = payload)
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