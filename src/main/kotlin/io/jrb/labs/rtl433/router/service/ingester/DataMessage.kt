package io.jrb.labs.rtl433.router.service.ingester

import java.time.Instant
import java.util.UUID

data class DataMessage(

    val id: UUID = UUID.randomUUID(),

    val timestamp: Instant = Instant.now(),

    val type: String = "RAW",

    val source: String,

    val topic: String,

    val payload: Any

) {

    fun getPayloadType(): String  = payload.javaClass.simpleName

}
