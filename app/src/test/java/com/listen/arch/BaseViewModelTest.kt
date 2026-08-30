package com.listen.arch

import com.listen.arch.mvi.BaseViewModel
import com.listen.arch.mvi.CommonUiEffect
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

data class TestState(val count: Int = 0)
sealed interface TestIntent {
    data object Increment : TestIntent
    data class Add(val amount: Int) : TestIntent
}
sealed interface TestEffect : CommonUiEffect {
    data class ShowCount(val count: Int) : TestEffect
}

class SampleViewModel : BaseViewModel<TestState, TestIntent>(TestState()) {
    override fun handleIntent(intent: TestIntent) {
        when (intent) {
            is TestIntent.Increment -> updateState { copy(count = count + 1) }
            is TestIntent.Add -> {
                val newCount = viewState.value.count + intent.amount
                updateState { copy(count = newCount) }
            }
        }
    }

    fun emitTestEffect(effect: TestEffect) {
        emitEffect(effect)
    }
}

@OptIn(ExperimentalCoroutinesApi::class)
class BaseViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun testHandleIntentAndStateUpdate() = runTest(testDispatcher) {
        val vm = SampleViewModel()
        assertEquals(0, vm.viewState.value.count)

        vm.handleIntent(TestIntent.Increment)
        testDispatcher.scheduler.advanceUntilIdle()
        assertEquals(1, vm.viewState.value.count)

        vm.handleIntent(TestIntent.Add(5))
        testDispatcher.scheduler.advanceUntilIdle()
        assertEquals(6, vm.viewState.value.count)
    }

    @Test
    fun testEffectEmission() = runTest(testDispatcher) {
        val vm = SampleViewModel()
        var receivedEffect: TestEffect? = null

        val job = launch {
            receivedEffect = vm.viewEffect.first() as? TestEffect
        }

        vm.emitTestEffect(TestEffect.ShowCount(10))
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(TestEffect.ShowCount(10), receivedEffect)
        job.cancel()
    }
}
