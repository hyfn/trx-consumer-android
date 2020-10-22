package com.trx.consumer

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.trx.consumer.module.splash.SplashViewModel
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
    lateinit var handleTapStart: Observer<Void>

    private lateinit var viewModel: SplashViewModel

    @get:Rule
    val rule = InstantTaskExecutorRule()

    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)

        viewModel = SplashViewModel().apply {
            eventLoadView.observeForever(handleLoadView)
            eventTapStart.observeForever(handleTapStart)
        }
    }

    @After
    fun clearMocks() {
        Mockito.framework().clearInlineMocks()
    }

    @Test
    fun testLoadView() {
        viewModel.onLoadView()
        Mockito.verify(handleLoadView).onChanged(null)
    }

    @Test
    fun testTapStart() {
        viewModel.onTapStart()
        Mockito.verify(handleTapStart).onChanged(null)
    }
}
