package mx.oaraiza.demodebs

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.AdapterView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.ernestoyaquello.dragdropswiperecyclerview.listener.OnItemDragListener
import com.ernestoyaquello.dragdropswiperecyclerview.listener.OnItemSwipeListener
import com.ernestoyaquello.dragdropswiperecyclerview.listener.OnListScrollListener
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_second.*
import mx.oaraiza.demodebs.database.DebtDB
import mx.oaraiza.demodebs.database.DebtEntity
import mx.oaraiza.demodebs.recycler_view.DebtAdapter
import mx.oaraiza.demodebs.recycler_view.SecondAdapter

class SecondActivity : AppCompatActivity() {
    var dataset = ArrayList<DebtEntity>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_second)
        list.layoutManager = LinearLayoutManager(this@SecondActivity)
        list.adapter = SecondAdapter(dataset)
        list.swipeListener = onItemSwipeListener
        list.dragListener = onItemDragListener
        list.scrollListener = onListScrollListener

        object : AsyncTask<Void, Void, ArrayList<DebtEntity>?>(){
            override fun doInBackground(vararg params: Void?): ArrayList<DebtEntity>? {
                return DebtDB.getDatabase(this@SecondActivity).getDao().getDebts() as ArrayList
            }

            override fun onPostExecute(result: ArrayList<DebtEntity>?) {
                super.onPostExecute(result)
                (list.adapter as SecondAdapter).dataSet = result!!
                (list.adapter as SecondAdapter).notifyDataSetChanged()
            }
        }.execute()
    }

    private val onItemSwipeListener = object : OnItemSwipeListener<DebtEntity> {
        override fun onItemSwiped(position: Int, direction: OnItemSwipeListener.SwipeDirection, item: DebtEntity): Boolean {
            // Handle action of item swiped
            // Return false to indicate that the swiped item should be removed from the adapter's data set (default behaviour)
            // Return true to stop the swiped item from being automatically removed from the adapter's data set (in this case, it will be your responsibility to manually update the data set as necessary)
            var remove = false
            val builder = AlertDialog.Builder(this@SecondActivity)
            val dialogClickListener = DialogInterface.OnClickListener { _, which ->
                when (which) {
                    DialogInterface.BUTTON_POSITIVE -> {
                        object : AsyncTask<Void, Void, Void?>() {
                            override fun doInBackground(vararg params: Void?): Void? {
                                DebtDB.getDatabase(applicationContext).getDao().deleteDebt(item)
                                return null
                            }
                        }.execute()
                    }
                    DialogInterface.BUTTON_NEGATIVE ->  remove = true
                }
            }
            if (direction == OnItemSwipeListener.SwipeDirection.LEFT_TO_RIGHT) {
                builder.setTitle("Eliminar")
                builder.setMessage("¿Esta seguro?")
                builder.setPositiveButton("SI",dialogClickListener)
                builder.setNegativeButton("No",dialogClickListener)
            }else if (direction == OnItemSwipeListener.SwipeDirection.RIGHT_TO_LEFT){
                builder.setTitle("Actualizar")
                val view = LayoutInflater.from(applicationContext).inflate(R.layout.new_debt_dialog, null)
                builder.setView(view)
                val alertDialog = builder.create()
                alertDialog.setCancelable(false)
                alertDialog.show()
            }
            Log.e("Hola", "MUNdo")
            return remove
        }
    }

    private val onItemDragListener = object : OnItemDragListener<DebtEntity> {
        override fun onItemDragged(previousPosition: Int, newPosition: Int, item: DebtEntity) {
            // Handle action of item being dragged from one position to another
            Log.e("Hola", "MUNdo")
        }

        override fun onItemDropped(initialPosition: Int, finalPosition: Int, item: DebtEntity) {
            // Handle action of item dropped
            Log.e("Hola", "MUNdo")
        }
    }

    private val onListScrollListener = object : OnListScrollListener {
        override fun onListScrollStateChanged(scrollState: OnListScrollListener.ScrollState) {
            // Handle change on list scroll state
            Log.e("Hola", "MUNdo")
        }

        override fun onListScrolled(scrollDirection: OnListScrollListener.ScrollDirection, distance: Int) {
            // Handle scrolling
            Log.e("Hola", "MUNdo")
        }
    }
}
