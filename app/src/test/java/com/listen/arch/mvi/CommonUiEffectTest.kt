package com.listen.arch.mvi

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class CommonUiEffectTest {

    @Test
    fun `test ShowToast properties`() {
        val effect = CommonUiEffect.ShowToast("Test Message")
        assertEquals("Test Message", effect.message)
    }

    @Test
    fun `test ShowSnackbar default properties`() {
        val effect = CommonUiEffect.ShowSnackbar("Test Snackbar")
        assertEquals("Test Snackbar", effect.message)
        assertNull(effect.actionLabel)
        assertNull(effect.onAction)
    }

    @Test
    fun `test ShowSnackbar with all properties`() {
        var actionTriggered = false
        val effect = CommonUiEffect.ShowSnackbar(
            message = "Test Snackbar",
            actionLabel = "Retry",
            onAction = { actionTriggered = true }
        )
        assertEquals("Test Snackbar", effect.message)
        assertEquals("Retry", effect.actionLabel)
        effect.onAction?.invoke()
        assertTrue(actionTriggered)
    }

    @Test
    fun `test ShareText properties`() {
        val effect = CommonUiEffect.ShareText("Title", "Content")
        assertEquals("Title", effect.title)
        assertEquals("Content", effect.content)
    }
    
    @Test
    fun `test NavigateTo properties`() {
        val effect = CommonUiEffect.NavigateTo("/home")
        assertEquals("/home", effect.route)
    }

    @Test
    fun `test OpenUrl properties`() {
        val effect = CommonUiEffect.OpenUrl("https://github.com")
        assertEquals("https://github.com", effect.url)
    }

    @Test
    fun `test object types`() {
        assertTrue(CommonUiEffect.NavigateBack is CommonUiEffect)
        assertTrue(CommonUiEffect.HideKeyboard is CommonUiEffect)
    }
}
