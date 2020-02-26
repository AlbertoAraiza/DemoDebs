package mx.oaraiza.demodebs.database

import android.content.Context
import androidx.room.*
import java.sql.Date

@Database(entities = arrayOf(DebtEntity::class), version = 1, exportSchema = false)
@TypeConverters(Converters::class)
public abstract class DebtDB:RoomDatabase(){
    abstract fun getDao() : DemoDAO

    companion object{
        @Volatile
        private var INSTANCE : DebtDB? = null

        fun getDatabase(context: Context): DebtDB {
            val tempInstance = INSTANCE
            if (tempInstance != null) {
                return tempInstance
            }
            synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    DebtDB::class.java,
                    "debt_database"
                ).build()
                INSTANCE = instance
                return instance
            }
        }
    }
}
class Converters {
    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time?.toLong()
    }
}
