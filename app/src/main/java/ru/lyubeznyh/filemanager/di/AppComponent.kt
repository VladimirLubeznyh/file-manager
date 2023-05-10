package ru.lyubeznyh.filemanager.di

import android.content.Context
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjectionModule
import dagger.android.AndroidInjector
import dagger.android.support.AndroidSupportInjectionModule
import ru.lyubeznyh.filemanager.FileManagerApp
import ru.lyubeznyh.filemanager.di.module.ActivityModule
import ru.lyubeznyh.filemanager.di.module.DataBaseModule
import ru.lyubeznyh.filemanager.di.module.FragmentModule
import ru.lyubeznyh.filemanager.di.module.RepositoryModule
import javax.inject.Singleton

@Component(
    modules = [
        RepositoryModule::class,
        ActivityModule::class,
        FragmentModule::class,
        DataBaseModule::class,
        AndroidSupportInjectionModule::class,
        AndroidInjectionModule::class
    ]
)
@Singleton
interface AppComponent : AndroidInjector<FileManagerApp> {

    @Component.Builder
    interface Builder {
        @BindsInstance
        fun context(context: Context): Builder

        fun build(): AppComponent
    }
}
