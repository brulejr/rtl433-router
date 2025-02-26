package io.jrb.labs.rtl433.router.service.procesor.workflow

import io.jrb.labs.commons.workflow.Outcome
import io.jrb.labs.commons.workflow.WorkflowStep
import org.springframework.stereotype.Component

@Component
class NormalizeDeviceData : WorkflowStep<FilterDataContext> {

    override fun entryCondition(context: FilterDataContext): Boolean {
        return context.status == FilterDataContext.Status.DATA_ACCEPTED_BY_ID
    }

    override fun apply(context: FilterDataContext): Outcome<FilterDataContext> {
        val rtl433Data = context.rtl433Data

        val fields = updateFields(rtl433Data?.extraFields, CELSIUS_SUFFIX, FAHRENHEIT_SUFFIX, CELSIUS_TO_FAHRENHEIT_FN)
        val updatedRtl433Data = rtl433Data?.copy(extraFields = rtl433Data.extraFields + fields)

        return Outcome.Success(context.copy(rtl433Data = updatedRtl433Data))
    }

    private fun updateFields(fields: Map<String, Any?>?, suffix: String, newSuffix: String, updateFn: (Any?) -> Any?): Map<String, Any?> {
        return fields?.entries
            ?.filter { it.key.endsWith(suffix) }
            ?.associate { (key, value) -> key.removeSuffix(suffix).plus(newSuffix) to updateFn(value) }
            ?: emptyMap()
    }

    companion object {
        const val CELSIUS_SUFFIX = "_C"
        const val FAHRENHEIT_SUFFIX = "_F"
        val CELSIUS_TO_FAHRENHEIT_FN: (Any?) -> Any? = { if (it is Number) (it as Double * 9/5) + 32 else null }
    }

}