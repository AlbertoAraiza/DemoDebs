package mx.oaraiza.demodebs.database

import androidx.lifecycle.LiveData

class DebtRepository(private val debtDAO: DemoDAO) {
    val allDebts: LiveData<List<DebtEntity>> = debtDAO.getDebts()

    suspend fun insertDebt(debt: DebtEntity) {
        debtDAO.insertDebt(debt)
    }

    suspend fun deleteDebt(debt: DebtEntity) {
        debtDAO.deleteDebt(debt)
    }
}