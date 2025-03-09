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
 *
 *
 */
package io.jrb.labs.commons.workflow.coroutine

sealed class StepOutcome<C : WorkflowContext<C>> {

    data class Success<C : WorkflowContext<C>>(val context: C) : StepOutcome<C>()

    data class Failure<C : WorkflowContext<C>>(
        val context: C, val reason: FailureReason
    ) : StepOutcome<C>()

    data class Error<C : WorkflowContext<C>>(
        val context: C, val message: String, val exception: Exception?
    ) : StepOutcome<C>()

    data class Skipped<C : WorkflowContext<C>>(
        val context: C, val reason: SkippedReason = SkippedReason.GUARD_CHECK_VIOLATION
    ) : StepOutcome<C>()

    enum class FailureReason {
        DATA_ERROR_CONTINUE,
        DATA_ERROR_EXIT
    }

    enum class SkippedReason {
        GUARD_CHECK_VIOLATION
    }

}