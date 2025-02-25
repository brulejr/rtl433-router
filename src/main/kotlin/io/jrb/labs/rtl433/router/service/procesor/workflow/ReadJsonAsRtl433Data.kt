package io.jrb.labs.rtl433.router.service.procesor.workflow

import com.fasterxml.jackson.databind.ObjectMapper
import io.jrb.labs.commons.workflow.Outcome
import io.jrb.labs.commons.workflow.WorkflowStep
import org.springframework.stereotype.Component

@Component
class ReadJsonAsRtl433Data(
    private val objectMapper: ObjectMapper
) : WorkflowStep<FilterDataContext> {

    override fun apply(context: FilterDataContext): Outcome<FilterDataContext> {
        val rtl433Data = objectMapper.readValue(context.rawJson, Rtl433Data::class.java)
        return Outcome.Success(context.copy(rtl433Data = rtl433Data, status = FilterDataContext.Status.DATA_INGESTED))
    }

}