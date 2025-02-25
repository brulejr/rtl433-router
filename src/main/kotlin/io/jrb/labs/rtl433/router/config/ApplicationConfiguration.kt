package io.jrb.labs.rtl433.router.config

import io.jrb.labs.commons.eventbus.EventBus
import io.jrb.labs.commons.workflow.WorkflowServiceImpl
import io.jrb.labs.rtl433.router.datafill.SourcesDatafill
import io.jrb.labs.rtl433.router.service.ingester.Source
import io.jrb.labs.rtl433.router.service.ingester.mqtt.MqttSource
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class ApplicationConfiguration {

    @Bean
    fun eventBus() = EventBus()

    @Bean
    fun sources(sourcesDatafill: SourcesDatafill): List<Source> {
        return sourcesDatafill.mqtt.map { source -> MqttSource(source) }
    }

    @Bean
    fun workflowService() = WorkflowServiceImpl()

}