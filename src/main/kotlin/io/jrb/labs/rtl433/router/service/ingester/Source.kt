package io.jrb.labs.rtl433.router.service.ingester

import reactor.core.Disposable

interface Source {

    val name: String

    val topic: String

    val type: String

    fun connect()

    fun disconnect()

    fun subscribe(topic: String, handler: (String) -> Unit): Disposable?

}