package com.mxmariner.tides.di

import android.support.v4.app.FragmentActivity
import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.bind
import com.github.salomonbrys.kodein.provider
import com.mxmariner.tides.di.Injector
import com.mxmariner.tides.main.adapter.TidesRecyclerAdapter
import com.mxmariner.tides.main.viewmodel.TidesViewModel
import com.mxmariner.tides.main.viewmodel.TidesViewModelFactory

object SearchModuleInjector {

    /**
     * Gets the [FragmentActivity] scope assembly for the specified activity creating it if necessary
     * which mixes in this project's assembly module(s).
     */
    fun activityScopeAssembly(activity: FragmentActivity) : Kodein {
        return Injector.activityScopeAssembly(activity)
    }
}