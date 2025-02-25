package io.jrb.labs.rtl433.router.service.procesor.workflow

import io.jrb.labs.commons.workflow.Outcome
import io.jrb.labs.commons.workflow.WorkflowStep
import io.jrb.labs.rtl433.router.datafill.FilterDataWorkflowDatafill
import org.springframework.stereotype.Component

@Component
class FilterByModel(
    private val workflowDatafill: FilterDataWorkflowDatafill
) : WorkflowStep<FilterDataContext> {

    override fun apply(context: FilterDataContext): Outcome<FilterDataContext> {
        val rtl433Data = context.rtl433Data
        return if (rtl433Data != null) {
            val model = rtl433Data.model
            val modelDefinition = workflowDatafill.models?.find { it.name == model  }
            val updatedContext = if (modelDefinition != null) {
                val updatedRtl433Data = rtl433Data.copy(type = modelDefinition.type)
                context.copy(status = FilterDataContext.Status.DATA_ACCEPTED_BY_MODEL, rtl433Data = updatedRtl433Data)
            } else {
                context.copy(status = FilterDataContext.Status.DATA_REJECTED_BY_MODEL)
            }
            Outcome.Success(updatedContext)
        } else {
            Outcome.Failure(value = context, reason = Outcome.FailureReason.DATA_ERROR_EXIT)
        }
    }

}