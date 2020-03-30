package com.example.ravenous.ui.result

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.ravenous.R
import com.example.ravenous.data.Business
import com.example.ravenous.utils.PrefsHelper

class ResultRecyclerAdapter (
    private val context: Context,
    private val businesses: List<Business>,
    private val itemListener: BusinessItemListener
) : RecyclerView.Adapter<ResultRecyclerAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameText: TextView = itemView.findViewById(R.id.nameText)
        val businessImage: ImageView = itemView.findViewById(R.id.businessImage)
        val ratingBar: RatingBar = itemView.findViewById(R.id.ratingBar)
    }

    override fun getItemCount() = businesses.size

    // creates a layout view
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val layoutStyle = PrefsHelper.getItemType(parent.context)
        val layoutId = if (layoutStyle == "grid") {
            R.layout.business_grid_item
        } else {
            R.layout.business_list_item
        }
        val view = inflater.inflate(layoutId, parent, false)
        return ViewHolder(view)
    }

    // bind data to view holder
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val business = businesses[position]
        with(holder) {
            nameText.let {
                it.text = business.name
                it.contentDescription = business.name
            }
            Glide.with(context).load(business.image_url).into(businessImage)
            ratingBar.rating = business.rating
            itemView.setOnClickListener {
                itemListener.onBusinessItemClick(business)
            }
        }
    }

    interface BusinessItemListener {
        fun onBusinessItemClick(business: Business)
    }

}