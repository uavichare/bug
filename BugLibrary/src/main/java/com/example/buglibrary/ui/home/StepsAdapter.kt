package com.example.buglibrary.ui.home

import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.buglibrary.R
import com.example.buglibrary.data.NavDetails
import com.example.buglibrary.databinding.ItemStepsBinding

class StepsAdapter(private var navDetails: ArrayList<NavDetails>) :
    RecyclerView.Adapter<StepsAdapter.StepsViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StepsViewHolder {
        val itemBinding = ItemStepsBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return StepsViewHolder(itemBinding)
    }

    override fun getItemCount() = navDetails.size
    override fun onBindViewHolder(holder: StepsViewHolder, position: Int) {
        holder.bindData(navDetails[position])
    }

    inner class StepsViewHolder(private val itemStepsBinding: ItemStepsBinding) :
        RecyclerView.ViewHolder(itemStepsBinding.root) {
        private val context = itemStepsBinding.root.context
        fun bindData(navDetails: NavDetails) {
            var dir = ""
            if (navDetails.direction.contains(context.getString(R.string.left))) {
                dir = context.getString(R.string.left)
                if (navDetails.direction.contains(context.getString(R.string.slight))) {
                    dir = "${context.getString(R.string.slight)} $dir"
                    itemStepsBinding.imageDirection.setImageResource(R.drawable.ic_slight_left)
                } else {
                    itemStepsBinding.imageDirection.setImageResource(R.drawable.ic_turn_up_left)
                }
                if (navDetails.direction.contains("Your location")) {
                    itemStepsBinding.imageDirection.setImageResource(R.drawable.map_pin)
                }

            } else if (navDetails.direction.contains(context.getString(R.string.right))) {
                dir = context.getString(R.string.right)
                if (navDetails.direction.contains(context.getString(R.string.slight))) {
                    dir = "${context.getString(R.string.slight)} $dir"
                    itemStepsBinding.imageDirection.setImageResource(R.drawable.ic_slight_right)
                } else {
                    itemStepsBinding.imageDirection.setImageResource(R.drawable.ic_turn_up_right)
                }
                if (navDetails.direction.contains("Your location")) {
                    itemStepsBinding.imageDirection.setImageResource(R.drawable.map_pin)
                }
            } else if (navDetails.direction.contains("walk")) {
                dir = "walk"
                itemStepsBinding.imageDirection.setImageResource(R.drawable.ic_arrow_up)
            }
            val dirStr = navDetails.direction + " " + navDetails.distanceStr
            if (dir.isNotEmpty()) {
                val index = dirStr.indexOf(dir, 0, true)
                val span = SpannableStringBuilder(dirStr)
                span.setSpan(
                    ForegroundColorSpan(
                        ContextCompat.getColor(
                            itemStepsBinding.root.context,
                            R.color.purple_700
                        )
                    ), index, index + dir.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )
                itemStepsBinding.textDirection.text = span
            } else


                itemStepsBinding.textDirection.text = dirStr

        }


    }
}