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
package io.jrb.labs.commons.test

import org.apache.commons.lang3.RandomStringUtils
import java.time.Instant
import java.util.*
import java.util.concurrent.ThreadLocalRandom

interface TestUtils {

    fun randomBoolean() = ThreadLocalRandom.current().nextBoolean()

    fun <E : Enum<E>> randomEnum(enumClass: Class<E>): E {
        val pos = ThreadLocalRandom.current().nextInt(0, enumClass.enumConstants.size - 1)
        return enumClass.enumConstants[pos]
    }

    fun randomGuid(): UUID = UUID.randomUUID()

    fun randomDouble(upperLimit: Double = 1000.00) = ThreadLocalRandom.current().nextDouble(1.00, upperLimit)

    fun randomInt(upperLimit: Int = 1000) = ThreadLocalRandom.current().nextInt(1, upperLimit)

    fun <T> randomList(maxSize: Int = 3, supplier: () -> T): List<T> {
        val size = ThreadLocalRandom.current().nextInt(1, maxSize)
        return (1..size).map { supplier.invoke() }
    }

    fun randomLong(upperLimit: Long = 1000L): Long = ThreadLocalRandom.current().nextLong(0, upperLimit)

    fun <K, V> randomMap(maxSize: Int = 3, keySupplier: () -> K, valueSupplier: () -> V): Map<K, V> {
        val size = ThreadLocalRandom.current().nextInt(1, maxSize)
        return (1..size).associate { keySupplier.invoke() to valueSupplier.invoke() }
    }

    fun <T> randomSet(maxSize: Int = 3, supplier: () -> T): Set<T> {
        val size = ThreadLocalRandom.current().nextInt(1, maxSize)
        return (1..size).map { supplier.invoke() }.toSet()
    }

    fun randomString(): String = RandomStringUtils.randomAlphabetic(10)

    fun randomTimestamp(): Instant = Instant.ofEpochSecond(ThreadLocalRandom.current().nextInt().toLong())

}