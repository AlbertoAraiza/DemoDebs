package mx.oaraiza.demodebs.recycler_view

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import mx.oaraiza.demodebs.R
import mx.oaraiza.demodebs.database.DebtEntity
import java.text.NumberFormat
import java.util.*

class DebtAdapter internal constructor(ctx: Context):RecyclerView.Adapter<DebtAdapter.DebtViewHolder>(){

    private val inflater: LayoutInflater = LayoutInflater.from(ctx)
    private var debts = emptyList<DebtEntity>() // Cached copy of words

    inner class DebtViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nombre= itemView.findViewById<TextView>(R.id.tvNombre)
        val cantidad = itemView.findViewById<TextView>(R.id.tvCantidad)
        val fecha = itemView.findViewById<TextView>(R.id.tvFecha)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DebtViewHolder {
        val itemView = inflater.inflate(R.layout.deb_cardview, parent, false)
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
    }

    internal fun setDebts(debts: List<DebtEntity>) {
        this.debts = debts
        notifyDataSetChanged()
    }
}