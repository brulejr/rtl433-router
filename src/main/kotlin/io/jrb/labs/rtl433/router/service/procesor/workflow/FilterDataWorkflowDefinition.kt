package io.jrb.labs.rtl433.router.service.procesor.workflow

import io.jrb.labs.commons.workflow.WorkflowDefinition
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class FilterDataWorkflowDefinition {

    companion object {
        const val WORKFLOW_NAME = "FILTER_DATA"
    }

    @Bean
    fun filterDataWorkflow(
        readJsonAsRtl433Data: ReadJsonAsRtl433Data,
        filterByModel: FilterByModel,
        filterByDevice: FilterByDevice
    ): WorkflowDefinition<FilterDataContext> {
        return WorkflowDefinition(name = WORKFLOW_NAME, contextClass = FilterDataContext::class, steps = listOf(
            readJsonAsRtl433Data,
            filterByModel,
            filterByDevice
        ))
    }

}