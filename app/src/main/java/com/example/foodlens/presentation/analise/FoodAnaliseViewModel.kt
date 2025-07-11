package com.example.foodlens.presentation.analise

import android.net.Uri
import androidx.lifecycle.ViewModel
import com.example.foodlens.domain.AnaliseTextUseCase
import com.example.foodlens.domain.FormatResultUseCase
import com.example.foodlens.domain.GetTextFromPhotoUseCase
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel

@HiltViewModel(assistedFactory = FoodAnaliseViewModel.Factory::class)
class FoodAnaliseViewModel @AssistedInject constructor(
    @Assisted("photoUri") private val photoUri: Uri,
    private val getTextFromPhotoUseCase: GetTextFromPhotoUseCase,
    private val analiseTextUseCase: AnaliseTextUseCase,
    private val formatResultUseCase: FormatResultUseCase
): ViewModel() {

    @AssistedFactory
    interface Factory {

        fun create(
            @Assisted("photoUri") photoUri: Uri
        ): FoodAnaliseViewModel
    }
}