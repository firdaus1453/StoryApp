package com.firdaus1453.storyapp.util

import androidx.activity.ComponentActivity
import androidx.lifecycle.LiveData

fun <T> ComponentActivity.observe(liveData: LiveData<T>, action: (t: T) -> Unit) {
    liveData.observe(this) { it?.let { t -> action(t) } }
}