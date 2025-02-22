package io.jrb.labs.commons.workflow

import io.jrb.labs.commons.logging.LoggerDelegate

class WorkflowServiceImpl : WorkflowService {

    private val log by LoggerDelegate()

    @Suppress("UNCHECKED_CAST")
    override fun <C : WorkflowContext<C>> run(definition: WorkflowDefinition<C>, context: C): Outcome<C> {
        val flowName = definition.name
        try {
            val updatedContext = context.withWorkflowName(flowName)
            val workflow = buildWorkflow(definition)
            return Outcome.Success(workflow(updatedContext))
        } catch (e: WorkflowErrorException) {
            return e.outcome as Outcome.Error<C>
        } catch (e: WorkflowFailureException) {
            return e.outcome as Outcome.Failure<C>
        } catch(e: Exception) {
            return Outcome.Error(context,e.message ?: "Unexpected workflow error occurred: workflow=$flowName", e)
        }
    }

    private fun <C : WorkflowContext<C>> buildWorkflow(definition: WorkflowDefinition<C>): (C) -> C {
        val flowName = definition.name
        return { initialContext ->
            log.info("Running workflow $flowName")
            definition.steps
                .map { step -> buildWorkflowStep(definition, step) }
                .fold(initialContext) { acc, fn ->
                    when (val result = fn(acc)) {
                        is Outcome.Success -> result.value
                        is Outcome.Failure -> when (result.reason) {
                            Outcome.FailureReason.DATA_ERROR_CONTINUE -> result.value
                            Outcome.FailureReason.DATA_ERROR_EXIT -> throw WorkflowFailureException(flowName, result)
                        }
                        is Outcome.Error -> throw WorkflowErrorException(flowName, result)
                    }
                }
        }
    }

    private fun <C :WorkflowContext<C>> buildWorkflowStep(definition: WorkflowDefinition<C>, step: WorkflowStep<C>): (C) -> Outcome<C> {
        val flowName = definition.name
        val stepName = step.stepName()
        return { context ->
            log.info("Running workflow $flowName, step $stepName")
            try {
                step.apply(context)
            } catch(e: Exception) {
                Outcome.Error(context,"Unexpected workflow step error: workflow=$flowName, step=$stepName", e)
            }
        }
    }

}