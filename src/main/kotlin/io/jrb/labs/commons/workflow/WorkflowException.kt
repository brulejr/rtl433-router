package io.jrb.labs.commons.workflow

import io.jrb.labs.commons.service.ServiceException

open class WorkflowException(workflow: String, cause: Throwable?, message: String? = null): ServiceException(
    message = message ?: "An unexpected error occurred in workflow $workflow",
    cause = cause
)

class WorkflowExitException(workflow: String, val outcome: Outcome.Error): WorkflowException(
    workflow = workflow,
    message = "Exiting workflow $workflow",
    cause = null
)