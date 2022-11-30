package kg.foodbambook.bambook.ui.main.category

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class DishViewModelFactory constructor(val context: Context, val id: Int): ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return DishViewModel(context, id) as T
    }

}