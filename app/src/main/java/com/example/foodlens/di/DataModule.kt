package com.example.foodlens.di

import com.example.foodlens.data.FormatResultEngineImpl
import com.example.foodlens.data.OcrRepositoryImpl
import com.example.foodlens.data.RuleEngineImpl
import com.example.foodlens.domain.FormatResultEngine
import com.example.foodlens.domain.OcrRepository
import com.example.foodlens.domain.RuleEngine
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface DataModule {

    @Singleton
    @Binds
    fun bindOcrRepository(
        impl: OcrRepositoryImpl
    ): OcrRepository

    @Singleton
    @Binds
    fun bindRuleEngine(
        impl: RuleEngineImpl
    ): RuleEngine

    @Singleton
    @Binds
    fun bindFormatResultEngine(
        impl: FormatResultEngineImpl
    ): FormatResultEngine
}