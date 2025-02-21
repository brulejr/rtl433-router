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
            WorkflowStep { c -> c.copy(sum = c.sum + 1) },
            WorkflowStep { c -> c.copy(sum = c.sum * 2) },
            WorkflowStep { c -> c.copy(sum = c.sum + 10) },
            WorkflowStep { c -> c.copy(sum = c.sum / 2) }
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
    fun test_NumericWorkflowWithException() {
        val workflow = WorkflowDefinition(randomString(), listOf<WorkflowStep<TestContext>>(
            WorkflowStep { c -> c.copy(sum = c.sum + 1) },
            WorkflowStep { _ -> throw RuntimeException("EXPECTED") }
        ))

        val initialContext = TestContext(sum = 0)
        val outcome = workflowService.run(workflow, initialContext)

        assertThat(outcome)
            .isInstanceOf(Outcome.Error::class.java)
            .hasFieldOrPropertyWithValue("message", "An error occurred while processing workflow ${workflow.name} step WorkflowServiceTest\$test_NumericWorkflowWithException\$workflow\$2")
            .extracting("cause")
            .isInstanceOf(WorkflowStepException::class.java)
    }

}