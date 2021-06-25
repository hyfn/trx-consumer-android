package com.trx.consumer.screens.permissions

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import androidx.lifecycle.viewModelScope
import com.nhaarman.mockitokotlin2.verify
import com.trx.consumer.models.common.PermissionModel
import com.trx.consumer.screens.utils.MainCoroutineRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class PermissionViewModelTest {

    @ExperimentalCoroutinesApi
    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var eventLoadViewObserver: Observer<List<PermissionModel>>

    @Mock
    private lateinit var eventTapButtonObserver: Observer<Void>

    private val permissionViewModel: PermissionsViewModel = PermissionsViewModel()

    @Test
    fun testLoadViewWithCorrectListSize() {
        permissionViewModel.viewModelScope.launch {
            permissionViewModel.eventLoadView.observeForever(eventLoadViewObserver)

            permissionViewModel.doLoadView()

            val expected = permissionViewModel.permissionList
            verify(eventLoadViewObserver).onChanged(expected)
        }
    }

    @Test
    fun testTapButton() {
        permissionViewModel.viewModelScope.launch {
            permissionViewModel.eventTapBack.observeForever(eventTapButtonObserver)

            permissionViewModel.doTapBack()

            verify(eventTapButtonObserver).onChanged(null)
        }
    }
}
