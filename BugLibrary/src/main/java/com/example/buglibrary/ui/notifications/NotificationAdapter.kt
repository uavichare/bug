package com.example.buglibrary.ui.notifications

import android.text.format.DateUtils
import android.util.SparseBooleanArray
import android.view.HapticFeedbackConstants
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.util.isNotEmpty
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.buglibrary.R
import com.example.buglibrary.data.NotificationModel
import com.example.buglibrary.databinding.ItemNotificationBinding
import com.example.buglibrary.utils.CommonUtils
import java.util.*

/**
 * Created by Furqan on 23-03-2021.
 */
class NotificationAdapter(
    val fragment: NotificationFragment,
    private val dataList: ArrayList<NotificationModel>
) :
    RecyclerView.Adapter<NotificationAdapter.ViewHolder>() {
    private val selectedItems = SparseBooleanArray()
    private val mCalendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"))

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val itemBinding =
            ItemNotificationBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(itemBinding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.bind(dataList[position])

    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    fun toggleSelection(pos: Int) {
        if (selectedItems.get(pos, false)) {
            selectedItems.delete(pos)
        } else {
            selectedItems.put(pos, true)
        }
        notifyItemChanged(pos)
    }

    fun removeData(position: Int) {
        val noti = dataList[position]
        dataList.remove(noti)
        notifyDataSetChanged()
    }

    fun getSelectedItems(): SparseBooleanArray {
        return selectedItems
    }

    fun getSelectedItemCount(): Int {
        return selectedItems.size()
    }

    fun clearSelections() {
        selectedItems.clear()
        notifyDataSetChanged()
    }

    inner class ViewHolder(private val itemBinding: ItemNotificationBinding) :
        RecyclerView.ViewHolder(itemBinding.root), View.OnClickListener, View.OnLongClickListener {

        private val context = itemBinding.root.context
        val round = CommonUtils.pxFromDp(context, 4f)
        lateinit var notificationModel: NotificationModel

        init {
            itemBinding.imgBanner.setOnClickListener(this)
            itemBinding.root.setOnClickListener(this)
            itemBinding.root.setOnLongClickListener(this)

        }

        fun bind(data: NotificationModel) {
            this.notificationModel = data
            itemView.isActivated = selectedItems.get(adapterPosition, false)
            itemBinding.textDetail.text = data.message
            itemBinding.textTitle.text = data.title
            itemBinding.textTime.text = getRelativeDay(data.postedAt)
            if (data.mediaUrl.isEmpty()) {
                itemBinding.imgBanner.visibility = View.GONE
            } else
                itemBinding.imgBanner.visibility = View.VISIBLE

/*
            GlideApp.with(context)
                .load(data.mediaUrl)
                .placeholder(R.drawable.img_placeholder)
                .transform(CenterCrop(), RoundedCorners(round.toInt()))
                .into(itemBinding.imgBanner)
*/

        }

        override fun onClick(p0: View) {
            if (selectedItems.isNotEmpty()) {
                fragment.enableActionMode(adapterPosition)
                p0.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS)
                return
            }
            if (p0.id == R.id.imgBanner) {
                val direction =
                    NotificationFragmentDirections.actionFullImageFragment(notificationModel.mediaUrl)
                Navigation.findNavController(p0).navigate(direction)
            }

        }

        override fun onLongClick(v: View?): Boolean {
            fragment.enableActionMode(adapterPosition)
            v?.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS)
            return true
        }

        private fun getRelativeDay(today: Long): String {

            return DateUtils.getRelativeTimeSpanString(
                today,
                mCalendar.timeInMillis,
                DateUtils.DAY_IN_MILLIS, DateUtils.FORMAT_ABBREV_ALL
            ).toString()
        }
    }

}