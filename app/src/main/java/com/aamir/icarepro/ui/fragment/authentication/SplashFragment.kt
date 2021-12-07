package com.aamir.icarepro.ui.fragment.authentication

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.aamir.icarepro.R
import com.aamir.icarepro.base.presentation.fragment.BaseFragment
import com.aamir.icarepro.data.dataStore.DataStoreHelper
import com.aamir.icarepro.ui.activity.HomeActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject


@ExperimentalCoroutinesApi
@AndroidEntryPoint
class SplashFragment : BaseFragment() {

    @Inject
    lateinit var mDataStoreHelper: DataStoreHelper

    var userLoggedIn = false



    override fun getFragmentLayoutResId(): Int {
        return R.layout.fragment_splash
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)



    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lifecycleScope.launch {
            mDataStoreHelper.getCurrentUserLoggedIn().collectLatest {
                userLoggedIn = it
                lifecycleScope.launch {
                    delay(2000)
                    if(userLoggedIn){
                        startActivity(Intent(requireContext(),HomeActivity::class.java))
                        requireActivity().finish()
                    }
                    else {
                        findNavController().navigate(R.id.action_splashFragment_to_selectLanguageFragment)
                    }
                }
            }

        }

//        var zoomAnimation = AnimationUtils.loadAnimation(requireContext(), R.anim.zoom)
//        ivSplash.animation = zoomAnimation
//
//        zoomAnimation.setAnimationListener(object : Animation.AnimationListener {
//            override fun onAnimationStart(animation: Animation) {}
//            override fun onAnimationEnd(animation: Animation) {
//             //   if(mDataStoreHelper.getCurrentUserLoggedIn().collectLatest {  })
//
//
//            }
//
//            override fun onAnimationRepeat(animation: Animation) {}
//        })



    }
}