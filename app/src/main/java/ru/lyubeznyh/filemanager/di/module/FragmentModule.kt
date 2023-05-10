package ru.lyubeznyh.filemanager.di.module

import dagger.Module
import dagger.Provides
import dagger.android.ContributesAndroidInjector
import ru.lyubeznyh.filemanager.presentation.filespage.FilesFragment
import javax.inject.Singleton

@Module
interface FragmentModule {

    @ContributesAndroidInjector
    fun filesFragmentInject(): FilesFragment
}
