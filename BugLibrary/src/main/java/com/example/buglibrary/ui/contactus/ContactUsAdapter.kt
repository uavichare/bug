package com.example.buglibrary.ui.contactus

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.buglibrary.data.ContactLocal
import com.example.buglibrary.databinding.ItemContactUsBinding
import com.example.buglibrary.utils.IntentUtils

class ContactUsAdapter(private val contactList: List<ContactLocal>) :
    RecyclerView.Adapter<ContactUsAdapter.ContactViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactViewHolder {
        val itemBinding =
            ItemContactUsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ContactViewHolder(itemBinding)
    }

    override fun getItemCount() = contactList.size
    override fun onBindViewHolder(holder: ContactViewHolder, position: Int) {
        holder.bindData(contactList[position])

    }

    inner class ContactViewHolder(private val itemBinding: ItemContactUsBinding) :
        RecyclerView.ViewHolder(itemBinding.root),
        View.OnClickListener {

        private val context = itemBinding.root.context
        private lateinit var contactMenu: ContactLocal

        init {
            itemBinding.root.setOnClickListener(this)
        }

        fun bindData(contactMenu: ContactLocal) {
            this.contactMenu = contactMenu
            itemBinding.textContact.text = contactMenu.label
            itemBinding.imgContactUs.setImageResource(contactMenu.iconurl.toInt())

        }

        override fun onClick(v: View?) {
            when (adapterPosition) {
                0 -> {
                    IntentUtils.dialPhoneNumber(context, contactMenu.label)
                }
                6 -> {
                    IntentUtils.composeEmail(
                        context,
                        contactMenu.label.split(",").toTypedArray(),
                        ""
                    )
                }
                else -> {
                    IntentUtils.openWebPage(context, contactMenu.lblvalus.joinToString())
                }
            }

        }

        private fun composeMmsMessage(message: String, attachment: Uri) {
            val intent = Intent(Intent.ACTION_SENDTO).apply {
                type = "text/plain"
                putExtra("sms_body", message)
                putExtra(Intent.EXTRA_STREAM, attachment)
            }
            if (intent.resolveActivity(context.packageManager) != null) {
                context.startActivity(intent)
            }
        }
    }
}