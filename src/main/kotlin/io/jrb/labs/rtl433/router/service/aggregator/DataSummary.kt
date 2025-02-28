/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2025 Jon Brule <brulejr@gmail.com>
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
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