package io.jrb.labs.rtl433.router.config

import io.jrb.labs.commons.eventbus.EventBus
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class ApplicationJavaConfig {

    @Bean
    fun eventBus() = EventBus()

}