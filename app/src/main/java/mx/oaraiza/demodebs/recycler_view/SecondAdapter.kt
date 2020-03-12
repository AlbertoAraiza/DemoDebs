package mx.oaraiza.demodebs.recycler_view

import android.app.AlertDialog
import android.content.Context
import android.util.Log
import android.view.View
import com.ernestoyaquello.dragdropswiperecyclerview.DragDropSwipeAdapter
import com.ernestoyaquello.dragdropswiperecyclerview.listener.OnItemDragListener
import kotlinx.android.synthetic.main.deb_cardview.view.*
import mx.oaraiza.demodebs.database.DebtEntity

class SecondAdapter(dataset: ArrayList<DebtEntity>) : DragDropSwipeAdapter<DebtEntity, SecondAdapter.ViewHolder>(){
    var ctx : Context? = null
    class ViewHolder(itemView: View) : DragDropSwipeAdapter.ViewHolder(itemView){
        val ctx = itemView.context
        val tvNombre = itemView.tvNombre
        val tvcantidad = itemView.tvCantidad
        val tvFecha = itemView.tvFecha
    }

    override fun getViewHolder(itemView: View): ViewHolder = ViewHolder(itemView)

    override fun onBindViewHolder(item: DebtEntity, viewHolder: ViewHolder, position: Int) {
        ctx = viewHolder.ctx
        viewHolder.tvNombre.text = item.nombre
        viewHolder.tvFecha.text = item.fecha.toString()
        viewHolder.tvcantidad.text = item.cantidad.toString()
    }

    override fun getViewToTouchToStartDraggingItem(
        item: DebtEntity,
        viewHolder: ViewHolder,
        position: Int
    ): View? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}