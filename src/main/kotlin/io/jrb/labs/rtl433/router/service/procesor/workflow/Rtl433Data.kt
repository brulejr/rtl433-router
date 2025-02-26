package io.jrb.labs.rtl433.router.service.procesor.workflow

import com.fasterxml.jackson.annotation.JsonAnyGetter
import com.fasterxml.jackson.annotation.JsonAnySetter
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import java.time.Instant

@JsonDeserialize(builder = Rtl433Data.Builder::class)
data class Rtl433Data(
    val type: String? = null,
    val area: String? = null,
    val device: String? = null,
    val time: Instant,
    val model: String,
    val id: String,
    val extraFields: Map<String, Any?> = mapOf()
) {

    operator fun contains(key: String): Boolean = extraFields.containsKey(key)

    operator fun get(key: String): Any? = extraFields[key]

    @JsonAnyGetter
    private fun getProperty(): Map<String, Any?> = extraFields

    class Builder(private var model: String, private var id: String) {

        var type: String? = null

        var area: String? = null

        var device: String? = null

        var time: Instant = Instant.now()

        var extraFields: MutableMap<String, Any?> = mutableMapOf()

        fun build() = Rtl433Data(
            type = type,
            area = area,
            device = device,
            time = time,
            model = model,
            id = id,
            extraFields = extraFields.toMap()
        )

        @JsonAnySetter
        private fun setProperty(key: String, value: Any?) {
            extraFields[key] = value
        }

    }

}
