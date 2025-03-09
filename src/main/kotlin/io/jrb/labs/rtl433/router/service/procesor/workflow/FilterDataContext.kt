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

import io.jrb.labs.commons.workflow.simple.WorkflowContext

data class FilterDataContext(

    override val workflowName: String,

    val rawJson: String? = null,

    val rtl433Data: Rtl433Data? = null,

    val status: Status = Status.CONTEXT_CREATED

) : WorkflowContext<FilterDataContext> {

    override fun withWorkflowName(workflowName: String): FilterDataContext {
        return copy(workflowName = workflowName)
    }

    enum class Status {
        CONTEXT_CREATED,
        DATA_INGESTED,
        DATA_ACCEPTED_BY_MODEL,
        DATA_REJECTED_BY_MODEL,
        DATA_ACCEPTED_BY_ID,
        DATA_REJECTED_BY_ID
    }

}
