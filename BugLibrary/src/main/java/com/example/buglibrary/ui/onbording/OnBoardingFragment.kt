package com.example.buglibrary.ui.onbording

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.PopupWindow
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayoutMediator
import com.example.buglibrary.MainActivity
import com.example.buglibrary.helper.AppConstant
import com.example.buglibrary.helper.PreferenceHelper
import com.example.buglibrary.helper.PreferenceHelper.set
import com.example.buglibrary.manager.LocaleManager
import com.google.android.material.snackbar.Snackbar
import android.view.*
import com.example.buglibrary.R
import com.example.buglibrary.databinding.FragmentOnBoardingBinding


/**
 * A simple [Fragment] subclass.
 * Use the [OnBoardingFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class OnBoardingFragment : Fragment() {

    private var _binding: FragmentOnBoardingBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentOnBoardingBinding.inflate(inflater, container, false)
        binding.viewPagerOnBoarding.adapter = OnBoardingPagerAdapter(this)
        TabLayoutMediator(binding.tabLayoutGuide, binding.viewPagerOnBoarding) { _, _ ->
        }.attach()

        (activity as MainActivity).setToolBarVisibility(View.GONE)

        binding.viewPagerOnBoarding.registerOnPageChangeCallback(object :
            ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                binding.buttonSkip.text = if (position == 3) {
                    getString(R.string.finish)
                } else
                    getString(R.string.skip)
            }
        })
        binding.buttonSkip.setOnClickListener {

          //  showAlertFilter(requireContext())

        }
        val text =
            if (LocaleManager.getLanguage(requireContext()) == LocaleManager.LANGUAGE_ARABIC) {
                "English"
            } else {
                "العربية"
            }
        binding.buttonLanguage.text = text
        binding.buttonLanguage.setOnClickListener {
            val language = if (text == "العربية") {
                LocaleManager.LANGUAGE_ARABIC
            } else {
                LocaleManager.LANGUAGE_ENGLISH
            }

            LocaleManager.setNewLocale(requireContext(), language)
            val i = Intent(context, MainActivity::class.java)
            startActivity(i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK))
//            Navigation.findNavController(it)
//                .navigate(R.id.language)
        }
        return binding.root
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


/*
    private fun showAlertFilter(context: Context) {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = inflater.inflate(R.layout.fragment_permission, null)
        val popUpWindow = PopupWindow(
            view,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

        val proceed = view.findViewById<Button>(R.id.proceed)
        val deny = view.findViewById<Button>(R.id.deny)

        proceed.setOnClickListener {
            callStartMethod(popUpWindow)
            (activity as MainActivity).setPermission()
        }

        deny.setOnClickListener {
            Snackbar.make(
                requireView(),
                "You can't use navigation services without location permission",
                Snackbar.LENGTH_SHORT
            ).show()

            callStartMethod(popUpWindow)

        }
        popUpWindow.showAtLocation(view, Gravity.CENTER, 0, 0)

    }
*/

/*
    private fun callStartMethod(popUpWindow: PopupWindow) {
        (activity as MainActivity).setToolBarVisibility(View.VISIBLE)
        val pref = PreferenceHelper.defaultPrefs(requireContext())
        pref[AppConstant.ON_BOARDING] = true
        val direction = OnBoardingFragmentDirections.actionNavHome(null)
        findNavController().navigate(direction)
        popUpWindow.dismiss()
    }
*/

}