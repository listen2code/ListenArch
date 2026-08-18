package com.listen.arch

import com.listen.arch.mvi.asResult
import com.listen.arch.mvi.safeCall
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ResultExtensionsTest {

    @Test
    fun testSafeCallSuccess() {
        val res = safeCall { 10 + 20 }
        assertTrue(res.isSuccess)
        assertEquals(30, res.getOrNull())
    }

    @Test
    fun testSafeCallFailure() {
        val res = safeCall<Int> { throw IllegalStateException("Custom error") }
        assertTrue(res.isFailure)
        assertEquals("Custom error", res.exceptionOrNull()?.message)
    }

    @Test
    fun testFlowAsResult() = runBlocking {
        val successFlow = flow {
            emit("Hello")
            emit("World")
        }.asResult().toList()

        assertEquals(2, successFlow.size)
        assertTrue(successFlow[0].isSuccess)
        assertEquals("Hello", successFlow[0].getOrNull())
        assertTrue(successFlow[1].isSuccess)
        assertEquals("World", successFlow[1].getOrNull())

        val errorFlow = flow<String> {
            emit("First")
            throw RuntimeException("Flow error")
        }.asResult().toList()

        assertEquals(2, errorFlow.size)
        assertTrue(errorFlow[0].isSuccess)
        assertTrue(errorFlow[1].isFailure)
        assertEquals("Flow error", errorFlow[1].exceptionOrNull()?.message)
    }
}
