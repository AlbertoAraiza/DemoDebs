package mx.oaraiza.demodebs

import android.content.Context
import android.os.AsyncTask
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_main.*
import mx.oaraiza.demodebs.database.DebtDB
import mx.oaraiza.demodebs.database.DebtEntity
import mx.oaraiza.demodebs.database.DemoDAO
import mx.oaraiza.demodebs.databinding.NewDebtDialogBinding
import mx.oaraiza.demodebs.recycler_view.DebtAdapter
import java.lang.ref.WeakReference
import java.text.NumberFormat
import java.util.*
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity() {
    lateinit var db : DemoDAO
    lateinit var debts: List<DebtEntity>
    lateinit var ctx : Context
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        ctx = this
        db = DebtDB.getDatabase(this).getDao()
        object : AsyncTask<Void, Void, Void?>(){
            override fun doInBackground(vararg params: Void?): Void? {
                debts = DebtDB.getDatabase(ctx).getDao().getDebts()
                return null
            }

            override fun onPostExecute(result: Void?) {
                rvDebts.adapter = DebtAdapter(debts, tvTotal){
                    Toast.makeText(ctx,"Hola mundo", Toast.LENGTH_LONG).show()
                }
                (rvDebts.adapter as DebtAdapter).notifyDataSetChanged()
            }
        }.execute()
        rvDebts.layoutManager = LinearLayoutManager(this)

        btnAdd.setOnClickListener{
            val builder = AlertDialog.Builder(it.context)
            lateinit var dialog : AlertDialog
            builder.setTitle("Agregar Deuda")
            val view = NewDebtDialogBinding.inflate(layoutInflater)
           view.btnAdd.setOnClickListener {
                val newName = view.etNombre.text.toString()
                val newCantity = view.etCantidad.text.toString().toFloat()
                val newEntity = DebtEntity(nombre = newName, cantidad = newCantity)
                AddItemTask(it.context, WeakReference(this)).execute(newEntity)
                dialog.dismiss()
                this.onResume()
            }
            builder.setView(view.root)
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
            rvDebts.adapter?.notifyDataSetChanged()
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
        val debts = (rvDebts.adapter as DebtAdapter).debts

        debts.add(0,newDebt)
        debts.forEach{
            total = it.cantidad
        }
        val format = NumberFormat.getCurrencyInstance(Locale.US)
        val currency = format.format(total)
        runOnUiThread{
            (rvDebts.adapter as DebtAdapter).notifyDataSetChanged()
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