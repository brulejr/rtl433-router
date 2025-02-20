package io.jrb.labs.commons.workflow

import io.jrb.labs.commons.service.ServiceException

open class WorkflowException(private val workflow: String, cause: Throwable?, message: String? = null): ServiceException(
    message = message ?: "An error occurred while processing workflow $workflow",
    cause = cause
)

open class WorkflowStepException(private val workflow: String, private val step: String, cause: Throwable?, message: String? = null): ServiceException(
    message = message ?: "An error occurred while processing workflow $workflow step $step",
    cause = cause
)