package ru.lyubeznyh.filemanager

import dagger.android.AndroidInjector
import dagger.android.DaggerApplication
import ru.lyubeznyh.filemanager.di.DaggerAppComponent

class FileManagerApp:DaggerApplication() {
    override fun applicationInjector(): AndroidInjector<out DaggerApplication> =
        DaggerAppComponent
            .builder()
            .context(this)
            .build()
}
