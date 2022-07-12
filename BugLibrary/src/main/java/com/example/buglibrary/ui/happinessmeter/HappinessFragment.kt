package com.example.buglibrary.ui.happinessmeter

import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import com.example.buglibrary.R
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.example.buglibrary.manager.LocaleManager


private val SECRET: String = "F0A7F2DDF3092D8D"
private const val SERVICE_PROVIDER = "Dubaiculture"
private const val CLIENT_ID = "dculbeatuser"

class HappinessFragment : BottomSheetDialogFragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_happiness, container, false)
        val webviewHappinessMeter = view.findViewById<WebView>(R.id.web_view_happiness_meter)
        val request = VotingRequest()
        val user = User()
        val application =
            Application(
                "Smart Guide Application",
                "https://dubaiculture.gov.ae/en/Public-Library/Pages/Services.aspx",
                "SMARTAPP",
                "ANDROID"
            );
        application.notes = "MobileSDK Vote"
        request.application = application


        val timeStamp = Utils.getUTCDate()
        val header = Header()
        header.timeStamp = timeStamp
        header.serviceProvider = SERVICE_PROVIDER
        header.themeColor = "#7D0037"
        // Set MicroApp details

//         if (type == WITH_MICROAPP) {
//             header.microApp = ""
//             header.microAppDisplay = "General"
//         }

        request.header = header
        request.user = user

        webviewHappinessMeter.webViewClient = object : WebViewClient() {
            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                super.onPageStarted(view, url, favicon)


            }


            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                if (url!!.contains("happiness://done")) {
                    dismiss()
                }
            }


        }

        /**
         * This is QA URL. Replace it with production once it is ready for production.
         */
        VotingManager.setHappinessUrl("https://happinessmeter.dubai.gov.ae/HappinessMeter2/MobilePostDataService")


        VotingManager.loadHappiness(
            webviewHappinessMeter,
            request,
            SECRET,
            SERVICE_PROVIDER,
            CLIENT_ID,
            LocaleManager.getLanguage(requireContext())
        )
        return view
    }


}