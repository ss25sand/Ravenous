package com.example.ravenous.utils

import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide

@BindingAdapter("imageUrl") // --> name of app attribute that can be used to call this function from a layout file
// --> first param is type that new name attribute above can be added to
// --> rest of the params must be specified by the caller
fun loadImage(view: ImageView, imageUrl: String) {
    Glide.with(view.context).load(imageUrl).into(view)
}

@BindingAdapter("reviews")
fun itemPrice(view: TextView, value: String) {
    val text = "$value Reviews"
    view.text = text
}