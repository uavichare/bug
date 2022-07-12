package com.example.buglibrary.utils

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import androidx.browser.customtabs.CustomTabsIntent

class IntentUtils {
    companion object {
        fun openWebPage(context: Context, url: String) {
            var fullUrl = url
            if (!url.startsWith("https://") && !url.startsWith("http://")) {
                fullUrl = "https://$url";
            }
            val webPage: Uri = Uri.parse(fullUrl)
            val intent = Intent(Intent.ACTION_VIEW, webPage)
            if (intent.resolveActivity(context.packageManager) != null) {
                context.startActivity(intent)
            }
        }

        fun urlIntent(context: Context, url: String) {
            var fullUrl = url
            if (!url.startsWith("https://") && !url.startsWith("http://")) {
                fullUrl = "https://$url";
            }
            val builder = CustomTabsIntent.Builder()
            builder.setToolbarColor(Color.parseColor("#552678"))
            val customTabsIntent = builder.build()
            customTabsIntent.launchUrl(context, Uri.parse(fullUrl))
        }

        fun dialPhoneNumber(context: Context, phoneNumber: String) {
            val intent = Intent(Intent.ACTION_DIAL).apply {
                data = Uri.parse("tel:$phoneNumber")
            }
            if (intent.resolveActivity(context.packageManager) != null) {
                context.startActivity(intent)
            }
        }

        fun composeEmail(context: Context, addresses: Array<String>, subject: String) {
            val intent = Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("mailto:") // only email apps should handle this
                putExtra(Intent.EXTRA_EMAIL, addresses)
                putExtra(Intent.EXTRA_SUBJECT, subject)
            }
            if (intent.resolveActivity(context.packageManager) != null) {
                context.startActivity(intent)
            }
        }

        fun shareText(
            context: Context,
            text: String?,
            title: String?
        ) {

            val sendIntent = Intent()
            sendIntent.action = Intent.ACTION_SEND
            sendIntent.putExtra(Intent.EXTRA_TEXT, text)
            sendIntent.type = "text/plain"
            context.startActivity(Intent.createChooser(sendIntent, title))
        }

    }
}