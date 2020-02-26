package mx.oaraiza.demodebs.database;

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface DemoDAO {

    @Insert
    fun insertDebt(newDebt:DebtEntity)

    @Delete
    fun deleteDebt(newDebt: DebtEntity)

    @Query("SELECT * FROM DebtEntity ORDER BY fecha DESC")
    fun getDebts():LiveData<List<DebtEntity>>
}
