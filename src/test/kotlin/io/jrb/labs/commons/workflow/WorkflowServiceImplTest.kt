package io.jrb.labs.commons.workflow

import io.mockk.junit5.MockKExtension
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MockKExtension::class)
class WorkflowServiceImplTest {

    private lateinit var workflowService: WorkflowService
    private lateinit var workflowDefinition: WorkflowDefinition<TestContext>

    @BeforeEach
    fun setup() {
        workflowService = WorkflowServiceImpl()
        workflowDefinition = WorkflowDefinition("test", listOf(
            TestStep1(),
            TestStep2(),
            TestStep3(),
            TestStep4()
        ))
    }

    @Test
    fun testSUCCESS() {
        val initialContext = TestContext(workflowName = "", sum = 0)
        val finalContext = workflowService.run(workflowDefinition, initialContext)
        assertThat(finalContext)
            .hasFieldOrPropertyWithValue("workflowName", "test")
            .hasFieldOrPropertyWithValue("sum", 6)
    }

}