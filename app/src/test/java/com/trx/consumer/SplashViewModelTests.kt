package com.trx.consumer

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.trx.consumer.screens.splash.SplashViewModel
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations

class SplashViewModelTests {

    @Mock
    lateinit var handleTapEmail: Observer<Void>

    @Mock
    lateinit var handleTapSignUp: Observer<Void>

    private lateinit var viewModel: SplashViewModel

    @get:Rule
    val rule = InstantTaskExecutorRule()

    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)

        viewModel = SplashViewModel().apply {

            eventTapEmail.observeForever(handleTapEmail)
            eventTapSignUp.observeForever(handleTapSignUp)
        }
    }

    @After
    fun clearMocks() {
        Mockito.framework().clearInlineMocks()
    }

    @Test
    fun testTapEmail() {
        viewModel.doTapEmail()
        Mockito.verify(handleTapEmail).onChanged(null)
    }

    @Test
    fun testTapSignUp() {
        viewModel.doTapSignUp()
        Mockito.verify(handleTapSignUp).onChanged(null)
    }
}
