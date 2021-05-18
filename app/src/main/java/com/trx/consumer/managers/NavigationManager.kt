package com.trx.consumer.managers

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import androidx.annotation.IdRes
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.trx.consumer.R

class NavigationManager {

    var isGuestMode: Boolean = false

    private val extraParcelable = "EXTRA_PARCELABLE"
    private val extraAny = "EXTRA_ANY"

    private val listTab = listOf(
        R.id.home_fragment,
        R.id.virtual_fragment,
        R.id.live_fragment,
        R.id.video_fragment
    )

    private val listIgnoreTab = listOf<Int>()

    private fun handleItemListener(activity: FragmentActivity, navController: NavController) =
        BottomNavigationView.OnNavigationItemSelectedListener { menuItem ->
            NavigationUI.onNavDestinationSelected(menuItem, navController)
        }

    private fun handleDestinationChangeListener(activity: FragmentActivity) =
        NavController.OnDestinationChangedListener { _, destination, _ ->
            if (!listIgnoreTab.contains(destination.id)) {
                getTabBar(activity).isVisible = listTab.contains(destination.id)
            }
        }

    fun launch(activity: FragmentActivity, isLoggedIn: Boolean) {
        val navHostFragment = activity
            .supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController
        val graphInflater = navController.navInflater
        val graph = graphInflater.inflate(R.navigation.nav_main)
        graph.startDestination = if (isLoggedIn) R.id.home_fragment else R.id.splash_fragment
        navController.graph = graph
        getTabBar(activity).apply {
            setupWithNavController(navController)
            setOnNavigationItemSelectedListener(handleItemListener(activity, navController))
            setOnNavigationItemReselectedListener { }
        }
        navController.addOnDestinationChangedListener(handleDestinationChangeListener(activity))
    }

    fun notLoggedInLaunchSequence(fragment: Fragment) {
        launchSequence(fragment.requireActivity(), login = false)
    }

    fun loggedInLaunchSequence(fragment: Fragment) {
        launchSequence(fragment.requireActivity())
    }

    fun guestLaunchSequence(fragment: Fragment) {
        launchSequence(fragment.requireActivity(), guest = true)
    }

    fun loadTab(activity: FragmentActivity, show: Boolean) {
        getTabBar(activity).isVisible = show
    }

    private fun launchSequence(
        activity: FragmentActivity,
        login: Boolean = true,
        guest: Boolean = false
    ) {
        isGuestMode = guest
        getTabBar(activity).apply {
            menu.clear()
            inflateMenu(R.menu.menu_bottom_nav)
        }
        val navController = getNavController(activity)
        val start = if (login) R.id.home_fragment else R.id.splash_fragment
        val graph = navController.graph
        graph.startDestination = start
        navController.graph = graph
    }

    fun params(fragment: Fragment): Any? {
        return extras(fragment.arguments)
    }

    fun params(intent: Intent): Any? {
        return extras(intent.extras)
    }

    private fun extras(bundle: Bundle?): Any? {
        var extraP: Any? = bundle?.getParcelable(extraParcelable)
        extraP?.let { safeP -> return safeP }
        extraP = bundle?.get(extraAny)
        extraP?.let { safeA -> return safeA }
        return null
    }

    fun present(source: Fragment, @IdRes fragment: Int, params: Any? = null) {
        val bundle = when (params) {
            is Parcelable -> bundleOf(extraParcelable to params)
            else -> bundleOf(extraAny to params)
        }
        show(source, fragment, bundle)
    }

    fun <T : Activity> presentActivity(source: Activity, activity: Class<T>, params: Any? = null) {
        val bundle = when (params) {
            is Parcelable -> bundleOf(extraParcelable to params)
            else -> bundleOf(extraAny to params)
        }
        Intent(source, activity).apply {
            putExtras(bundle)
            source.startActivity(this)
        }
    }

    private fun show(fragment: Fragment, @IdRes destination: Int, bundle: Bundle? = null) {
        getNavController(fragment.requireActivity()).navigate(destination, bundle)
    }

    fun getNavController(activity: FragmentActivity): NavController {
        val navHostFragment = activity
            .supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        return navHostFragment.navController
    }

    fun dismiss(fragment: Fragment) {
        getNavController(fragment.requireActivity()).navigateUp()
    }

    fun dismiss(from: Fragment, @IdRes to: Int?, model: Any? = null) {
        val navController = getNavController(from.requireActivity())
        to?.let {
            try {
                val backStackEntry = navController.getBackStackEntry(to)
                val arguments = backStackEntry.arguments
                val params = model
                    ?: arguments?.getParcelable(extraParcelable)
                    ?: arguments?.get(extraAny)

                navController.popBackStack(to, true)
                present(from, to, params)
            } catch (e: IllegalArgumentException) {
                if (to in listTab) popRoot(navController) else popTab(navController)
                present(from, to, model)
            }
        } ?: popTab(navController)
    }

    @SuppressLint("RestrictedApi")
    private fun popTab(navController: NavController) {
        navController.backStack.map { it.destination.id }.lastOrNull { it in listTab }?.let {
            navController.popBackStack(it, false)
        } ?: popRoot(navController)
    }

    private fun popRoot(navController: NavController) {
        val startDestination = navController.graph.startDestination
        navController.popBackStack(startDestination, false)
    }

    fun currentFragment(activity: FragmentActivity): Int? {
        return getNavController(activity).currentDestination?.id
    }

    fun previousFragment(activity: FragmentActivity): Int? {
        return getNavController(activity).previousBackStackEntry?.destination?.id
    }

    private fun getTabBar(activity: FragmentActivity): BottomNavigationView {
        return activity.findViewById(R.id.tabBar)
    }



    companion object {

        val shared: NavigationManager = getInstance()

        @Volatile
        private var instance: NavigationManager? = null

        private fun init(): NavigationManager = instance ?: synchronized(this) {
            instance ?: NavigationManager().also {
                instance = it
            }
        }

        private fun getInstance(): NavigationManager =
            instance ?: init()
    }
}
