package com.example.buglibrary.ui.registration

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.buglibrary.R
import com.example.buglibrary.databinding.FragmentProfileBinding
import com.example.buglibrary.helper.AppConstant
import org.json.JSONObject
import java.io.ByteArrayOutputStream


class ProfileFragment : Fragment() {
    private var photo: Bitmap? = null

    //    private lateinit var userDetail: UserDetails
    private var cameraPermission = arrayOf(Manifest.permission.CAMERA)
    private val requestCameraPermission = 110
    private val imageCaptureCode = 1001
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding =
            FragmentProfileBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        /*   BudapestDatabase.getInstance(requireActivity().applicationContext).dao().allUser()
               .observe(viewLifecycleOwner,
                   Observer {


                       if (it != null) {
                           userDetail = it
                           etFirstName.setText(it.name)
                           etLastName.setText(it.lastname)

                           Glide.with(requireView()).load(it.image)
                               .circleCrop()
                               .into(imgUser)
                           imgUser.visibility = View.VISIBLE
                           txtInitial.visibility = View.INVISIBLE

                       }


                   })*/

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.buttonUpdate.setOnClickListener {
            userValidate()
        }




        binding.fabCamera.setOnClickListener {


            if (hasPermissions(cameraPermission)) {
                openCamera()
            } else {
                requestPermissions(cameraPermission, requestCameraPermission)
            }


        }
    }


    private fun openCamera() {
//        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
//        startActivityForResult(cameraIntent, imageCaptureCode)
    }

    private fun hasPermissions(permissions: Array<String>): Boolean {
        for (permission in permissions) {
            if (ActivityCompat.checkSelfPermission(
                    requireContext(),
                    permission
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return false
            }
        }
        return true
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            openCamera()
        }

    }

    private fun changeProfileImage(encodedString: String) {
        val requestBody = HashMap<String, String>()
        requestBody["img"] = encodedString
//        requestBody["user_id"] = userDetail.id
        /*   ApiManager.clientDevUrl(ApiAuthentication::class.java).uploadImage(requestBody)
               .subscribeOn(Schedulers.io())
               .observeOn(AndroidSchedulers.mainThread())
               .subscribe(object : DisposableObserver<LoginResponse>() {
                   override fun onNext(resp: LoginResponse) {

                       if (resp.success) {
                           updateUser()
                           dismissDialog()

                       }
                   }

                   override fun onError(e: Throwable) {
                       dismissDialog()

                   }

                   override fun onComplete() {

                       dismissDialog()

                   }
               })*/
    }


    private fun encodeImage(bm: Bitmap): String {
        val image = ByteArrayOutputStream()
        bm.compress(Bitmap.CompressFormat.JPEG, 10, image)
        val b = image.toByteArray()

        return Base64.encodeToString(b, Base64.DEFAULT)
    }

    private fun userValidate() {
        when {
            binding.etFirstName.text.isNullOrEmpty() -> {
                binding.textInputFirstName.error = getString(R.string.error_first_name)

            }
            binding.etLastName.text.isNullOrEmpty() -> {
                binding.textInputLastName.error = getString(R.string.error_last_name)

            }

        }

//        show(requireContext(), "Loading...")
        if (photo != null) {

            changeProfileImage(encodeImage(photo!!))
        } else {
            updateUser()
        }


    }

    private fun updateUser() {
        val hashMapRequest = HashMap<String, String>()
//        hashMapRequest["email"] = userDetail.email.toString()
//        hashMapRequest["first_name"] = userDetail.name.toString()
//        hashMapRequest["last_name"] = userDetail.lastname.toString()
//        hashMapRequest["uniqueId"] = CommonUtils.getDeviceId(requireContext())
        hashMapRequest["appname"] = AppConstant.APP_NAME

        val request = HashMap<String, HashMap<String, String>>()
        request["user"] = hashMapRequest
        val jsonObjectInner = JSONObject(request as Map<*, *>)
//        val cryptLib = CryptLib()
//        val parameter =
//            cryptLib.encryptPlainTextWithRandomIV(jsonObjectInner.toString(), "Insuide@1438!")
//        val hashMapEncryptedRequest = HashMap<String, String>()
//        hashMapEncryptedRequest["encryptdata"] = parameter
        /*  val apiAuthentication: ApiAuthentication =
              ApiManager.clientDevUrl(ApiAuthentication::class.java)
          apiAuthentication.signUp(hashMapEncryptedRequest)
              .subscribeOn(Schedulers.io())
              .observeOn(AndroidSchedulers.mainThread())
              .subscribe(object : DisposableObserver<LoginResponse>() {
                  override fun onNext(loginResponse: LoginResponse) {
                      dismissDialog()

                      try {
                          if (loginResponse.success) {
                              val decryptedData = cryptLib.decryptCipherTextWithRandomIV(
                                  loginResponse.encryptdata,
                                  "Insuide@1438!"
                              )
                              val type = object : TypeToken<LoginEncryptedResponse>() {}.type
                              try {
                                  val decryptLoginResponse =
                                      Gson().fromJson<LoginEncryptedResponse>(decryptedData, type)
                                  context?.let {
                                      val pref = PreferenceHelper.defaultPrefs(it)
                                      pref[AppConstant.TOKEN_LOGIN_DETAIL] =
                                          Gson().toJson(decryptLoginResponse)
                                      pref[AppConstant.IS_LOGIN] = true
                                  }
  //                                if (decryptLoginResponse.status == 2) {
  //
  //                                    // Toast.makeText(act)
  //                                } else {
  //                                    Toast.makeText(context, "Login Failed", Toast.LENGTH_LONG)
  //                                        .show()
  //                                }


                              } catch (e: Exception) {
                                  e.toString()
                              }
                          } else {

                              val decryptedData = cryptLib.decryptCipherTextWithRandomIV(
                                  loginResponse.encryptdata,
                                  "Insuide@1438!"
                              )
                              val type = object : TypeToken<LoginEncryptedResponse>() {}.type
                              val decryptLoginResponse =
                                  Gson().fromJson<LoginEncryptedResponse>(decryptedData, type)


                              decryptLoginResponse.status
                              Toast.makeText(
                                  requireContext(),
                                  "server not allowed try again",
                                  Toast.LENGTH_LONG
                              ).show()

                          }
                      } catch (e: Exception) {
                          Toast.makeText(requireContext(), "server Error", Toast.LENGTH_LONG).show()

                      }


                  }

                  override fun onError(e: Throwable) {
                      dismissDialog()

                      Toast.makeText(requireContext(), "server Error", Toast.LENGTH_LONG).show()

                  }

                  override fun onComplete() {
                      dismissDialog()

                  }
              })*/
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        try {
            try {
                if (resultCode == Activity.RESULT_OK) {
                    try {
                        if (requestCode == imageCaptureCode) {
                            try {
                                binding.imgUser.setImageURI(data?.data)
                                photo = data?.extras?.get("data") as? Bitmap
                                binding.imgUser.setImageBitmap(photo)
                            } catch (e: Exception) {
                                e.toString()
                            }

                            Glide.with(requireContext()).load(photo)
                                .circleCrop()
                                .into(binding.imgUser)
                        }
                    } catch (e: Exception) {

                        e.message
                    }
                }
            } catch (e: Exception) {
                e.toString()
            }
        } catch (e: Exception) {
            e.toString()
        }
    }
}
