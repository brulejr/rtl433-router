package io.jrb.labs.commons.workflow

data class TestContext(

    override val workflowName: String = "",
    val sum: Int

) : WorkflowContext<TestContext> {

    override fun withWorkflowName(workflowName: String): TestContext {
        return copy(workflowName = workflowName)
    }

}