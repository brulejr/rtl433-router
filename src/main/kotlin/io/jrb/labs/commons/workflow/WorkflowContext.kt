package io.jrb.labs.commons.workflow

interface WorkflowContext<C : WorkflowContext<C>> {

    fun withWorkflowName(workflowName: String): C

}