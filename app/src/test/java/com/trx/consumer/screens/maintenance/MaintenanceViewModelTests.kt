package com.trx.consumer.screens.maintenance

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import com.trx.consumer.screens.utils.CoroutineTestRule
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class MaintenanceViewModelTests {

    @get:Rule
    val coroutineTestRule = CoroutineTestRule()

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var eventLoadViewObserver: Observer<MaintenanceViewState>

    @Mock
    private lateinit var eventTapButtonObserver: Observer<Unit>

    private val maintenanceViewModel: MaintenanceViewModel = MaintenanceViewModel()

    @Test
    fun `Verify correct view state observer when doLoadView`() = coroutineTestRule.runBlockingTest {
        maintenanceViewModel.state = MaintenanceViewState.UPDATE
        maintenanceViewModel.eventLoadView.observeForever(eventLoadViewObserver)

        maintenanceViewModel.doLoadView()

        verify(eventLoadViewObserver).onChanged(MaintenanceViewState.UPDATE)
    }

    @Test
    fun `Verify not correct view state observer when doLoadView`() = coroutineTestRule.runBlockingTest {
        maintenanceViewModel.state = MaintenanceViewState.MAINTENANCE
        maintenanceViewModel.eventLoadView.observeForever(eventLoadViewObserver)

        maintenanceViewModel.doLoadView()

        verify(eventLoadViewObserver, times(0)).onChanged(MaintenanceViewState.UPDATE)
    }

    @Test
    fun `Verify action trigger when event tap button clicked`() = coroutineTestRule.runBlockingTest {
        maintenanceViewModel.eventTapButton.observeForever(eventTapButtonObserver)

        maintenanceViewModel.doTapButton()

        verify(eventTapButtonObserver).onChanged(null)
    }
}
