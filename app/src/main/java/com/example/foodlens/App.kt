package com.example.foodlens

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/**
 * Главный класс приложения FoodLens, инициализирующий Hilt для внедрения зависимостей.
 *
 * Этот класс помечен аннотацией [HiltAndroidApp], что позволяет Hilt автоматически
 * сгенерировать и подключить необходимые компоненты внедрения зависимостей на уровне приложения.
 *
 * Класс наследуется от [Application] и указывается в `AndroidManifest.xml` как точка входа в приложение.
 *
 * @see <a href="https://developer.android.com/training/dependency-injection/hilt-android">Документация по Hilt</a>
 */
@HiltAndroidApp
class App: Application()