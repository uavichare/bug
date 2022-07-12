package com.example.buglibrary.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.buglibrary.ui.about_us.AboutUsViewModel
import com.example.buglibrary.ui.attraction.AttractionViewModel
import com.example.buglibrary.ui.attraction.ShopNDineViewModel
import com.example.buglibrary.ui.favourite.FavouriteViewModel
import com.example.buglibrary.ui.home.HomeViewModel
import com.example.buglibrary.ui.home.MapViewModel
import com.example.buglibrary.ui.newcontactus.NewContactUsViewModel
import com.example.buglibrary.ui.notifications.NotificationViewModel
import com.example.buglibrary.ui.terms.TermsPrivacyViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class ViewModelModule {

    @Binds
    @IntoMap
    @ViewModelKey(HomeViewModel::class)
    abstract fun bindHomeViewModel(homeViewModel: HomeViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(MapViewModel::class)
    abstract fun bindMapViewModel(mapViewModel: MapViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(AttractionViewModel::class)
    abstract fun bindAttractionViewModel(attractionViewModel: AttractionViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(ShopNDineViewModel::class)
    abstract fun bindShopNDineViewModel(shopNDineViewModel: ShopNDineViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(TermsPrivacyViewModel::class)
    abstract fun bindTermsPrivacyViewModel(termsPrivacyViewModel: TermsPrivacyViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(FavouriteViewModel::class)
    abstract fun bindFavouriteViewModel(favouriteViewModel: FavouriteViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(NotificationViewModel::class)
    abstract fun bindNotificationViewModel(notificationViewModel: NotificationViewModel): ViewModel


    @Binds
    abstract fun bindViewModelFactory(factory: ViewModelFactory): ViewModelProvider.Factory

    @Binds
    @IntoMap
    @ViewModelKey(AboutUsViewModel::class)
    abstract fun bindAboutUsViewModel(aboutUsViewModel: AboutUsViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(NewContactUsViewModel::class)
    abstract fun bindContactUsViewModel(contactUsViewModel: NewContactUsViewModel): ViewModel

}