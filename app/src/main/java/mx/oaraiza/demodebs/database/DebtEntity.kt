package mx.oaraiza.demodebs.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.sql.Date

@Entity
class DebtEntity (
    @PrimaryKey(autoGenerate = true) var id: Int = 0,
    var nombre: String,
    var cantidad: Float,
    var fecha : Date? = Date(System.currentTimeMillis())
)
