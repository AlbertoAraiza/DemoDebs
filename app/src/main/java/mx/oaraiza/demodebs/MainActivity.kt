package mx.oaraiza.demodebs

import android.content.Context
import android.os.AsyncTask
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_main.*
import mx.oaraiza.demodebs.database.DebtDB
import mx.oaraiza.demodebs.database.DebtEntity
import mx.oaraiza.demodebs.database.DemoDAO
import mx.oaraiza.demodebs.recycler_view.DebtAdapter
import java.lang.ref.WeakReference
import java.text.NumberFormat
import java.util.*
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity() {
    lateinit var db : DemoDAO
    lateinit var adapter : DebtAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        db = DebtDB.getDatabase(this).getDao()
        adapter = DebtAdapter(this, WeakReference(this))

        rvDebts.adapter = adapter
        rvDebts.layoutManager = LinearLayoutManager(this)

        ibAdd.setOnClickListener{
            val builder = AlertDialog.Builder(it.context)
            lateinit var dialog : AlertDialog
            builder.setTitle("Agregar Deuda")
            val view = LayoutInflater.from(it.context).inflate(R.layout.new_debt_dialog,null)
            val addButton = view.findViewById<Button>(R.id.btnAdd)
            addButton.setOnClickListener {
                val newName = view.findViewById<EditText>(R.id.etNombre).text.toString()
                val newCantity = view.findViewById<EditText>(R.id.etCantidad).text.toString().toFloat()
                AddItemTask(it.context, WeakReference(this)).execute(DebtEntity(nombre = newName, cantidad = newCantity))
                dialog.dismiss()
                this.onResume()
            }
            builder.setView(view)
            dialog = builder.create()
            dialog.show()

        }
    }

    override fun onResume() {
        super.onResume()
        val updateThread = GetDebtsTask(this, WeakReference(this))
        updateThread.execute()
    }

    fun updateDebts(currentDebts: ArrayList<DebtEntity>){
        var total = 0f
        currentDebts.forEach{
            total += it.cantidad
        }
        val format = NumberFormat.getCurrencyInstance(Locale.US)
        val currency = format.format(total)
        runOnUiThread{
            tvTotal.text = currency
            adapter.setDebts(currentDebts)
            adapter.notifyDataSetChanged()
        }
    }

    fun updateTotal(total:Float){
        val format = NumberFormat.getCurrencyInstance(Locale.US)
        val currency = format.format(total)
        runOnUiThread{
            tvTotal.text = currency
        }
    }

    fun addItemAdapter(newDebt: DebtEntity){
        var total = 0f
        val debts = adapter.getDebts()
        debts.add(newDebt)
        debts.forEach{
            total = it.cantidad
        }
        val format = NumberFormat.getCurrencyInstance(Locale.US)
        val currency = format.format(total)
        runOnUiThread{
            adapter.notifyDataSetChanged()
            tvTotal.text = currency
        }
    }

    private class AddItemTask(val ctx: Context, val reference: WeakReference<MainActivity>) :
        AsyncTask<DebtEntity, Void, Void?>() {
        override fun doInBackground(vararg params: DebtEntity?): Void? {
            if (params[0] != null) {
                val activity = reference.get()
                val db = DebtDB.getDatabase(ctx).getDao()
                val newDebt = params[0]!!
                db.insertDebt(params[0]!!)
                activity?.addItemAdapter(newDebt)
            }
            return null
        }
    }

    private class GetDebtsTask(val ctx : Context, val reference: WeakReference<MainActivity>) : AsyncTask<Void, Void, ArrayList<DebtEntity>>(){
        override fun doInBackground(vararg params: Void?): ArrayList<DebtEntity> {
            val debts :ArrayList<DebtEntity> = ArrayList(DebtDB.getDatabase(ctx).getDao().getDebts())
            return debts
        }

        override fun onPostExecute(result: ArrayList<DebtEntity>) {
            super.onPostExecute(result)
            val activity = reference.get()
            activity?.updateDebts(result)
        }
    }
}