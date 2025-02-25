package io.jrb.labs.rtl433.router.datafill

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "application.router.filter-data-workflow")
data class FilterDataWorkflowDatafill(
    val models: Set<ModelDefinition>? = setOf(),
    val devices: Set<DeviceDefinition>? = setOf()
)
