package io.jrb.labs.commons.workflow

class WorkflowServiceImpl : WorkflowService {

    override fun <C : WorkflowContext<C>> run(definition: WorkflowDefinition<C>, context: C): C {
        val updatedContext = context.withWorkflowName(definition.name)
        val workflow = buildWorkflow(definition)
        return workflow(updatedContext)
    }

    private fun <C : WorkflowContext<C>> buildWorkflow(definition: WorkflowDefinition<C>): (C) -> C {
        return { initialContext ->
            definition.steps
                .map { step -> buildWorkflowStep(definition, step) }
                .fold(initialContext) { acc, fn -> fn(acc) }
        }
    }

    private fun <C :WorkflowContext<C>> buildWorkflowStep(definition: WorkflowDefinition<C>, step: WorkflowStep<C>): (C) -> C {
        return { context ->
            step.apply(context)
        }
    }

}