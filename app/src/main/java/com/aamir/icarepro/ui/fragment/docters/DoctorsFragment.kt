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
import com.aamir.icarepro.data.dataStore.DataStoreHelper
import com.aamir.icarepro.data.models.login.LoginResponse
import com.aamir.icarepro.databinding.FragmentDoctersBinding
import com.aamir.icarepro.ui.adapter.DocsAdapter
import com.aamir.icarepro.ui.viewModel.HomeViewModel
import com.google.gson.Gson
import com.pawegio.kandroid.hide
import com.pawegio.kandroid.show
import com.pawegio.kandroid.toast
import com.pawegio.kandroid.visible
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_authentication.*
import kotlinx.android.synthetic.main.fragment_docters.*
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject


@ExperimentalCoroutinesApi
@AndroidEntryPoint
class DoctorsFragment : BaseContainerFragment<FragmentDoctersBinding>() {
    private lateinit var catAdapter: DocsAdapter
    private lateinit var binding: FragmentDoctersBinding

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
            binding.progressCircular.visible = it
        })

        viewModel.error.observe(this, Observer {
            toast(it.message!!)
        })
        viewModel.docs.observe(this, Observer {
            if (it != null ) {
                if (it.isNotEmpty()) {
                    tvNotFound.hide()
                    setServiceAdapter(it as ArrayList<LoginResponse>)
                }else{
                    tvNotFound.show()
                }
            }
        })
    }

    private fun setServiceAdapter(list: ArrayList<LoginResponse>) {
        catAdapter = DocsAdapter(this,list)
        binding.rvDocs.adapter = catAdapter
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = getViewDataBinding()
        viewModel.getDoctors(requireArguments()["id"] as Int)
//        (requireActivity() as HomeActivity)
        tvTitle.text=requireArguments()["name"].toString()
        ivBack.setOnClickListener {
            requireActivity().onBackPressed()
        }
    }

    fun clickItem(loginResponse: LoginResponse) {
        findNavController().navigate(R.id.action_doctors_to_doctor, bundleOf("DATA" to Gson().toJson(loginResponse)))
    }

    override val layoutResourceId: Int
        get() = R.layout.fragment_docters



}