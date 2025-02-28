package io.jrb.labs.rtl433.router.service.aggregator

import io.jrb.labs.rtl433.router.events.FilteredDataEvent
import io.jrb.labs.rtl433.router.service.procesor.workflow.FilterDataContext

class DataSummary {

    var count: Int = 0
    val summary: MutableMap<String, CollectionSummary> = mutableMapOf()

    fun aggregate(event: FilteredDataEvent<*>) {
        val data = event.data as FilterDataContext
        val area = data.rtl433Data?.area ?: "UNKNOWN"
        if (!summary.containsKey(area)) { summary[area] = CollectionSummary() }
        summary[area]?.model(data.rtl433Data?.model ?: "UNKNOWN")
        summary[area]?.device(data.rtl433Data?.device ?: "UNKNOWN")
    }

    fun incrementMessageCount() {
        count += 1
    }

    override fun toString(): String = "count=$count, summary=$summary"

}