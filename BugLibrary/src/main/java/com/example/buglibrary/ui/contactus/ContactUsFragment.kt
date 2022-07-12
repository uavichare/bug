package com.example.buglibrary.ui.contactus

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.buglibrary.R
import com.example.buglibrary.data.ContactLocal
import com.example.buglibrary.databinding.ContactUsFragmentBinding

class ContactUsFragment : Fragment() {

    private lateinit var viewModel: ContactUsViewModel
    private var _binding: ContactUsFragmentBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = ContactUsFragmentBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this).get(ContactUsViewModel::class.java)

        binding.recyclerViewContact.layoutManager = LinearLayoutManager(context)
        binding.recyclerViewContact.adapter = ContactUsAdapter(prepareList())
        return binding.root
    }

    private fun prepareList(): ArrayList<ContactLocal> {
        val list = ArrayList<ContactLocal>()

        list.add(ContactLocal("80033222", emptyList(), R.drawable.ic_phone.toString()))
        list.add(
            ContactLocal(
                "www.dubaiculture.gov.ae",
                "www.dubaiculture.gov.ae".split(","),
                R.drawable.ic_web_globe.toString()
            )
        )
        list.add(
            ContactLocal(
                "Facebook.com/Dubai culture",
                "Facebook.com/Dubai culture".split(","),
                R.drawable.logo_facebook.toString()
            )
        )
        list.add(
            ContactLocal(
                "Domestic twitter.com/Dubaiculture",
                "twitter.com/Dubaiculture".split(","),
                R.drawable.logo_twitter.toString()
            )
        )
        list.add(
            ContactLocal(
                "Instagram.com/dubaiculture",
                "https://www.instagram.com/dubaiculture/".split(","),
                R.drawable.logo_instagram.toString()
            )
        )
        list.add(
            ContactLocal(
                "Youtube.com/dubaiCulture",
                "https://www.youtube.com/user/DubaiCulture".split(","),
                R.drawable.logo_youtube.toString()
            )
        )
        list.add(ContactLocal("info@dubaiculture.ae", emptyList(), R.drawable.ic_mail.toString()))
        return list

    }


    /* override fun onActivityCreated(savedInstanceState: Bundle?) {
         super.onActivityCreated(savedInstanceState)

 //        binding.recyclerViewContact.showShimmerAdapter()
         viewModel.contactMenuList.observe(viewLifecycleOwner, Observer {
 //            binding.recyclerViewContact.hideShimmerAdapter()
             if (it.isNotEmpty()) {

                 binding.recyclerViewContact.layoutManager = LinearLayoutManager(context)
                 binding.recyclerViewContact.adapter = ContactUsAdapter(it[0].fieldvalue.en)
                 binding.recyclerViewContact.addItemDecoration(
                     DividerItemDecoration(
                         context,
                         RecyclerView.VERTICAL
                     )
                 )
             } else {
                 binding.recyclerViewContact.visibility = View.GONE
                 binding.errorLayout.errorLayout.visibility = View.VISIBLE
 //                binding.errorLayout.imgError.setImageResource(R.drawable.ic_cloud_off)
 //                binding.errorLayout.errorTitle.text = getString(R.string.bud_connect)
             }
         })
         val contactList = viewModel.fetchContact()
         *//* contactList.observe(viewLifecycleOwner, Observer {
             if (it.isNotEmpty()) {
 //                    binding.recyclerViewContact.adapter =
 //                        ContactListviewAdapter(requireActivity(),it)
             } else {
                 binding.recyclerViewContact.visibility = View.GONE

             }
         })*//*


    }*/

}
