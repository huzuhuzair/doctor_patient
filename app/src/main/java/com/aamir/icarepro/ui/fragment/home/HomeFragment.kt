package com.aamir.icarepro.ui.fragment.home

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
import com.aamir.icarepro.data.models.login.LoginResponse
import com.aamir.icarepro.data.models.login.UserProfile
import com.aamir.icarepro.data.models.service.Category
import com.aamir.icarepro.databinding.FragmentHomeBinding
import com.aamir.icarepro.ui.adapter.HomeCategoriesAdapter
import com.aamir.icarepro.ui.adapter.HomeDocsAdapter
import com.aamir.icarepro.ui.viewModel.HomeViewModel
import com.aamir.icarepro.utils.SocketManager
import com.denzcoskun.imageslider.constants.ScaleTypes
import com.denzcoskun.imageslider.models.SlideModel
import com.google.gson.Gson
import com.pawegio.kandroid.hide
import com.pawegio.kandroid.show
import com.pawegio.kandroid.toast
import com.pawegio.kandroid.visible
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject


@ExperimentalCoroutinesApi
@AndroidEntryPoint
class HomeFragment : BaseContainerFragment<FragmentHomeBinding>(), SocketManager.OnMessageReceiver {
    private lateinit var docsAdapter: HomeDocsAdapter
    private lateinit var catAdapter: HomeCategoriesAdapter
    private lateinit var binding: FragmentHomeBinding
    private val socketManager = SocketManager.getInstance()

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
        viewModel.homeData.observe(this, Observer {
            if (it != null) {
                setBanner(it.banners as ArrayList<Category>)
//                setDocs(it.popular_docs as ArrayList<UserProfile>)
                setServiceAdapter(it.categories as ArrayList<Category>)
            }
        })
    }

    private fun setDocs(list: java.util.ArrayList<UserProfile>) {
        if (list.isNotEmpty()) {
            tvPopular.show()
            docsAdapter = HomeDocsAdapter(this, list)
            binding.rvDocs.adapter = docsAdapter
        } else {
            tvPopular.hide()
        }
    }

    private fun setBanner(arrayList: java.util.ArrayList<Category>) {
        val imageList = ArrayList<SlideModel>()
        for (data in arrayList) {
            data.banner_url = data.banner_url.replace(
                "http://127.0.0.1:8000/",
                "http://192.168.18.125:1020/hospitalmanagement/public/"
            )
            imageList.add(SlideModel(data.banner_url, ScaleTypes.CENTER_CROP))
        }
        image_slider.setImageList(imageList)
    }

    private fun setServiceAdapter(carServicesList: ArrayList<Category>) {
        carServicesList[0].layout = 1
        catAdapter = HomeCategoriesAdapter(this, carServicesList, binding.rvCategory)
        binding.rvCategory.adapter = catAdapter
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = getViewDataBinding()

        lifecycleScope.launch {
            mDataStoreHelper.getGsonValue(
                DataStoreConstants.USER_DATA,
                LoginResponse::class.java
            ).collectLatest {
//                imProfile.loadImage(it?.profile_pic_url!!)
//                tvName.text=it!!.name
//                tvEmail.text=it.email
                if (it != null) {
                    if (it.role_id == 3) {
                        socketManager.connect(requireActivity(), it!!, this@HomeFragment, 1)
                        try {
                            llMain.show()
                        }catch (e:Exception){}
                        viewModel.getHomeData()
                    } else {
                        try {
                            llMain.hide()
                        }catch (e:Exception){}

                    }
                }
            }
        }
//        (requireActivity() as HomeActivity)
    }

    fun clickItem(category: Category) {
        findNavController().navigate(
            R.id.action_homeFragment_to_doctors,
            bundleOf("id" to category.id,"name" to category.title)
        )
    }
    fun clickDoctor(loginResponse: LoginResponse) {
        findNavController().navigate(R.id.action_home_to_doctor, bundleOf("DATA" to Gson().toJson(loginResponse)))
    }

    override val layoutResourceId: Int
        get() = R.layout.fragment_home

    override fun onMessageReceive(message: String, event: String) {

    }


}