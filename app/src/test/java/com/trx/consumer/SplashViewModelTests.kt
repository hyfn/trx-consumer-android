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
    lateinit var handleLoadView: Observer<Void>

    @Mock
    lateinit var handleTapFacebook: Observer<Void>

    @Mock
    lateinit var handleTapGoogle: Observer<Void>

    @Mock
    lateinit var handleTapApple: Observer<Void>

    @Mock
    lateinit var handleTapEmail: Observer<Void>

    private lateinit var viewModel: SplashViewModel

    @get:Rule
    val rule = InstantTaskExecutorRule()

    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)

        viewModel = SplashViewModel().apply {
            eventLoadView.observeForever(handleLoadView)
            eventTapFacebook.observeForever(handleTapFacebook)
            eventTapGoogle.observeForever(handleTapGoogle)
            eventTapApple.observeForever(handleTapApple)
            eventTapEmail.observeForever(handleTapEmail)
        }
    }

    @After
    fun clearMocks() {
        Mockito.framework().clearInlineMocks()
    }

    @Test
    fun testLoadView() {
        viewModel.doLoadView()
        Mockito.verify(handleLoadView).onChanged(null)
    }

    @Test
    fun testTapFacebook() {
        viewModel.doTapFacebook()
        Mockito.verify(handleTapFacebook).onChanged(null)
    }

    @Test
    fun testTapGoogle() {
        viewModel.doTapGoogle()
        Mockito.verify(handleTapGoogle).onChanged(null)
    }

    @Test
    fun testTapApple() {
        viewModel.doTapApple()
        Mockito.verify(handleTapApple).onChanged(null)
    }

    @Test
    fun testTapEmail() {
        viewModel.doTapEmail()
        Mockito.verify(handleTapEmail).onChanged(null)
    }
}
