package io.jrb.labs.rtl433.router.service.procesor.workflow

import io.jrb.labs.commons.workflow.WorkflowContext

data class FilterDataContext(

    override val workflowName: String,

    val rawJson: String? = null,

    val rtl433Data: Rtl433Data? = null,

    val status: Status = Status.CONTEXT_CREATED

) : WorkflowContext<FilterDataContext> {

    override fun withWorkflowName(workflowName: String): FilterDataContext {
        return copy(workflowName = workflowName)
    }

    enum class Status {
        CONTEXT_CREATED,
        DATA_INGESTED,
        DATA_ACCEPTED_BY_MODEL,
        DATA_REJECTED_BY_MODEL,
        DATA_ACCEPTED_BY_ID,
        DATA_REJECTED_BY_ID
    }

}
