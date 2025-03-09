package io.jrb.labs.commons.workflow.coroutine

import io.jrb.labs.commons.logging.LoggerDelegate
import io.jrb.labs.commons.test.TestUtils
import io.jrb.labs.commons.workflow.simple.Outcome
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class WorkflowEngineTest : TestUtils {

    private val log by LoggerDelegate()

    private lateinit var workflowEngine: WorkflowEngine

    @BeforeEach
    fun setup() {
        workflowEngine = WorkflowEngine()
    }

    @Test
    fun testWorkflowEngine() {
        val workflow = WorkflowDefinition(randomString(), TestContext::class, listOf(
            TestStepA(),
            TestStepB(),
            TestStepC()
        ))

        val initialContext = TestContext().apply {
            data["enableStepC"] = false
        }

        runBlocking {
            workflowEngine.runWorkflow(workflow, initialContext).collect { outcome ->
                when (outcome) {
                    is StepOutcome.Success -> println("SUCCESS: ${outcome.context.data}")
                    is StepOutcome.Failure -> println("FAILURE: ${outcome.reason}")
                    is StepOutcome.Error -> println("ERROR: ${outcome.exception?.message}")
                    is StepOutcome.Skipped -> println("SKIPPED: ${outcome.reason}")
                }
            }
        }

    }

}