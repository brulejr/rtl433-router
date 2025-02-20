package io.jrb.labs.commons.workflow

import io.jrb.labs.commons.test.TestUtils
import io.mockk.junit5.MockKExtension
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MockKExtension::class)
class WorkflowServiceImplTest : TestUtils {

    private lateinit var workflowService: WorkflowService
    private lateinit var workflowDefinition: WorkflowDefinition<TestContext>

    @BeforeEach
    fun setup() {
        val steps = listOf<WorkflowStep<TestContext>>(
            WorkflowStep { c -> c.copy(sum = c.sum + 1) },
            WorkflowStep { c -> c.copy(sum = c.sum * 2) },
            WorkflowStep { c -> c.copy(sum = c.sum + 10) },
            WorkflowStep { c -> c.copy(sum = c.sum / 2) }
        )
        workflowService = WorkflowServiceImpl()
        workflowDefinition = WorkflowDefinition(randomString(), steps)
    }

    @Test
    fun test_SUCCESS1() {
        val initialContext = TestContext(sum = 0)
        val finalContext = workflowService.run(workflowDefinition, initialContext)
        assertThat(finalContext)
            .hasFieldOrPropertyWithValue("workflowName", workflowDefinition.name)
            .hasFieldOrPropertyWithValue("sum", 6)
    }

    @Test
    fun test_SUCCESS2() {
        val initialContext = TestContext(sum = 10)
        val finalContext = workflowService.run(workflowDefinition, initialContext)
        assertThat(finalContext)
            .hasFieldOrPropertyWithValue("workflowName", workflowDefinition.name)
            .hasFieldOrPropertyWithValue("sum", 16)
    }

}