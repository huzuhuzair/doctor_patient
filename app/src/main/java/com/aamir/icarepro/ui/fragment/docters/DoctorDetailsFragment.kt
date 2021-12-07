package com.aamir.icarepro.ui.fragment.docters

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.aamir.icarepro.R
import com.aamir.icarepro.base.presentation.fragment.BaseContainerFragment
import com.aamir.icarepro.data.dataStore.DataStoreConstants
import com.aamir.icarepro.data.dataStore.DataStoreHelper
import com.aamir.icarepro.data.models.chat.Conversation
import com.aamir.icarepro.data.models.login.LoginResponse
import com.aamir.icarepro.databinding.FragmentDoctorDetailBinding
import com.aamir.icarepro.ui.adapter.DocsAdapter
import com.aamir.icarepro.ui.viewModel.HomeViewModel
import com.aamir.icarepro.utils.ProgressDialog
import com.aamir.icarepro.utils.loadImage
import com.google.gson.Gson
import com.pawegio.kandroid.hide
import com.pawegio.kandroid.toast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject


@ExperimentalCoroutinesApi
@AndroidEntryPoint
class DoctorDetailsFragment : BaseContainerFragment<FragmentDoctorDetailBinding>() {
    private lateinit var conversation: Conversation
    private lateinit var userData: LoginResponse
    private lateinit var catAdapter: DocsAdapter
    private lateinit var binding: FragmentDoctorDetailBinding
    private lateinit var progressDialog:ProgressDialog
    @Inject
    lateinit var mDataStoreHelper: DataStoreHelper
    val viewModel: HomeViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lifecycleScope.launch {
            mDataStoreHelper.getCurrentUserLoggedIn().collectLatest {
                if (it) {

                }
            }
        }
        bindObserver()
    }

    private fun bindObserver() {
        viewModel.loading.observe(this, Observer {
            progressDialog.setLoading(it)
        })

        viewModel.error.observe(this, Observer {
            toast(it.message!!)
        })
        viewModel.user.observe(this, Observer {
            conversation=it
           setUserData(conversation.user)
        })

    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = getViewDataBinding()
        progressDialog= ProgressDialog(requireActivity())
//        viewModel.getDoctors(requireArguments()["id"] as Int)
//        (requireActivity() as HomeActivity)
        lifecycleScope.launch {
            mDataStoreHelper.getGsonValue(
                DataStoreConstants.USER_DATA,
                LoginResponse::class.java
            ).collectLatest {
                userData = it!!
            }
        }
        intialize()
        listeners()
    }

    private fun listeners() {
        binding.ivBack.setOnClickListener {
            requireActivity().onBackPressed()
        }
        binding.llChat.setOnClickListener {
           if(requireArguments().containsKey("fromChat")){
               requireActivity().onBackPressed()
           }else{
               findNavController().navigate(
                   R.id.action_detail_to_chat,
                   bundleOf("DATA" to Gson().toJson(conversation))
               )
           }
        }
    }

    private fun intialize() {

        if (requireArguments()["DATA"] != null) {
            var doctor = Gson().fromJson<LoginResponse>(
                requireArguments()["DATA"].toString(),
                LoginResponse::class.java
            )
            viewModel.loadUserData(doctor.id,userData.id)
//            setUserData(doctor)
//            binding.ivPic.loadImage(doctor.userDatauser_profile.image_url,R.drawable.placeholder)
        }
    }

    private fun setUserData(doctor: LoginResponse) {
//        doctor.conversation.user=doctor
        binding.tvName.text = doctor.user_profile.fullname
        binding.tvAboutV.text =
            "${doctor.user_profile.address}, ${doctor.user_profile.city} \n${doctor.user_profile.country}"
        if (doctor.role_id == 2) {
            binding.tvDesc.text = if (doctor.category != null) doctor.category.title else ""
            doctor.user_qualifications.groupBy { binding.tvQualV.text =
                binding.tvQualV.text.toString() + " " + if (it != null) it.title
                    ?: "N/A" else "N/A"
            }
        } else {
            binding.tvQualV.hide()
            binding.tvQual.hide()
        }
        if (doctor.user_profile.image_url != null) {
            doctor.user_profile.image_url = doctor.user_profile.image_url.replace(
                "http://127.0.0.1:8000/",
                "http://192.168.18.125:1020/hospitalmanagement/public/"
            )
            binding.ivPic.loadImage(doctor.user_profile.image_url, R.drawable.placeholder)
        }
    }

    override val layoutResourceId: Int
        get() = R.layout.fragment_doctor_detail


}