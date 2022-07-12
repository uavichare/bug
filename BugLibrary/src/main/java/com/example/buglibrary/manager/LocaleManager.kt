package com.example.buglibrary.manager

import android.content.Context
import android.content.res.Configuration
import android.content.res.Resources
import android.os.Build

import com.example.buglibrary.helper.AppConstant
import com.example.buglibrary.helper.PreferenceHelper
import com.example.buglibrary.helper.PreferenceHelper.set
import java.util.*

/**
 * Created by Furqan on 01-06-2018.
 */
class LocaleManager {
    companion object {
        const val LANGUAGE_ENGLISH = "en"
        const val LANGUAGE_ARABIC = "ar"
        const val LANGUAGE_CHINESE = "zh"
        fun setLocale(c: Context): Context {
            return updateResources(c, getLanguage(c))
        }

        fun setNewLocale(c: Context, language: String): Context {
            persistLanguage(c, language)
            return updateResources(c, language)
        }

        private fun updateResources(context: Context, language: String): Context {
            var context = context
            val locale = Locale(language)
            Locale.setDefault(locale)
            val res = context.resources
            val config = Configuration(res.configuration)
            config.setLocale(locale)
            context = context.createConfigurationContext(config)

            return context
        }

        fun getLanguage(c: Context): String {
            val prefs = PreferenceHelper.defaultPrefs(c)
            return prefs.getString(AppConstant.LANGUAGE_KEY, LANGUAGE_ENGLISH)!!
        }

        private fun persistLanguage(c: Context, language: String) {
            val prefs = PreferenceHelper.defaultPrefs(c)
            prefs[AppConstant.LANGUAGE_KEY] = language
        }

        fun getLocale(res: Resources): Locale {
            val config = res.configuration
            return if (Build.VERSION.SDK_INT >= 24) config.locales.get(0) else config.locale
        }
    }
}