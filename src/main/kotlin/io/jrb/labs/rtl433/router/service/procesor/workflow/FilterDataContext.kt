package io.jrb.labs.rtl433.router.service.procesor.workflow

import io.jrb.labs.commons.workflow.WorkflowContext

data class FilterDataContext(

    override val workflowName: String,

    val rawJson: String? = null,

    val rtl433Data: Rtl433Data? = null

) : WorkflowContext<FilterDataContext> {

    override fun withWorkflowName(workflowName: String): FilterDataContext {
        return copy(workflowName = workflowName)
    }

}
