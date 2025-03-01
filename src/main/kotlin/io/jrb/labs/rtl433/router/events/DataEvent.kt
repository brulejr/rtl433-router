package io.jrb.labs.rtl433.router.events

import io.jrb.labs.commons.eventbus.Event
import java.time.Instant
import java.util.UUID

interface DataEvent<out T> : Event<T> {

    val id: UUID

    val timestamp: Instant

    val topic: String

}