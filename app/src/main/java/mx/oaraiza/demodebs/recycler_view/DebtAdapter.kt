package mx.oaraiza.demodebs.recycler_view

import android.app.AlertDialog
import android.content.Context
import android.os.AsyncTask
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.zerobranch.layout.SwipeLayout
import mx.oaraiza.demodebs.MainActivity
import mx.oaraiza.demodebs.R
import mx.oaraiza.demodebs.database.DebtDB
import mx.oaraiza.demodebs.database.DebtEntity
import java.lang.ref.WeakReference
import java.text.NumberFormat
import java.util.*
import kotlin.collections.ArrayList

class DebtAdapter internal constructor(val ctx: Context, val reference: WeakReference<MainActivity>):RecyclerView.Adapter<DebtAdapter.DebtViewHolder>(){

    private val inflater: LayoutInflater = LayoutInflater.from(ctx)
    private var debts = ArrayList<DebtEntity>()

    inner class DebtViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nombre= itemView.findViewById<TextView>(R.id.tvNombre)
        val cantidad = itemView.findViewById<TextView>(R.id.tvCantidad)
        val fecha = itemView.findViewById<TextView>(R.id.tvFecha)
        val swipe = itemView.findViewById<SwipeLayout>(R.id.swipe)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DebtViewHolder {
        val itemView = inflater.inflate(R.layout.debt_swipelayout, parent, false)
        return DebtViewHolder(itemView)
    }

    override fun getItemCount() = debts.size

    override fun onBindViewHolder(holder: DebtViewHolder, position: Int) {
        val current = debts[position]
        val format = NumberFormat.getCurrencyInstance(Locale.US)
        val currency = format.format(current.cantidad)

        holder.nombre.text = current.nombre
        holder.cantidad.text = currency
        holder.fecha.text = current.fecha.toString()

        val swipeLayout = holder.swipe
        swipeLayout.setOnActionsListener(object :SwipeLayout.SwipeActionsListener {
            override fun onOpen(direction: Int, isContinuous: Boolean) {
                if (direction == SwipeLayout.LEFT) {
                    val builder = AlertDialog.Builder(ctx)
                    val alert = builder.setTitle("Eliminar Deuda").setMessage("Esta segudo que desea eliminar la deuda")
                        .setNegativeButton("No"){ _,_ ->
                            swipeLayout.close()
                        }
                        .setPositiveButton("SI") { _, _ ->
                            DeleteTask(ctx, reference).execute(current)
                            notifyItemRemoved(debts.indexOf(current))
                            debts.remove(current)
                        }.create()
                    alert.show()
                }
            }

            override fun onClose() {
                swipeLayout.close()
            }
        })
    }

    internal fun setDebts(debts: ArrayList<DebtEntity>) {
        this.debts = debts
        notifyDataSetChanged()
    }

    internal fun getDebts():ArrayList<DebtEntity>{
        return this.debts
    }

    private class DeleteTask(val ctx: Context,val reference: WeakReference<MainActivity>):AsyncTask<DebtEntity, Void, Float?>(){
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
            if(result!=null) {
                val activity = reference.get()
                activity?.updateTotal(result)
            }
        }
    }
}