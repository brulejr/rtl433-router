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

import io.jrb.labs.commons.workflow.simple.Outcome
import io.jrb.labs.commons.workflow.simple.WorkflowStep
import io.jrb.labs.rtl433.router.datafill.FilterDataWorkflowDatafill
import org.springframework.stereotype.Component

@Component
class FilterByModel(
    private val workflowDatafill: FilterDataWorkflowDatafill
) : WorkflowStep<FilterDataContext> {

    override fun apply(context: FilterDataContext): Outcome<FilterDataContext> {
        val rtl433Data = context.rtl433Data
        return if (rtl433Data != null) {
            val modelDefinition = workflowDatafill.models?.find { it.name == rtl433Data.model  }
            val updatedContext = if (modelDefinition != null) {
                val updatedRtl433Data = rtl433Data.copy(type = modelDefinition.type)
                context.copy(status = FilterDataContext.Status.DATA_ACCEPTED_BY_MODEL, rtl433Data = updatedRtl433Data)
            } else {
                context.copy(status = FilterDataContext.Status.DATA_REJECTED_BY_MODEL)
            }
            Outcome.Success(updatedContext)
        } else {
            Outcome.Failure(value = context, reason = Outcome.FailureReason.DATA_ERROR_EXIT)
        }
    }

}