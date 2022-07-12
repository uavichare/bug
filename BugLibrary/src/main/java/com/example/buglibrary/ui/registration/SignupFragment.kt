package com.example.buglibrary.ui.registration

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.text.HtmlCompat
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.Navigation
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.ApiException
import com.example.buglibrary.MainActivity
import com.example.buglibrary.R
import com.example.buglibrary.databinding.SignUpFragmentBinding
import com.example.buglibrary.utils.CommonUtils


class SignupFragment : Fragment() {

    private val signupViewModel: SignupViewModel by activityViewModels()

    private var _binding: SignUpFragmentBinding? = null
    private val binding get() = _binding!!
    private lateinit var mainActivity: MainActivity


    //    private lateinit var userSignUpData: UserDetails
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding =
            SignUpFragmentBinding.inflate(inflater, container, false)
        mainActivity = activity as MainActivity
        binding.textSignIn.text = HtmlCompat.fromHtml(
            getString(R.string.Already_have_an_account_Sign_In),
            HtmlCompat.FROM_HTML_MODE_LEGACY
        )
        signupViewModel.configGoogleSignIn(requireContext())
        binding.checkTermCondition.text =
            HtmlCompat.fromHtml(
                getString(R.string.I_read_and_I_accept_Terms_Conditions),
                HtmlCompat.FROM_HTML_MODE_LEGACY
            )
        binding.fbLoginButton.setOnClickListener {
//            if (isNetworkAvailable(requireContext())) {
//            (activity as MainActivity?)!!.facebookLogin()
//            } else {
//                requireActivity().toast(resources.getString(R.string.check_internet_status))
//
//            }

        }
        binding.customEmailLogin.setOnClickListener {
            signIn()
            /*if (isNetworkAvailable(requireContext())) {
                mainActivity.googleSignIn()
            } else {
                requireActivity().toast(resources.getString(R.string.check_internet_status))

            }*/
        }



        return binding.root
    }

    private fun signIn() {
        val signInIntent = signupViewModel.mGoogleSignInClient.signInIntent
        resultLauncher.launch(signInIntent)
    }

    private var resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                // There are no request codes
                val data: Intent? = result.data
                val task = GoogleSignIn.getSignedInAccountFromIntent(data)
                try {
                    // Google Sign In was successful, authenticate with Firebase
                    val account = task.getResult(ApiException::class.java)!!

                    Log.d("TAG", "firebaseAuthWithGoogle:" + account.id)
//                    firebaseAuthWithGoogle(account.idToken!!)
                } catch (e: ApiException) {
                    // Google Sign In failed, update UI appropriately
                    Log.w("TAG", "Google sign in failed", e)
                    // [START_EXCLUDE]
//                    updateUI(null)
                    // [END_EXCLUDE]
                }

            }
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        binding.buttonSubmit.setOnClickListener {
            if (userValidate()) {
                /* if (isNetworkAvailable(requireContext())) {
                     userSignUpData = UserDetails(
                         "1",
                         binding.etFirstName.text.toString(),
                         binding.etLastName.text.toString(),
                         binding.etEmail.text.toString(),
                         "",
                         true,
                         3
                     )
                     signUp(userSignUpData)
                 } else {
                     requireActivity().toast(resources.getString(R.string.check_internet_status))

                 }*/
            }
        }

        binding.textSignIn.setOnClickListener {
            Navigation.findNavController(it).navigate(R.id.signInFragment)

        }
        /* binding.checkSendNewsLetter.setOnCheckedChangeListener { _, _ ->
             binding.buttonSubmit.isEnabled = userValidateError()
         }*/
        binding.checkTermCondition.setOnCheckedChangeListener { _, _ ->
            binding.buttonSubmit.isEnabled = userValidateError()
        }
        binding.etFirstName.doAfterTextChanged {
            binding.buttonSubmit.isEnabled = userValidateError()
        }
        binding.etLastName.doAfterTextChanged {
            binding.buttonSubmit.isEnabled = userValidateError()
        }
        binding.etEmail.doAfterTextChanged {
            binding.buttonSubmit.isEnabled = userValidateError()
        }


    }


    private fun userValidateError(): Boolean {

        if (binding.etFirstName.text.isNullOrEmpty()) {
            return false

        } else if (binding.etLastName.text.isNullOrEmpty()) {
            return false

        } else if (CommonUtils.isValidEmail(binding.etEmail.text.toString())
                .not()
        ) {
            return false

        } else if (!binding.checkTermCondition.isChecked) {

            return false

        }
        binding.textInputFirstName.error = null
        binding.textInputLastName.error = null
        binding.textInputEmail.error = null
        return true
    }

    private fun userValidate(): Boolean {

        if (binding.etFirstName.text.isNullOrEmpty()) {
            binding.textInputFirstName.error = getString(R.string.error_first_name)
            return false

        } else if (binding.etLastName.text.isNullOrEmpty()) {
            binding.textInputLastName.error = getString(R.string.error_last_name)
            return false

        } else if (CommonUtils.isValidEmail(binding.etEmail.text.toString())
                .not()
        ) {
            binding.textInputEmail.error = getString(R.string.error_email)
            return false

        } else if (!binding.checkTermCondition.isChecked) {
            Toast.makeText(activity, "check term and condition", Toast.LENGTH_LONG).show()
            return false

        }

        return true
    }

    /* fun signUp(userData: UserDetails) {
         val hashMapRequest = HashMap<String, Any>()
         hashMapRequest["email"] = userData.email!!//binding.etEmail.text.toString()
         hashMapRequest["first_name"] = userData.name!! //binding.etFirstName.text.toString()
         hashMapRequest["last_name"] = userData.lastname!!//sbinding.etLastName.text.toString()
         hashMapRequest["uniqueId"] = CommonUtils.getDeviceId(requireContext())
         hashMapRequest["appname"] = AppConstant.APP_NAME
         hashMapRequest["isSubscribe"] = binding.checkSendNewsLetter.isChecked
         Log.d("TAG", "signUp: ${binding.checkSendNewsLetter.isChecked}")

         val request = HashMap<String, HashMap<String, Any>>()
         request["user"] = hashMapRequest

         val jsonObjectInner = JSONObject(request as Map<*, *>)
         Log.d("TAG", "signUp: $jsonObjectInner")
         val cryptLib = CryptLib()
         val parameter =
             cryptLib.encryptPlainTextWithRandomIV(jsonObjectInner.toString(), "Insuide@1438!")
         val hashMapEncryptedRequest = HashMap<String, String>()
         hashMapEncryptedRequest["encryptdata"] = parameter
         val apiAuthentication: ApiAuthentication =
             ApiManager.clientDevUrl(ApiAuthentication::class.java)
         apiAuthentication.signUp(hashMapEncryptedRequest)
             .subscribeOn(Schedulers.io())
             .observeOn(AndroidSchedulers.mainThread())
             .subscribe(object : DisposableObserver<LoginResponse>() {
                 override fun onNext(loginResponse: LoginResponse) {
                     try {

                         val decryptedData = cryptLib.decryptCipherTextWithRandomIV(
                             loginResponse.encryptdata,
                             "Insuide@1438!"
                         )
                         val type = object : TypeToken<LoginEncryptedResponse>() {}.type
                         val decryptLoginResponse =
                             Gson().fromJson<LoginEncryptedResponse>(decryptedData, type)
                         Log.d("TAG", "onNext: ${decryptLoginResponse.toString()}")
                         if (loginResponse.success) {
                             context?.let {
                                 decryptLoginResponse.doc?.let { _ ->
                                     try {
                                         val pref = PreferenceHelper.defaultPrefs(it)
                                         pref[AppConstant.TOKEN_LOGIN_DETAIL] =
                                             Gson().toJson(decryptLoginResponse)
                                         pref[AppConstant.IS_LOGIN] = true
                                         val bundle = Bundle()
                                         bundle.putParcelable(
                                             Intent.EXTRA_TEXT,
                                             decryptLoginResponse.doc
                                         )
                                         Navigation.findNavController(requireView())
                                             .navigate(
                                                 R.id.action_signup_to_login_successfully,
                                                 bundle
                                             )

                                         signupViewModel.setLoginDetails(
                                             UserDetails(
                                                 decryptLoginResponse.doc.id,
                                                 decryptLoginResponse.doc.firstName,
                                                 decryptLoginResponse.doc.lastName,
                                                 decryptLoginResponse.doc.email,
                                                 decryptLoginResponse.doc.profilePic,
                                                 true,
                                                 userData.logintype
                                             )
                                         )


                                     } catch (e: Exception) {
                                         e.toString()
                                     }
                                 }
                                 Toast.makeText(
                                     context,
                                     decryptLoginResponse.message,
                                     Toast.LENGTH_LONG
                                 ).show()
                             }

                         } else {
                             Toast.makeText(
                                 requireContext(),
                                 "server not allowed try again",
                                 Toast.LENGTH_LONG
                             ).show()

                             if (userData.logintype == 1) {
                                 (activity as MainActivity?)!!.googleSignOut()
                             } else if (userData.logintype == 2) {
                                 (activity as MainActivity?)!!.facebookSignOut()

                             }
                         }
                     } catch (e: Exception) {
                         Toast.makeText(
                             requireContext(),
                             "server Error ${e.printStackTrace()}",
                             Toast.LENGTH_LONG
                         ).show()

                     }


                 }

                 override fun onError(e: Throwable) {
                     Toast.makeText(requireContext(), "server Error", Toast.LENGTH_LONG).show()

                 }

                 override fun onComplete() {

                 }
             })
     }*/

}






