package com.firdaus1453.storyapp.util

import androidx.activity.ComponentActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData

fun <T> ComponentActivity.observe(liveData: LiveData<T>, action: (t: T) -> Unit) {
    liveData.observe(this) { it?.let { t -> action(t) } }
}

fun <T> Fragment.observe(liveData: LiveData<T>, action: (t: T) -> Unit) {
    liveData.observe(this) { it?.let { t -> action(t) } }
}