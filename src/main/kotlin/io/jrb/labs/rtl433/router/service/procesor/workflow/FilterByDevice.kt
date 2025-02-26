package io.jrb.labs.rtl433.router.service.procesor.workflow

import io.jrb.labs.commons.workflow.Outcome
import io.jrb.labs.commons.workflow.WorkflowStep
import io.jrb.labs.rtl433.router.datafill.FilterDataWorkflowDatafill
import org.springframework.stereotype.Component

@Component
class FilterByDevice(
    private val workflowDatafill: FilterDataWorkflowDatafill
) : WorkflowStep<FilterDataContext> {

    override fun entryCondition(context: FilterDataContext): Boolean {
        return context.status == FilterDataContext.Status.DATA_ACCEPTED_BY_MODEL
    }

    override fun apply(context: FilterDataContext): Outcome<FilterDataContext> {
        val rtl433Data = context.rtl433Data
        return if (rtl433Data != null) {
            val device = workflowDatafill.devices?.find { it.id == rtl433Data.id  }
            val updatedContext = if (device != null) {
                val updatedRtl433Data = rtl433Data.copy(
                    area = device.area,
                    device = device.name,
                    type = device.type ?: rtl433Data.type,
                )
                context.copy(status = FilterDataContext.Status.DATA_ACCEPTED_BY_ID, rtl433Data = updatedRtl433Data)
            } else {
                context.copy(status = FilterDataContext.Status.DATA_REJECTED_BY_ID)
            }
            Outcome.Success(updatedContext)
        } else {
            Outcome.Failure(value = context, reason = Outcome.FailureReason.DATA_ERROR_EXIT)
        }
    }

}