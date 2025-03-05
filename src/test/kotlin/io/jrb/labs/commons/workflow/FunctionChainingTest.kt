package io.jrb.labs.commons.workflow

import io.jrb.labs.commons.test.TestUtils
import io.jrb.labs.commons.workflow.FunctionChainingTest.StepType1
import io.jrb.labs.commons.workflow.FunctionChainingTest.StepType2
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class FunctionChainingTest : TestUtils {

    @Test
    fun `should execute a simple synchronous chain`() {

        val steps = listOf<(String) -> String>(
            { s -> "${s}a" },
            { s -> "${s}b" },
            { s -> "${s}c" },
            { s -> "${s}d" }
        )
        val chain = steps.reduce { acc, fn -> { acc(it).let(fn) } }

        val result = chain("1")

        assertThat(result).isEqualTo("1abcd")
    }

    @Test
    fun `should execute a simple numeric chain`() {
        val steps = listOf<(TestContext) -> TestContext>(
            { c -> c.copy(sum = c.sum + 1) },
            { c -> c.copy(sum = c.sum * 2) },
            { c -> c.copy(sum = c.sum + 10) },
            { c -> c.copy(sum = c.sum / 2) }
        )
        val chain = steps.reduce { acc, nextfn -> { acc(it).let(nextfn) } }

        val result = chain(TestContext(sum = 1))

        assertThat(result)
            .hasFieldOrPropertyWithValue("sum", 7)
    }

    @Test
    fun `should execute a type 1 named numeric chain`() {
        val steps = listOf(
            StepType1 { c -> c.copy(sum = c.sum + 1) },
            StepType1 { c -> c.copy(sum = c.sum * 2) },
            StepType1 { c -> c.copy(sum = c.sum + 10) },
            StepType1 { c -> c.copy(sum = c.sum / 2) }
        )

        val chain = steps.reduce { acc, nextfn -> StepType1 { acc(it).let(nextfn) } }

        val result = chain(TestContext(sum = 5))

        assertThat(result)
            .hasFieldOrPropertyWithValue("sum", 11)
    }

    @Test
    fun `should execute a type 2 named numeric chain`() {
        val steps = listOf(
            StepType2 { c -> c.copy(sum = c.sum + 1) },
            StepType2 { c -> c.copy(sum = c.sum * 2) },
            StepType2 { c -> c.copy(sum = c.sum + 10) },
            StepType2 { c -> c.copy(sum = c.sum / 2) }
        )

        val chain = steps.reduce { acc, nextfn -> StepType2 { acc.execute(it).let(nextfn::execute) } }

        val result = chain.execute(TestContext(sum = 5))

        assertThat(result)
            .hasFieldOrPropertyWithValue("sum", 11)
    }

    @Test
    fun `should execute a type 3 named numeric chain - happy path`() {
        val steps = listOf(
            StepType3 { c -> Outcome.Success(c.copy(sum = c.sum + 1)) },
            StepType3 { c -> Outcome.Success(c.copy(sum = c.sum * 2)) },
            StepType3 { c -> Outcome.Success(c.copy(sum = c.sum + 10)) },
            StepType3 { c -> Outcome.Success(c.copy(sum = c.sum / 2)) }
        )

        val chain = steps.reduce { acc, nextfn -> StepType3 {
            acc.execute(it)
            .takeIf { it is Outcome.Success }.let { it as Outcome.Success }.value
            .let(nextfn::execute)
        } }

        val result = chain.execute(TestContext(sum = 5))

        assertThat(result)
            .isInstanceOf(Outcome.Success::class.java)
            .extracting("value")
            .hasFieldOrPropertyWithValue("sum", 11)
    }

    private fun interface StepType1 : (TestContext) -> TestContext

    private fun interface StepType2 {
        fun execute(context: TestContext): TestContext
    }

    private fun interface StepType3 {
        fun execute(context: TestContext): Outcome<TestContext>
    }

}