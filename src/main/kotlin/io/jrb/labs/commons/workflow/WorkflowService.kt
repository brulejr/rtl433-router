package io.jrb.labs.commons.workflow

interface WorkflowService {

    fun <C : WorkflowContext<C>> run(definition: WorkflowDefinition<C>, context: C): C

}