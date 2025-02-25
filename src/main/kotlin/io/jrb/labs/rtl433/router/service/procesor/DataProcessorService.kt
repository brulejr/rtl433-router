package io.jrb.labs.rtl433.router.service.procesor

import io.jrb.labs.commons.eventbus.DataEvent
import io.jrb.labs.commons.eventbus.EventBus
import io.jrb.labs.commons.eventbus.SystemEvent
import io.jrb.labs.commons.logging.LoggerDelegate
import io.jrb.labs.commons.workflow.Outcome
import io.jrb.labs.commons.workflow.WorkflowDefinition
import io.jrb.labs.commons.workflow.WorkflowService
import io.jrb.labs.rtl433.router.service.ingester.DataMessage
import io.jrb.labs.rtl433.router.service.procesor.workflow.FilterDataContext
import io.jrb.labs.rtl433.router.service.procesor.workflow.FilterDataWorkflowDefinition.Companion.WORKFLOW_NAME
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.flowOf
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

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun start() {
        log.info("Starting {}...", _serviceName)
        _scope.launch {
            eventBus.events(DataEvent::class)
                .flatMapConcat { receiveDataEvent(it.data as DataMessage) }
                .collectLatest { log.info("Received: {}", it) }
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

    private fun receiveDataEvent(message: DataMessage): Flow<*> {
        val payload = message.payload as String
        val initialContext = FilterDataContext(workflowName = WORKFLOW_NAME, rawJson = payload)
        return when (val result: Outcome<FilterDataContext> = workflowService.run(filterDataWorkflow, initialContext)) {
            is Outcome.Success -> flowOf(result.value.rtl433Data)
            is Outcome.Failure -> {
                log.warn("FAILURE: reason=${result.reason}")
                flowOf(result.value.rtl433Data)
            }
            is Outcome.Error -> throw RuntimeException(result.message, result.cause)
        }
    }

}