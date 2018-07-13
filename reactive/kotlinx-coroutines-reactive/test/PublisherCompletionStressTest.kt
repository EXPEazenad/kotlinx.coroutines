/*
 * Copyright 2016-2018 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.coroutines.reactive

import kotlinx.coroutines.DefaultDispatcher
import kotlinx.coroutines.TestBase
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import org.junit.Test
import java.util.*
import kotlin.coroutines.CoroutineContext

class PublisherCompletionStressTest : TestBase() {
    val N_REPEATS = 10_000 * stressTestMultiplier

    fun range(context: CoroutineContext, start: Int, count: Int) = publish<Int>(context) {
        for (x in start until start + count) send(x)
    }

    @Test
    fun testCompletion() {
        val rnd = Random()
        repeat(N_REPEATS) {
            val count = rnd.nextInt(5)
            runBlocking {
                withTimeout(5000) {
                    var received = 0
                    range(DefaultDispatcher, 1, count).consumeEach { x ->
                        received++
                        if (x != received) error("$x != $received")
                    }
                    if (received != count) error("$received != $count")
                }
            }
        }
    }
}