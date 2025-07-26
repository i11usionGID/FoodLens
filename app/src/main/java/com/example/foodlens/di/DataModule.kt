package com.example.foodlens.di

import com.example.foodlens.data.OcrRepositoryImpl
import com.example.foodlens.data.RuleEngineImpl
import com.example.foodlens.domain.OcrRepository
import com.example.foodlens.domain.RuleEngine
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Dagger Hilt-модуль, предоставляющий зависимости из слоя данных.
 *
 * Используется для внедрения реализаций репозиториев и бизнес-логики
 * в интерфейсы [OcrRepository] и [RuleEngine].
 *
 * Все зависимости живут в [SingletonComponent], т.е. в скоупе всего приложения.
 */
@Module
@InstallIn(SingletonComponent::class)
interface DataModule {

    /**
     * Предоставляет реализацию [OcrRepository] через [OcrRepositoryImpl].
     *
     * @param impl Инстанс реализации OCR.
     * @return Интерфейс [OcrRepository] для внедрения зависимостей.
     */
    @Singleton
    @Binds
    fun bindOcrRepository(
        impl: OcrRepositoryImpl
    ): OcrRepository

    /**
     * Предоставляет реализацию [RuleEngine] через [RuleEngineImpl].
     *
     * @param impl Инстанс движка правил.
     * @return Интерфейс [RuleEngine] для внедрения зависимостей.
     */
    @Singleton
    @Binds
    fun bindRuleEngine(
        impl: RuleEngineImpl
    ): RuleEngine
}