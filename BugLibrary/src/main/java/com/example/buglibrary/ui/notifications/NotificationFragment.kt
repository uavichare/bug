package com.example.buglibrary.ui.notifications

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import com.example.buglibrary.SDKActivity
import com.example.buglibrary.R
import com.example.buglibrary.data.NotificationModel
import com.example.buglibrary.data.Result
import com.example.buglibrary.databinding.NotificationFragmentBinding
import com.example.buglibrary.di.Injectable
import com.example.buglibrary.manager.LocaleManager
import com.example.buglibrary.utils.CommonUtils
import com.example.buglibrary.utils.ext.injectViewModel
import javax.inject.Inject

class NotificationFragment : Fragment(), Injectable {

    private lateinit var viewModel: NotificationViewModel

    @Inject
    lateinit var viewModelProvider: ViewModelProvider.Factory
    private var _binding: NotificationFragmentBinding? = null
    private val binding get() = _binding!!

    private var adapter: NotificationAdapter? = null

    private var actionModeCallback: ActionModeCallBack? = null
    private var actionMode: ActionMode? = null
    private var notificationList: List<NotificationModel>? = null
    private val idsList = ArrayList<String>()
    private lateinit var mainActivity: SDKActivity
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = NotificationFragmentBinding.inflate(inflater, container, false)
        viewModel = injectViewModel(viewModelProvider)
        if (context is SDKActivity) {
            mainActivity = context as SDKActivity
        }
        fcmToken()
        actionModeCallback = ActionModeCallBack()
        return binding.root
    }

    private fun setEmptyState() {
        binding.errorLayout.errorLayout.visibility = View.VISIBLE
        binding.errorLayout.imgError.setImageResource(R.drawable.empty_notification)
        binding.errorLayout.errorTitle.text = getString(R.string.empty_state_title)
//        binding.errorLayout.errorDetail.text =
//            getString(R.string.empty_notification_detail)
    }

    private fun fcmToken() {
        binding.notificationList.layoutManager =
            LinearLayoutManager(context)
        FirebaseMessaging.getInstance().token
            .addOnCompleteListener(OnCompleteListener { task ->
                if (!task.isSuccessful) {
                    return@OnCompleteListener
                }
                val fcmRegToken = task.result?.toString()
                val language = LocaleManager.getLanguage(requireContext())
                val deviceId = CommonUtils.getDeviceId(requireContext())
                viewModel.fetchNotification(fcmRegToken!!, language, deviceId)
                    .observe(viewLifecycleOwner, {

                        when (it.status) {
                            Result.Status.SUCCESS -> {
                                binding.shimmer.visibility = View.GONE
                                binding.shimmer.hideShimmer()
                                it.data?.data?.let { notificationList ->
                                    this.notificationList = notificationList
                                    if (notificationList.isEmpty()) {
                                        setEmptyState()
                                    }
                                    adapter = NotificationAdapter(
                                        this,
                                        notificationList as java.util.ArrayList<NotificationModel>
                                    )
                                    binding.notificationList.adapter = adapter

                                } ?: kotlin.run {

                                    setEmptyState()
                                }
                            }
                            Result.Status.LOADING -> {
                                binding.shimmer.visibility = View.VISIBLE
                                binding.shimmer.startShimmer()
                            }
                            Result.Status.ERROR -> {
                                binding.shimmer.visibility = View.GONE
                                binding.shimmer.hideShimmer()
                                setEmptyState()

                            }
                        }
                    })


            })
    }

    fun enableActionMode(position: Int) {

        if (actionMode == null) {
            actionMode = mainActivity.getToolbar().startActionMode(actionModeCallback!!)
        }

        toggleSelection(position)
    }

    private fun toggleSelection(position: Int) {
        adapter?.toggleSelection(position)
        val count: Int = adapter?.getSelectedItemCount()!!

        if (count == 0) {
            idsList.clear()
            actionMode!!.finish()
            actionMode = null
        } else {
            idsList.add(notificationList!![position].id)
//            binding.root.notification_actionbar.visibility = View.GONE
            actionMode!!.title = "$count"// ${getString(R.string.selected)}"
            actionMode!!.invalidate()
        }
    }

    inner class ActionModeCallBack : ActionMode.Callback {

        override fun onActionItemClicked(mode: ActionMode?, item: MenuItem?): Boolean {
            return when (item?.itemId) {
/*
                R.id.ic_delete -> {
                    //delete rows
                    Log.d("TAG", "onActionItemClicked: $idsList")
                    viewModel.deleteNotification(idsList.clone() as java.util.ArrayList<String>)
                        .observe(viewLifecycleOwner, {})
                    adapter?.getSelectedItems()?.forEach { key, _ ->
                        adapter?.removeData(key)
                    }

                    idsList.clear()

                    mode?.finish()
                    true
                }
*/
                else -> {
                    false
                }

            }
        }

        override fun onCreateActionMode(mode: ActionMode?, menu: Menu?): Boolean {
           // mode?.menuInflater?.inflate(R.menu.menu_action_mode, menu)
            return true
        }

        override fun onPrepareActionMode(mode: ActionMode?, menu: Menu?): Boolean {
            return false
        }

        override fun onDestroyActionMode(mode: ActionMode?) {
            idsList.clear()
//            binding.root.notification_actionbar.visibility = View.VISIBLE
            adapter?.clearSelections()
            actionMode = null
        }
    }
}