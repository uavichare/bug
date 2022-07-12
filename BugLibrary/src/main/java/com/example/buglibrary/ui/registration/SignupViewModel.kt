package com.example.buglibrary.ui.registration

import android.content.Context
import androidx.lifecycle.ViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import javax.inject.Inject


class SignupViewModel @Inject constructor() : ViewModel() {
    lateinit var mGoogleSignInClient: GoogleSignInClient
    fun configGoogleSignIn(context: Context) {
        // Configure Google Sign In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
//            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .requestProfile()
            .build()
        mGoogleSignInClient = GoogleSignIn.getClient(context, gso);
    }

    /* private val isUserLoginDetails = MutableLiveData<UserDetails>()

     fun setLoginDetails(loginStatus :UserDetails) {
         isUserLoginDetails.value = loginStatus
     }


     fun getLoginDetails(): MutableLiveData<UserDetails> {
         return isUserLoginDetails
     }*/


}