package com.chanpal.target.application

import android.app.Application
import kotlin.properties.Delegates

class TargetApplication : Application() {
    companion object {

    var instance: TargetApplication by Delegates.notNull()

    fun instance() =
        instance
}

    override fun onCreate() {
        super.onCreate()
        instance = this
    }

}