package io.jrb.labs.commons.workflow

data class WorkflowDefinition<C :WorkflowContext<C>>(
    val name: String,
    val steps: List<WorkflowStep<C>>
)
