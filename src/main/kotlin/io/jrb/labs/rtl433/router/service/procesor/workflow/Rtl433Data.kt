package io.jrb.labs.rtl433.router.service.procesor.workflow

import com.fasterxml.jackson.annotation.JsonAnyGetter
import com.fasterxml.jackson.annotation.JsonAnySetter
import com.fasterxml.jackson.annotation.JsonIgnore
import java.time.Instant

data class Rtl433Data(
    val type: String? = null,
    val area: String? = null,
    val device: String? = null,
    val time: Instant,
    val model: String,
    val id: String
) {

    private val extraFields: MutableMap<String, Any?> = mutableMapOf()

    @get:JsonIgnore
    val keys: Set<String> get() = setOf("model", "id", "time", "type") + extraFields.keys

    operator fun contains(key: String): Boolean = extraFields.containsKey(key)

    operator fun get(key: String): Any? = extraFields[key]

    override fun toString(): String {
        return javaClass.simpleName + (mapOf("type" to type, "time" to time, "model" to model, "id" to id) + extraFields).toString()
    }

    @JsonAnySetter
    private fun setProperty(key: String, value: Any?) {
        extraFields[key] = value
    }

    @JsonAnyGetter
    private fun getProperty(): Map<String, Any?> = extraFields

}
