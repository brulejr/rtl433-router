/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2025 Jon Brule <brulejr@gmail.com>
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package io.jrb.labs.commons.workflow.simple

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
            return Outcome.Error(context, e.message ?: "Unexpected workflow error occurred: workflow=$flowName", e)
        }
    }

    private fun <C : WorkflowContext<C>> buildWorkflow(definition: WorkflowDefinition<C>): (C) -> C {
        val flowName = definition.name
        return { initialContext ->
            log.debug("Running workflow $flowName")
            definition.steps
                .map { step -> buildWorkflowStep(definition, step) }
                .fold(initialContext) { acc, fn ->
                    when (val result = fn(acc)) {
                        is Outcome.Success -> result.value
                        is Outcome.Skipped -> result.value
                        is Outcome.Failure -> when (result.reason) {
                            Outcome.FailureReason.DATA_ERROR_CONTINUE -> result.value
                            Outcome.FailureReason.DATA_ERROR_EXIT -> throw WorkflowFailureException(flowName, result)
                        }
                        is Outcome.Error -> throw WorkflowErrorException(flowName, result)
                    }
                }
        }
    }

    private fun <C : WorkflowContext<C>> buildWorkflowStep(definition: WorkflowDefinition<C>, step: WorkflowStep<C>): (C) -> Outcome<C> {
        val flowName = definition.name
        val stepName = step.stepName()
        return { context ->
            if (step.entryCondition(context)) {
                log.debug("Running workflow step $flowName.$stepName")
                try {
                    step.apply(context)
                } catch(e: Exception) {
                    Outcome.Error(context, "Unexpected workflow step error: workflow=$flowName, step=$stepName", e)
                }
            } else {
                log.debug("Skipping workflow step $flowName.$stepName")
                Outcome.Skipped(context)
            }
        }
    }

}