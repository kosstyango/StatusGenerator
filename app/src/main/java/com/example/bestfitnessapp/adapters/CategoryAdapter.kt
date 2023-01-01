package com.example.bestfitnessapp.adapters

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.bestfitnessapp.R
import com.example.bestfitnessapp.databinding.CategoryItemBinding


class CategoryAdapter(var listener: Listener) : ListAdapter<String, CategoryAdapter.Holder>(Comparator()) {

    class Holder (view: View) : RecyclerView.ViewHolder(view){
        private val binding = CategoryItemBinding.bind(view)
            @SuppressLint("ResourceAsColor")
            fun setData(text:String, listener: Listener) = with(binding){
                tvCatTitle.text = text
                //tvCatTitle.setTextColor(R.color.purple_200)//указываем цвет текста в кнопках
                //tvCatTitle.textSize = 24F//указываем размер текста в кнопках
                cardViewCat.backgroundTintList =//указываем цвет кнопки в горизонтальном Recycler View
                    ColorStateList.valueOf(Color.parseColor(ContentManager.colorList[adapterPosition]))
                itemView.setOnClickListener{listener.onClick(adapterPosition)}
            }
    }

    class Comparator : DiffUtil.ItemCallback<String>(){
        override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem == newItem
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.category_item, parent, false)
        return Holder(view)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.setData(getItem(position), listener)
    }
    interface Listener{
        fun onClick (pos: Int)
    }
}