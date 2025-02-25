package io.jrb.labs.rtl433.router.service.procesor.workflow

import com.fasterxml.jackson.annotation.JsonAnyGetter
import com.fasterxml.jackson.annotation.JsonAnySetter
import java.time.Instant

data class Rtl433Data(
    val type: String? = null,
    val area: String? = null,
    val device: String? = null,
    val time: Instant,
    val model: String,
    val id: String,
    val extraFields: MutableMap<String, Any?> = mutableMapOf()
) {

    operator fun contains(key: String): Boolean = extraFields.containsKey(key)

    operator fun get(key: String): Any? = extraFields[key]

    @JsonAnySetter
    private fun setProperty(key: String, value: Any?) {
        extraFields[key] = value
    }

    @JsonAnyGetter
    private fun getProperty(): Map<String, Any?> = extraFields

}
