package io.jrb.labs.commons.workflow

fun interface WorkflowStep<C : WorkflowContext<C>> {

    fun stepName(): String {
        return javaClass.name
    }

    fun apply(context: C): C

}