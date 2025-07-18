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

@HiltViewModel(assistedFactory = FoodAnaliseViewModel.Factory::class)
class FoodAnaliseViewModel @AssistedInject constructor(
    @Assisted("photoUri") private val photoUri: Uri,
    private val getTextFromPhotoUseCase: GetTextFromPhotoUseCase,
    private val analiseTextUseCase: AnaliseTextUseCase
): ViewModel() {

    private val _state = MutableStateFlow<FoodAnaliseState>(FoodAnaliseState.Loading)
    val state = _state.asStateFlow()

    init {
        processPhoto()
    }

    private fun processPhoto() {
        viewModelScope.launch {
            try {
                _state.value = FoodAnaliseState.Loading

                val rawText = getTextFromPhotoUseCase(photoUri)

                val analysisResult = analiseTextUseCase(rawText)

                _state.value = FoodAnaliseState.Success(analysisResult)

            } catch (e: Exception) {
                _state.value = FoodAnaliseState.Error("Ошибка при обработке фото: ${e.message}")
            }
        }
    }

    @AssistedFactory
    interface Factory {

        fun create(
            @Assisted("photoUri") photoUri: Uri
        ): FoodAnaliseViewModel
    }
}

sealed interface FoodAnaliseState {
    data object Loading : FoodAnaliseState
    data class Success(val result: ProductAnalysesResult) : FoodAnaliseState
    data class Error(val message: String) : FoodAnaliseState
}