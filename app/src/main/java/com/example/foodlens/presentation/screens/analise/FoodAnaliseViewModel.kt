package com.example.foodlens.presentation.screens.analise

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.foodlens.domain.AnaliseTextUseCase
import com.example.foodlens.domain.GetTextFromPhotoUseCase
import com.example.foodlens.domain.model.ProductAnalysesResult
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel для экрана показа результатов анализа продуктов.
 *
 * Загружает изображение, распознаёт текст и выполняет анализ состава продукта,
 * используя [GetTextFromPhotoUseCase] и [AnaliseTextUseCase]. Состояние анализа хранится в [state].
 *
 * @param photoUri URI изображения, переданного для анализа.
 * @param getTextFromPhotoUseCase use case для получения текста с фотографии.
 * @param analiseTextUseCase use case для анализа полученного текста.
 */
@HiltViewModel(assistedFactory = FoodAnaliseViewModel.Factory::class)
class FoodAnaliseViewModel @AssistedInject constructor(
    @Assisted("photoUri") private val photoUri: Uri,
    private val getTextFromPhotoUseCase: GetTextFromPhotoUseCase,
    private val analiseTextUseCase: AnaliseTextUseCase
): ViewModel() {

    /**
     * Приватное состояние, управляющее текущим статусом анализа.
     */
    private val _state = MutableStateFlow<FoodAnaliseState>(FoodAnaliseState.Loading)
    /**
     * Публичный поток состояния анализа. Используется UI для отображения состояния.
     */
    val state = _state.asStateFlow()

    /**
     * Инициализирует анализ фотографии при создании ViewModel.
     */
    init {
        processPhoto()
    }

    /**
     * Обрабатывает фото: извлекает текст и анализирует его.
     *
     * В случае успешного анализа состояние переводится в [FoodAnaliseState.Success],
     * при ошибке — в [FoodAnaliseState.Error].
     */
    private fun processPhoto() {
        viewModelScope.launch {
            _state.value = FoodAnaliseState.Loading
            try {
                val rawText = getTextFromPhotoUseCase(photoUri)

                val analysisResult = analiseTextUseCase(rawText)

                _state.value = FoodAnaliseState.Success(analysisResult)

            } catch (e: IllegalArgumentException) {
                _state.value = FoodAnaliseState.Error("${e.message}")
            } catch (e: Exception) {
                _state.value = FoodAnaliseState.Error("Ошибка при обработке фото: ${e.message}")
            }
        }
    }

    /**
     * Фабрика для создания [FoodAnaliseViewModel] с параметром [photoUri].
     */
    @AssistedFactory
    interface Factory {

        fun create(
            @Assisted("photoUri") photoUri: Uri
        ): FoodAnaliseViewModel
    }
}

/**
 * Состояние UI экрана анализа.
 */
sealed interface FoodAnaliseState {

    /**
     * Состояние загрузки и обработки изображения.
     */
    data object Loading : FoodAnaliseState

    /**
     * Состояние успешного анализа.
     *
     * @param result результат анализа состава продукта.
     */
    data class Success(val result: ProductAnalysesResult) : FoodAnaliseState

    /**
     * Состояние ошибки при анализе.
     *
     * @param message сообщение об ошибке.
     */
    data class Error(val message: String) : FoodAnaliseState
}