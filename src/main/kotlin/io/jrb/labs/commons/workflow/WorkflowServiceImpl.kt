package io.jrb.labs.commons.workflow

import io.jrb.labs.commons.logging.LoggerDelegate

class WorkflowServiceImpl : WorkflowService {

    private val log by LoggerDelegate()

    override fun <C : WorkflowContext<C>> run(definition: WorkflowDefinition<C>, context: C): Outcome<C> {
        try {
            val updatedContext = context.withWorkflowName(definition.name)
            val workflow = buildWorkflow(definition)
            return Outcome.Success(workflow(updatedContext))
        } catch(e: Exception) {
            return Outcome.Error(e.message ?: "", e)
        }
    }

    private fun <C : WorkflowContext<C>> buildWorkflow(definition: WorkflowDefinition<C>): (C) -> C {
        val flowName = definition.name
        return { initialContext ->
            log.info("Running workflow $flowName")
            try {
                definition.steps
                    .map { step -> buildWorkflowStep(definition, step) }
                    .fold(initialContext) { acc, fn -> fn(acc) }
            } catch(e: WorkflowStepException) {
                throw e
            } catch(e: Exception) {
                throw WorkflowException(flowName, e)
            }
        }
    }

    private fun <C :WorkflowContext<C>> buildWorkflowStep(definition: WorkflowDefinition<C>, step: WorkflowStep<C>): (C) -> C {
        val flowName = definition.name
        val stepName = step.stepName()
        return { context ->
            log.info("Running workflow $flowName, step $stepName")
            try {
                step.apply(context)
            } catch(e: Exception) {
                throw WorkflowStepException(flowName, stepName, e)
            }
        }
    }

}