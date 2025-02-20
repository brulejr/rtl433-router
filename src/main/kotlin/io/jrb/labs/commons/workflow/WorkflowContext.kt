package io.jrb.labs.commons.workflow

interface WorkflowContext<C : WorkflowContext<C>> {

    val workflowName: String

    fun withWorkflowName(workflowName: String): C

}