package com.trx.consumer.screens.maintenance

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import androidx.lifecycle.viewModelScope
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import com.trx.consumer.screens.utils.MainCoroutineRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class MaintenanceViewModelTests {

    @ExperimentalCoroutinesApi
    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var eventLoadViewObserver: Observer<MaintenanceViewState>

    @Mock
    private lateinit var eventTapButtonObserver: Observer<Unit>

    private val maintenanceViewModel: MaintenanceViewModel = MaintenanceViewModel()

    @Test
    fun testLoadView() {
        maintenanceViewModel.viewModelScope.launch {
            maintenanceViewModel.state = MaintenanceViewState.UPDATE
            maintenanceViewModel.eventLoadView.observeForever(eventLoadViewObserver)

            maintenanceViewModel.doLoadView()

            verify(eventLoadViewObserver).onChanged(MaintenanceViewState.UPDATE)
        }
    }

    @Test
    fun testLoadViewWithDoNotCorrectState() {
        maintenanceViewModel.viewModelScope.launch {
            maintenanceViewModel.state = MaintenanceViewState.MAINTENANCE
            maintenanceViewModel.eventLoadView.observeForever(eventLoadViewObserver)

            maintenanceViewModel.doLoadView()

            verify(eventLoadViewObserver, times(0)).onChanged(MaintenanceViewState.UPDATE)
        }
    }

    @Test
    fun testTapButton() {
        maintenanceViewModel.viewModelScope.launch {
            maintenanceViewModel.eventTapButton.observeForever(eventTapButtonObserver)

            maintenanceViewModel.doTapButton()

            verify(eventTapButtonObserver).onChanged(null)
        }
    }
}
