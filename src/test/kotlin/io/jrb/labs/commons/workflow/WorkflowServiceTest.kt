package io.jrb.labs.commons.workflow

import io.jrb.labs.commons.test.TestUtils
import io.mockk.junit5.MockKExtension
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MockKExtension::class)
class WorkflowServiceTest : TestUtils {

    private lateinit var workflowService: WorkflowService

    @BeforeEach
    fun setup() {
        workflowService = WorkflowServiceImpl()
    }

    @Test
    fun test_NumericWorkflow() {
        val workflow = WorkflowDefinition(randomString(), listOf<WorkflowStep<TestContext>>(
            WorkflowStep { c -> Outcome.Success(c.copy(sum = c.sum + 1)) },
            WorkflowStep { c -> Outcome.Success(c.copy(sum = c.sum * 2)) },
            WorkflowStep { c -> Outcome.Success(c.copy(sum = c.sum + 10)) },
            WorkflowStep { c -> Outcome.Success(c.copy(sum = c.sum / 2)) }
        ))

        val initialContext = TestContext(sum = 0)
        val outcome = workflowService.run(workflow, initialContext)

        assertThat(outcome)
            .isInstanceOf(Outcome.Success::class.java)
            .extracting("value")
            .hasFieldOrPropertyWithValue("workflowName", workflow.name)
            .hasFieldOrPropertyWithValue("sum", 6)
    }

    @Test
    fun test_NumericWorkflowWithError() {
        val workflow = WorkflowDefinition(randomString(), listOf<WorkflowStep<TestContext>>(
            WorkflowStep { c -> Outcome.Success(c.copy(sum = c.sum + 1)) },
            WorkflowStep { _ -> Outcome.Error("EXPECTED", RuntimeException("EXPECTED")) }
        ))

        val initialContext = TestContext(sum = 0)
        val outcome = workflowService.run(workflow, initialContext)

        assertThat(outcome)
            .isInstanceOf(Outcome.Error::class.java)
            .hasFieldOrPropertyWithValue("message", "EXPECTED")
            .extracting("cause")
            .isInstanceOf(RuntimeException::class.java)
    }

    @Test
    fun test_NumericWorkflowWithDataErrorContinue() {
        val workflow = WorkflowDefinition(randomString(), listOf<WorkflowStep<TestContext>>(
            WorkflowStep { c -> Outcome.Success(c.copy(sum = c.sum + 1)) },
            WorkflowStep { c -> Outcome.Failure(Outcome.FailureReason.DATA_ERROR_CONTINUE, c) },
            WorkflowStep { c -> Outcome.Success(c.copy(sum = c.sum * 2)) }
        ))

        val initialContext = TestContext(sum = 10)
        val outcome = workflowService.run(workflow, initialContext)

        assertThat(outcome)
            .isInstanceOf(Outcome.Success::class.java)
            .extracting("value")
            .hasFieldOrPropertyWithValue("workflowName", workflow.name)
            .hasFieldOrPropertyWithValue("sum", 22)
    }

    @Test
    fun test_NumericWorkflowWithDataErrorExit() {
        val workflow = WorkflowDefinition(randomString(), listOf<WorkflowStep<TestContext>>(
            WorkflowStep { c -> Outcome.Success(c.copy(sum = c.sum + 1)) },
            WorkflowStep { c -> Outcome.Failure(Outcome.FailureReason.DATA_ERROR_EXIT, c) },
            WorkflowStep { c -> Outcome.Success(c.copy(sum = c.sum * 2)) }
        ))

        val initialContext = TestContext(sum = 10)
        val outcome = workflowService.run(workflow, initialContext)

        assertThat(outcome)
            .isInstanceOf(Outcome.Failure::class.java)
            .extracting("value")
            .hasFieldOrPropertyWithValue("workflowName", workflow.name)
            .hasFieldOrPropertyWithValue("sum", 11)
    }

}