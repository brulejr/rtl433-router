package io.jrb.labs.commons.workflow

import io.jrb.labs.commons.test.TestUtils
import io.jrb.labs.commons.workflow.FunctionChainingTest.StepType1
import io.jrb.labs.commons.workflow.FunctionChainingTest.StepType2
import kotlinx.coroutines.runBlocking
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
                .takeIf { o -> o is Outcome.Success }.let { o -> (o as Outcome.Success).value }
                .let(nextfn::execute)
        } }

        val result = chain.execute(TestContext(sum = 5))

        assertThat(result)
            .isInstanceOf(Outcome.Success::class.java)
            .extracting("value")
            .hasFieldOrPropertyWithValue("sum", 11)
    }

    @Test
    fun `should concurrently execute a type 1 named numeric chain`() {
        val steps = listOf(
            StepType1Concurrent { c -> c.copy(sum = c.sum + 1) },
            StepType1Concurrent { c -> c.copy(sum = c.sum * 2) },
            StepType1Concurrent { c -> c.copy(sum = c.sum + 10) },
            StepType1Concurrent { c -> c.copy(sum = c.sum / 2) }
        )

        val chain = steps.reduce { acc, nextfn -> StepType1Concurrent { nextfn(acc(it)) } }

        runBlocking {
            val result = chain(TestContext(sum = 6))

            assertThat(result)
                .hasFieldOrPropertyWithValue("sum", 12)
        }
    }

    @Test
    fun `should concurrently execute a type 2 named numeric chain`() {
        val steps = listOf(
            StepType2Concurrent { c -> c.copy(sum = c.sum + 1) },
            StepType2Concurrent { c -> c.copy(sum = c.sum * 2) },
            StepType2Concurrent { c -> c.copy(sum = c.sum + 10) },
            StepType2Concurrent { c -> c.copy(sum = c.sum / 2) }
        )

        val chain = steps.reduce { acc, nextfn -> StepType2Concurrent { nextfn.execute(acc.execute(it)) } }

        runBlocking {
            val result = chain.execute(TestContext(sum = 5))

            assertThat(result)
                .hasFieldOrPropertyWithValue("sum", 11)
        }
    }

    @Test
    fun `should concurrently execute a type 3 named numeric chain - happy path`() {
        val steps = listOf(
            StepType3Concurrent { c -> Outcome.Success(c.copy(sum = c.sum + 1)) },
            StepType3Concurrent { c -> Outcome.Success(c.copy(sum = c.sum * 2)) },
            StepType3Concurrent { c -> Outcome.Success(c.copy(sum = c.sum + 10)) },
            StepType3Concurrent { c -> Outcome.Success(c.copy(sum = c.sum / 2)) }
        )

        val chain = steps.reduce { acc, nextfn -> StepType3Concurrent {
            nextfn.execute(
                when (val outcome = acc.execute(it)) {
                    is Outcome.Success -> outcome.value
                    else -> it
                }
            )
        } }

        runBlocking {
            val result = chain.execute(TestContext(sum = 5))

            assertThat(result)
                .isInstanceOf(Outcome.Success::class.java)
                .extracting("value")
                .hasFieldOrPropertyWithValue("sum", 11)
        }
    }

    private fun interface StepType1 : (TestContext) -> TestContext

    private fun interface StepType1Concurrent : suspend (TestContext) -> TestContext

    private fun interface StepType2 {
        fun execute(context: TestContext): TestContext
    }

    private fun interface StepType2Concurrent {
        suspend fun execute(context: TestContext): TestContext
    }

    private fun interface StepType3 {
        fun execute(context: TestContext): Outcome<TestContext>
    }

    private fun interface StepType3Concurrent {
        suspend fun execute(context: TestContext): Outcome<TestContext>
    }

}