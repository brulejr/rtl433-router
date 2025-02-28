package io.jrb.labs.rtl433.router.service.aggregator

import com.fasterxml.jackson.annotation.JsonProperty

class CollectionSummary {

    @JsonProperty("device")
    val deviceMap = mutableMapOf<String, Int>()

    @JsonProperty("model")
    val modelMap = mutableMapOf<String, Int>()

    fun device(key: String) {
        deviceMap[key] = deviceMap.getOrPut(key) { 0 }.inc()
    }

    fun model(key: String) {
        modelMap[key] = modelMap.getOrPut(key) { 0 }.inc()
    }

}