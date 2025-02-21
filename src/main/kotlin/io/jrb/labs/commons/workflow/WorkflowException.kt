package io.jrb.labs.commons.workflow

import io.jrb.labs.commons.service.ServiceException

open class WorkflowException(workflow: String, cause: Throwable?, message: String? = null): ServiceException(
    message = message ?: "An unexpected error occurred in workflow $workflow",
    cause = cause
)

class WorkflowErrorException(workflow: String, val outcome: Outcome.Error): WorkflowException(
    workflow = workflow,
    message = "Exiting workflow $workflow due to error",
    cause = null
)

class WorkflowFailureException(workflow: String, val outcome: Outcome.Failure<Any>): WorkflowException(
    workflow = workflow,
    message = "Exiting workflow $workflow due to failure",
    cause = null
)