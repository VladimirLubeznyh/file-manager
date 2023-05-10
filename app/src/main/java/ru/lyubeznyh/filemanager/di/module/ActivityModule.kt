package ru.lyubeznyh.filemanager.di.module

import dagger.Module
import dagger.android.ContributesAndroidInjector
import ru.lyubeznyh.filemanager.presentation.MainActivity

@Module
interface ActivityModule {

    @ContributesAndroidInjector
    fun mainActivityInject(): MainActivity
}
