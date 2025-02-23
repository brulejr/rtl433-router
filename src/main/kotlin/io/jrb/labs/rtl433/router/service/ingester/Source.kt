package io.jrb.labs.rtl433.router.service.ingester

import reactor.core.Disposable

interface Source {

    fun connect()

    fun disconnect()

    fun name(): String

    fun subscribe(topic: String, handler: (String) -> Unit): Disposable?

    fun topic(): String

    fun type(): String

}