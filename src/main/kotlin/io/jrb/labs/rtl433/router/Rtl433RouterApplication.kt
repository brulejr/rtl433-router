package io.jrb.labs.rtl433.router

import io.jrb.labs.rtl433.router.datafill.MqttDatafill
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication

@SpringBootApplication
@EnableConfigurationProperties(MqttDatafill::class)
class Rtl433RouterApplication

fun main(args: Array<String>) {
	runApplication<Rtl433RouterApplication>(*args)
}
