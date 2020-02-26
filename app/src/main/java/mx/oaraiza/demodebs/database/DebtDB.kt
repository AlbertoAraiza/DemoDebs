package mx.oaraiza.demodebs.database

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.sql.Date
import java.sql.Timestamp
import java.time.Instant
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

@Database(entities = arrayOf(DebtEntity::class), version = 1, exportSchema = false)
public abstract class DebtDB:RoomDatabase(){
    abstract fun getDao() : DemoDAO

    companion object{
        @Volatile
        private var INSTANCE : DebtDB? = null

        fun getDatabase(context: Context, scope: CoroutineScope): DebtDB {
            val tempInstance = INSTANCE
            if (tempInstance != null) {
                return tempInstance
            }
            synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    DebtDB::class.java,
                    "debt_database"
                ).addCallback(DebtDatabaseCallback(scope)).build()
                INSTANCE = instance
                return instance
            }
        }
    }
    private class DebtDatabaseCallback(
        private val scope: CoroutineScope
    ) : RoomDatabase.Callback() {

        @RequiresApi(Build.VERSION_CODES.O)
        override fun onOpen(db: SupportSQLiteDatabase) {
            super.onOpen(db)
            INSTANCE?.let { database ->
                scope.launch {
                    populateDatabase(database.getDao())
                }
            }
        }

        @RequiresApi(Build.VERSION_CODES.O)
        suspend fun populateDatabase(debtDAO: DemoDAO) {
            val fecha = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSS")
                .withZone(ZoneOffset.UTC)
                .format(Instant.now())
            var debt = DebtEntity(nombre = "Mom", cantidad = 1000000f)
            debtDAO.insertDebt(debt)
            debt = DebtEntity(nombre = "Mom", cantidad = 1000000f)
            debtDAO.insertDebt(debt)
        }
    }
}
