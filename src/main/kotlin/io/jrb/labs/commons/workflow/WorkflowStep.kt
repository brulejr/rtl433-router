package io.jrb.labs.commons.workflow

@FunctionalInterface
interface WorkflowStep<C : WorkflowContext<C>> {

    fun stepName(): String {
        return javaClass.simpleName
    }

    fun apply(context: C): C

}