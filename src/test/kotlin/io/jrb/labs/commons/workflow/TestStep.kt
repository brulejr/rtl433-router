package io.jrb.labs.commons.workflow

class TestStep1 : WorkflowStep<TestContext> {
    override fun apply(context: TestContext): TestContext {
        return context.copy(sum = context.sum + 1)
    }
}

class TestStep2 : WorkflowStep<TestContext> {
    override fun apply(context: TestContext): TestContext {
        return context.copy(sum = context.sum * 2)
    }
}

class TestStep3 : WorkflowStep<TestContext> {
    override fun apply(context: TestContext): TestContext {
        return context.copy(sum = context.sum + 10)
    }
}

class TestStep4 : WorkflowStep<TestContext> {
    override fun apply(context: TestContext): TestContext {
        return context.copy(sum = context.sum / 2)
    }
}