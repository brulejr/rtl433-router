package io.jrb.labs.commons.workflow

import kotlin.reflect.KClass

data class WorkflowDefinition<C :WorkflowContext<C>>(
    val name: String,
    val contextClass: KClass<C>,
    val steps: List<WorkflowStep<C>>
)
