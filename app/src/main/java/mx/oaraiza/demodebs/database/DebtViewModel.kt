package mx.oaraiza.demodebs.database

import android.app.Activity
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class DebtViewModel(application: Application):  AndroidViewModel(application){
    // The ViewModel maintains a reference to the repository to get data.
    private val repository: DebtRepository
    // LiveData gives us updated words when they change.
    val allDebts: LiveData<List<DebtEntity>>

    init {
        // Gets reference to WordDao from WordRoomDatabase to construct
        // the correct WordRepository.
        val debtsDao = DebtDB.getDatabase(application, viewModelScope).getDao()
        repository = DebtRepository(debtsDao)
        allDebts = repository.allDebts
    }
    fun insert(debtEntity: DebtEntity) = viewModelScope.launch {
        repository.insertDebt(debtEntity)
    }
}