package org.pazos.fileresolver

import android.app.Application
import android.os.StrictMode

// https://issuetracker.google.com/issues/74514347
@Suppress("unused")
class MainApp : Application() {
    override fun onCreate() {
        super.onCreate()
        // set a lax vm policy to allow sharing file uris
        // across different application domains.
        StrictMode.setVmPolicy(StrictMode.VmPolicy.LAX)
    }
}