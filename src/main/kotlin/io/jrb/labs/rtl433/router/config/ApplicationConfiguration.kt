/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2025 Jon Brule <brulejr@gmail.com>
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package io.jrb.labs.rtl433.router.config

import io.jrb.labs.commons.eventbus.EventBus
import io.jrb.labs.commons.workflow.simple.WorkflowServiceImpl
import io.jrb.labs.rtl433.router.datafill.SourcesDatafill
import io.jrb.labs.rtl433.router.datafill.TargetsDatafill
import io.jrb.labs.rtl433.router.service.ingester.Source
import io.jrb.labs.rtl433.router.service.ingester.mqtt.MqttSource
import io.jrb.labs.rtl433.router.service.publisher.Target
import io.jrb.labs.rtl433.router.service.publisher.mqtt.MqttTarget
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
    fun targets(targetsDatafill: TargetsDatafill): List<Target> {
        return targetsDatafill.mqtt.map { target -> MqttTarget(target) }
    }

    @Bean
    fun workflowService() = WorkflowServiceImpl()

}