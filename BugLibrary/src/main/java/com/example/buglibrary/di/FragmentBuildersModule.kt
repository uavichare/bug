package com.example.buglibrary.di

import com.example.buglibrary.ui.about_us.AboutUsFragment
import com.example.buglibrary.ui.attraction.AttractionFragment
import com.example.buglibrary.ui.attraction.AttractionVideoPlayFragment
import com.example.buglibrary.ui.attraction.ShopNDineFragment
import com.example.buglibrary.ui.favourite.FavouriteFragment
import com.example.buglibrary.ui.guide.GuidePager1Fragment
import com.example.buglibrary.ui.guide.GuidePager3Fragment
import com.example.buglibrary.ui.home.HomeFragment
import com.example.buglibrary.ui.home.PoiDetailFragment
import com.example.buglibrary.ui.home.ar.NavigationFragment
import com.example.buglibrary.ui.newcontactus.FeedbackFragment
import com.example.buglibrary.ui.newcontactus.NewContactUsFragment
import com.example.buglibrary.ui.notifications.NotificationFragment
import com.example.buglibrary.ui.terms.TermsPrivacyFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class FragmentBuildersModule {

    @ContributesAndroidInjector
    abstract fun contributeHomeFragment(): HomeFragment

    @ContributesAndroidInjector
    abstract fun contributeAttractionFragment(): AttractionFragment

    @ContributesAndroidInjector
    abstract fun contributeShopNDineFragment(): ShopNDineFragment

    @ContributesAndroidInjector
    abstract fun contributeNavigationFragment(): NavigationFragment

    @ContributesAndroidInjector
    abstract fun contributeTermsFragment(): TermsPrivacyFragment

    @ContributesAndroidInjector
    abstract fun contributeFavouriteFragment(): FavouriteFragment

    @ContributesAndroidInjector
    abstract fun contributePoiDetailFragment(): PoiDetailFragment

    @ContributesAndroidInjector
    abstract fun contributePoiNotificationFragment(): NotificationFragment

    @ContributesAndroidInjector
    abstract fun contributeAboutUsFragment(): AboutUsFragment

    @ContributesAndroidInjector
    abstract fun contributeNewContactUsFragment(): NewContactUsFragment

    @ContributesAndroidInjector
    abstract fun contributeFeedbackFragment(): FeedbackFragment

    @ContributesAndroidInjector
    abstract fun attractionVideoFragment(): AttractionVideoPlayFragment

    @ContributesAndroidInjector
    abstract fun contributeGuidePager1Fragment(): GuidePager1Fragment

    @ContributesAndroidInjector
    abstract fun contributeGuidePager3Fragment(): GuidePager3Fragment


}