package com.example.buglibrary.ui.registration

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.text.HtmlCompat
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.buglibrary.R
import com.example.buglibrary.databinding.SignInFragmentBinding
import com.example.buglibrary.utils.CommonUtils

class SignInFragment : Fragment() {
    private var _binding: SignInFragmentBinding? = null
    private val binding get() = _binding!!
    private val sighupViewModel: SignupViewModel by activityViewModels()
//    private lateinit var userSignInData: UserDetails

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding =
            SignInFragmentBinding.inflate(inflater, container, false)
        binding.checkTermCondition.text =
            HtmlCompat.fromHtml(
                getString(R.string.I_read_and_I_accept_Terms_Conditions),
                HtmlCompat.FROM_HTML_MODE_LEGACY
            )

        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        binding.fbLoginButton.setOnClickListener {
            /*if (isNetworkAvailable(requireContext())) {
                (activity as MainActivity?)!!.facebookLogin()
            } else {
                requireActivity().toast(resources.getString(R.string.check_internet_status))
            }*/
        }
        binding.customEmailLogin.setOnClickListener {
            /* if (isNetworkAvailable(requireContext())) {
                 (activity as MainActivity?)!!.googleSignIn()
             } else {
                 requireActivity().toast(resources.getString(R.string.check_internet_status))
             }
 */

        }
        binding.buttonSubmit.setOnClickListener {

            if (userValidate()) {

                /* if (isNetworkAvailable(requireContext())) {
                     userSignInData = UserDetails(
                         "1",
                         binding.etFirstName.text.toString(),
                         "",
                         binding.etEmail.text.toString(),
                         "",
                         true,
                         3
                     )
                     signIn(userSignInData)
                 } else {
                     requireActivity().toast(resources.getString(R.string.check_internet_status))
                 }*/
            }
        }

        /*  binding.checkSendNewsLetter.setOnCheckedChangeListener { _, _ ->
              binding.buttonSubmit.isEnabled = userErrorValidation()
          }*/
        binding.checkTermCondition.setOnCheckedChangeListener { _, _ ->
            binding.buttonSubmit.isEnabled = userErrorValidation()
        }


        val noAccountString = "Don't have an account yet? <font color='#FF8C51'>Sign Up</font>."
        /*  binding.backSignup.text =
              HtmlCompat.fromHtml(
                  getString(R.string.dont_have_account_yet_sign_up),
                  HtmlCompat.FROM_HTML_MODE_LEGACY
              )*/
        binding.backSignup.setOnClickListener {

            requireActivity().onBackPressed()
        }
        binding.etFirstName.doAfterTextChanged {
            binding.buttonSubmit.isEnabled = userErrorValidation()
        }

        binding.etEmail.doAfterTextChanged {
            binding.buttonSubmit.isEnabled = userErrorValidation()
        }


    }

    private fun userValidate(): Boolean {

        if (binding.etFirstName.text.isNullOrEmpty()) {
            binding.textInputFirstName.error = getString(R.string.error_first_name)
            return false

        } else if (CommonUtils.isValidEmail(binding.etEmail.text.toString())
                .not()
        ) {
            binding.textInputEmail.error = getString(R.string.error_email)
            return false

        } /*else if (!binding.checkSendNewsLetter.isChecked) {
            Toast.makeText(activity, "check news letter ", Toast.LENGTH_LONG).show()
            return false

        }*/ else if (!binding.checkTermCondition.isChecked) {
            Toast.makeText(activity, "check term and condition", Toast.LENGTH_LONG).show()
            return false

        }

        return true
    }

    private fun userErrorValidation(): Boolean {
        if (binding.etFirstName.text.isNullOrEmpty()) {
            return false

        } else if (CommonUtils.isValidEmail(binding.etEmail.text.toString())
                .not()
        ) {
            return false

        } /*else if (!binding.checkSendNewsLetter.isChecked) {
            return false

        }*/ else if (!binding.checkTermCondition.isChecked) {
            return false
        }
        binding.textInputFirstName.error = null
        binding.textInputEmail.error = null
        return true
    }

    /* private fun signIn(userDetails: UserDetails) {
         val hashMapRequest = HashMap<String, Any>()
         hashMapRequest["email"] = userDetails.email.toString()
         hashMapRequest["first_name"] = userDetails.name.toString()
         hashMapRequest["uniqueId"] = CommonUtils.getDeviceId(requireContext())
         hashMapRequest["appname"] = AppConstant.APP_NAME
         hashMapRequest["isSubscribe"] = binding.checkSendNewsLetter.isChecked
         Log.d("TAG", "signUp: ${binding.checkSendNewsLetter.isChecked}")
         val request = HashMap<String, HashMap<String, Any>>()
         request["user"] = hashMapRequest
         val jsonObjectInner = JSONObject(request as Map<*, *>)
         val cryptLib = CryptLib()
         val parameter =
             cryptLib.encryptPlainTextWithRandomIV(jsonObjectInner.toString(), "Insuide@1438!")
         val hashMapEncryptedRequest = HashMap<String, String>()
         hashMapEncryptedRequest["encryptdata"] = parameter
         val apiAuthentication: ApiAuthentication =
             ApiManager.clientDevUrl(ApiAuthentication::class.java)
         apiAuthentication.signIn(hashMapEncryptedRequest)
             .subscribeOn(Schedulers.io())
             .observeOn(AndroidSchedulers.mainThread())
             .subscribe(object : DisposableObserver<LoginResponse>() {
                 override fun onNext(loginResponse: LoginResponse) {
                     try {
                         if (loginResponse.success) {
                             val decryptedData = cryptLib.decryptCipherTextWithRandomIV(
                                 loginResponse.encryptdata,
                                 "Insuide@1438!"
                             )
                             val type = object : TypeToken<LoginEncryptedResponse>() {}.type
                             val decryptLoginResponse =
                                 Gson().fromJson<LoginEncryptedResponse>(decryptedData, type)
                             context?.let {
                                 decryptLoginResponse?.doc?.let { _ ->
                                     val pref = PreferenceHelper.defaultPrefs(it)
                                     pref[AppConstant.TOKEN_LOGIN_DETAIL] =
                                         Gson().toJson(decryptLoginResponse)
                                     pref[AppConstant.IS_LOGIN] = true
                                     if (decryptLoginResponse != null) {
                                         sighupViewModel.setLoginDetails(
                                             UserDetails(
                                                 decryptLoginResponse.doc.id,
                                                 decryptLoginResponse.doc.firstName,
                                                 decryptLoginResponse.doc.lastName,
                                                 decryptLoginResponse.doc.email,
                                                 userDetails.image,
                                                 true,
                                                 userDetails.logintype
                                             )
                                         )
                                         val bundle = Bundle()
                                         bundle.putParcelable(
                                             Intent.EXTRA_TEXT,
                                             decryptLoginResponse.doc
                                         )

                                         Navigation.findNavController(requireView())
                                             .navigate(
                                                 R.id.action_signin_to_login_successfully,
                                                 bundle
                                             )
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
                                 "server not allow please try again",
                                 Toast.LENGTH_LONG
                             ).show()

                             if (userDetails.logintype == 1) {
                                 (activity as MainActivity?)!!.googleSignOut()
                             } else if (userDetails.logintype == 2) {
                                 (activity as MainActivity?)!!.facebookSignOut()

                             }
                         }
                     } catch (e: Exception) {
                         Toast.makeText(requireContext(), "server Error", Toast.LENGTH_LONG).show()

                     }

                 }

                 override fun onError(e: Throwable) {
                     Toast.makeText(requireContext(), "server Error", Toast.LENGTH_LONG).show()

                 }

                 override fun onComplete() {

                 }
             })
     }
 */
}