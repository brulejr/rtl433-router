package io.jrb.labs.rtl433.router.service.ingester

interface Source {

    fun connect()

    fun disconnect()

    fun source(): String

}