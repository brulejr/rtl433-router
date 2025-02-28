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
package io.jrb.labs.rtl433.router.service.procesor.workflow

import com.fasterxml.jackson.databind.ObjectMapper
import io.jrb.labs.commons.workflow.Outcome
import io.jrb.labs.commons.workflow.WorkflowStep
import org.springframework.stereotype.Component

@Component
class ReadJsonAsRtl433Data(
    private val objectMapper: ObjectMapper
) : WorkflowStep<FilterDataContext> {

    override fun apply(context: FilterDataContext): Outcome<FilterDataContext> {
        val rtl433Data = objectMapper.readValue(context.rawJson, Rtl433Data::class.java)
        return Outcome.Success(context.copy(rtl433Data = rtl433Data, status = FilterDataContext.Status.DATA_INGESTED))
    }

}