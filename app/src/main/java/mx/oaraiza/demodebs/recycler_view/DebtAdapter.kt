package mx.oaraiza.demodebs.recycler_view

import android.app.AlertDialog
import android.content.Context
import android.os.AsyncTask
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.zerobranch.layout.SwipeLayout
import kotlinx.android.synthetic.main.deb_cardview.view.*
import kotlinx.android.synthetic.main.debt_swipelayout.view.*
import kotlinx.android.synthetic.main.new_debt_dialog.view.*
import mx.oaraiza.demodebs.MainActivity
import mx.oaraiza.demodebs.R
import mx.oaraiza.demodebs.database.DebtDB
import mx.oaraiza.demodebs.database.DebtEntity
import mx.oaraiza.demodebs.database.DemoDAO
import java.lang.ref.WeakReference
import java.sql.Date
import java.text.NumberFormat
import java.util.*
import kotlin.collections.ArrayList

class DebtAdapter internal constructor(val debtsFromOutsite : List<DebtEntity>, val total : TextView, val listener: (DebtEntity) -> Unit):RecyclerView.Adapter<DebtAdapter.DebtViewHolder>(){
    lateinit var alert : AlertDialog
    val debts = ArrayList<DebtEntity>(debtsFromOutsite)

    inner class DebtViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(debt :DebtEntity, listener: (DebtEntity) -> Unit) = with(itemView) {
            val format = NumberFormat.getCurrencyInstance(Locale.US)
            val currency = format.format(debt.cantidad)
            tvNombre.text = debt.nombre
            tvCantidad.text = currency
            tvFecha.text = debt.fecha.toString()
            setOnClickListener{listener(debt)}
            swipe.setOnActionsListener(object :SwipeLayout.SwipeActionsListener {
                override fun onOpen(direction: Int, isContinuous: Boolean) {
                    if (direction == SwipeLayout.LEFT) {
                        swipe.close()
                        val builder = AlertDialog.Builder(context)
                        val alert = builder.setTitle("Eliminar Deuda").setMessage("Esta segudo que desea eliminar la deuda")
                            .setNegativeButton("No",null)
                            .setPositiveButton("SI") { _, _ ->
                                DeleteTask(context, total).execute(debt)
                                val delPosition = debts.indexOf(debt)
                                notifyItemRemoved(delPosition)
                                debts.removeAt(delPosition)
                            }.create()
                        alert.show()
                    }else if(direction == SwipeLayout.RIGHT){
                        swipe.close()
                        val builder = AlertDialog.Builder(context)
                        builder.setTitle("Actualizar Deuda")
                        val view = LayoutInflater.from(context).inflate(R.layout.new_debt_dialog, null)
                        view.etNombre.setText(debt.nombre)
                        view.etCantidad.setText(debt.cantidad.toString())
                        view.btnAdd.setOnClickListener {
                            debt.nombre =view.etNombre.text.toString()
                            debt.cantidad = view.etCantidad.text.toString().toFloat()
                            debt.fecha = Date(System.currentTimeMillis())
                            val newPosition = debts.indexOf(debt)
                            UpdateTask(context, total).execute(debt)
                            alert.dismiss()
                            notifyItemChanged(newPosition)
                        }
                        alert = builder.setView(view).create()
                        alert.show()
                    }
                }

                override fun onClose() {
                    swipe.close()
                }
            })
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DebtViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.debt_swipelayout, parent, false)
        return DebtViewHolder(itemView)
    }

    override fun getItemCount() = debts.size

    override fun onBindViewHolder(holder: DebtViewHolder, position: Int) { holder.bind(debts[position], listener) }

    private class DeleteTask(val ctx: Context, val etTotal:TextView):AsyncTask<DebtEntity, Void, Float?>(){
        val format = NumberFormat.getCurrencyInstance(Locale.US)
        override fun doInBackground(vararg params: DebtEntity?): Float? {
            var total = 0f
            if(params[0]!=null){
                DebtDB.getDatabase(ctx).getDao().deleteDebt(params[0]!!)
                DebtDB.getDatabase(ctx).getDao().getDebts().forEach {
                    total += it.cantidad
                }
            }
            return total
        }

        override fun onPostExecute(result: Float?) {
            super.onPostExecute(result)
            etTotal.text = format.format(result!!)
        }
    }
    private class UpdateTask(val ctx: Context, val etTotal: TextView):AsyncTask<DebtEntity, Void, Float?>(){
        val format = NumberFormat.getCurrencyInstance(Locale.US)
        override fun doInBackground(vararg params: DebtEntity?): Float? {
            var total = 0f
            if(params[0]!=null){
                DebtDB.getDatabase(ctx).getDao().updateDebt(params[0]!!)
                DebtDB.getDatabase(ctx).getDao().getDebts().forEach {
                    total += it.cantidad
                }
            }
            return total
        }

        override fun onPostExecute(result: Float?) {
            super.onPostExecute(result)
            etTotal.text = format.format(result!!)
        }
    }
}