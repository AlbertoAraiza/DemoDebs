package mx.oaraiza.demodebs

import android.app.Application
import android.content.Context
import android.os.AsyncTask
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.EditText
import android.widget.ImageButton
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_main.*
import mx.oaraiza.demodebs.database.DebtDB
import mx.oaraiza.demodebs.database.DebtEntity
import mx.oaraiza.demodebs.database.DebtViewModel
import mx.oaraiza.demodebs.recycler_view.DebtAdapter

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val adapter = DebtAdapter(this)
        rvDebts.adapter = adapter
        rvDebts.layoutManager = LinearLayoutManager(this)

        ibAdd.setOnClickListener{
            val builder = AlertDialog.Builder(it.context)
            builder.setTitle("Agregar Deuda")
            val view = LayoutInflater.from(it.context).inflate(R.layout.new_debt_dialog,null)
            val addButton = view.findViewById<ImageButton>(R.id.btnAdd)
            addButton.setOnClickListener {
                val newName = view.findViewById<EditText>(R.id.tvNombre).text.toString()
                val newCantity = view.findViewById<EditText>(R.id.tvCantidad).text.toString().toFloat()
                AddItemTask(it.context).execute(DebtEntity(nombre = newName, cantidad = newCantity))
            }
            builder.setView(R.layout.new_debt_dialog)

        }
    }
}

private class AddItemTask(val ctx: Context):AsyncTask<DebtEntity, Void, Void?>(){
    override fun doInBackground(vararg params: DebtEntity?): Void? {
        if (params[0]!= null)
            val x = DebtViewModel(ctx as Application).viewModelScope
            DebtDB.getDatabase(ctx, x).getDao().insertDebt(params[0]!!)
        return null
    }

}