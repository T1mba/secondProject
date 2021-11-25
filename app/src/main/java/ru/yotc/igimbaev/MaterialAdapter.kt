package ru.yotc.igimbaev

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView

class MaterialAdapter (
    private val values: ArrayList<Materiainfo>,
    private val activity: Activity
): RecyclerView.Adapter<MaterialAdapter.ViewHolder>(){
    private var itemClickListener: ((Materiainfo)-> Unit)? = null
    fun setItemClickListener(itemClickListener:(Materiainfo)-> Unit){
        this.itemClickListener = itemClickListener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):ViewHolder {
        var itemView = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.material_item,
            parent,
            false)
        return ViewHolder(itemView)
    }

    override fun getItemCount(): Int = values.size
    override fun onBindViewHolder(holder: ViewHolder, position: Int){
        holder.title.text = values[position].Title
        holder.kolvo.text = values[position].CountInPack.toString()
        holder.unit.text = values[position].Unit
        holder.stock.text = values[position].CountInStock.toString()
        holder.price.text = values[position].Cost.toString()
        itemClickListener?.invoke(values[position])

    }
    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
            var title: TextView = itemView.findViewById(R.id.title)
                var kolvo: TextView = itemView.findViewById(R.id.kolvo)
                    var unit:TextView = itemView.findViewById(R.id.Unit)
        var stock:TextView = itemView.findViewById(R.id.stock)
        var price:TextView = itemView.findViewById(R.id.price)
    }




}





