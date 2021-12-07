package com.aamir.icarepro.ui.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.aamir.icarepro.R
import com.aamir.icarepro.base.presentation.activity.BaseActivity
import com.aamir.icarepro.data.dataStore.DataStoreConstants
import com.aamir.icarepro.data.dataStore.DataStoreHelper
import com.aamir.icarepro.data.models.login.LoginResponse
import com.aamir.icarepro.utils.SocketManager
import com.aamir.icarepro.utils.getCurrentNavigationFragment
import com.aamir.icarepro.utils.toStart
import com.pawegio.kandroid.hide
import com.pawegio.kandroid.show
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.json.JSONObject
import javax.inject.Inject

@AndroidEntryPoint
@ExperimentalCoroutinesApi
class HomeActivity : BaseActivity(), SocketManager.OnMessageReceiver {

    private var userData: LoginResponse? = null
    private var isHome: Boolean = true
    private var navController: NavController? = null
    var serviceType = ""
    private val navBuilder: NavOptions.Builder by lazy {
        NavOptions.Builder()
    }

    override val layoutResId: Int
        get() = R.layout.activity_home

    @Inject
    lateinit var mDataStoreHelper: DataStoreHelper

    private val socketManager = SocketManager.getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        intialize()
        setData()
        listeners()

    }

    private fun listeners() {
//        btnNav.setOnClickListener {
//            if (isHome) {
//                drawerLayout.openDrawer(drawer)
//            } else {
//                navController!!.navigateUp()
//            }
//        }
//        btnChats.setOnClickListener {
//            navController!!.navigate(R.id.action_homeFragment_to_conversation)
//        }
        llLogout.setOnClickListener {
            logoutUser()
        }
//        llUsers.setOnClickListener {
//            navController?.navigate(R.id.usersFragment)
//            drawerLayout.closeDrawer(drawer)
//        }
//        llContact.setOnClickListener {
//            navController?.navigate(R.id.fragmentContact)
//            drawerLayout.closeDrawer(drawer)
//        }
//
//        llProfile.setOnClickListener {
//            navController?.navigate(R.id.profileFragment)
//            drawerLayout.closeDrawer(drawer)
//        }
        drawer.setOnClickListener {
//            navController?.navigate(R.id.profileFragment)
//            drawerLayout.closeDrawer(drawer)
        }
    }

    fun logoutUser() {
        lifecycleScope.launch {
            mDataStoreHelper.clear()
            mDataStoreHelper.logOut()
        }
        val arguments = JSONObject()
        arguments.putOpt("online_status", 0)
        arguments.putOpt("user_id", userData?.id)
        socketManager.sendOnlineStatus(arguments, this)
        socketManager.disconnect()
        toStart()
    }

    private fun intialize() {
        drawerLayout.setScrimColor(Color.TRANSPARENT)

        val actionBarDrawerToggle: ActionBarDrawerToggle =
            object :
                ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close) {
                override fun onDrawerSlide(drawerView: View, slideOffset: Float) {
                    super.onDrawerSlide(drawerView, slideOffset)
                    val slideX = drawerView.width * slideOffset
                    content.translationX = slideX
                }
            }
//        drawerLayout.addDrawerListener(actionBarDrawerToggle)
        lifecycleScope.launch {
            mDataStoreHelper.getGsonValue(
                DataStoreConstants.USER_DATA,
                LoginResponse::class.java
            ).collectLatest {
//                imProfile.loadImage(it?.profile_pic_url!!)
                userData = it
                if (userData != null) {
                    tvName.text = it!!.name
                    tvEmail.text = it.email
                    socketManager.connect(this@HomeActivity, userData!!, this@HomeActivity, 1)
                } else {
                    toStart()
                }
            }
        }
    }

    override fun onPause() {
        val arguments = JSONObject()
        arguments.putOpt("online_status", 0)
        arguments.putOpt("user_id", userData?.id)
        socketManager.sendOnlineStatus(arguments, this)
        super.onPause()
    }

    private fun setData() {
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.homeFragment) as NavHostFragment
        navController = navHostFragment.navController

        if (userData!!.role_id == 2) {
            userMenu.hide()
            doctorMenu.show()
            doctorMenu.setupWithNavController(navController!!)
        } else {
            userMenu.show()
            doctorMenu.hide()
            userMenu.setupWithNavController(navController!!)
        }


        var inflater = navHostFragment.navController.navInflater
        if (userData!!.role_id == 2) {
            navController!!.graph = inflater.inflate(R.navigation.doctor_home_navigation)
        } else {
            navController!!.graph = inflater.inflate(R.navigation.user_home_navigation)
        }
        navController?.addOnDestinationChangedListener { controller, destination, arguments ->
            when (destination.id) {
                R.id.fragmentDoctors -> {
                    header.hide()
//                    btnNav.setImageDrawable(getDrawable(R.drawable.ic_back_dark))
//                    btnNotify.hide()
//                    btnChats.hide()
                    isHome = false
//                    tvTitle.text = "Doctors"
                }
                R.id.fragmentConversation -> {
                    header.show()
                    isHome = userData!!.role_id == 2
//                    btnNotify.hide()
//                    btnChats.hide()

//                    tvTitle.text = "Conversations"
                }
                R.id.fragmentChat -> {
                    header.hide()
                    isHome = false
                }
                R.id.editProfileFragment -> {
                    header.hide()
                    isHome = false
                }
                R.id.fragmentDoctorDetails -> {
                    header.hide()
                    isHome = false
                }
                else -> {
//                    btnNav.setImageDrawable(getDrawable(R.drawable.ic_menu))
                    isHome = true
                    header.show()
//                    btnNotify.show()
//                    btnChats.show()
//                    tvTitle.text = "Home"
                }

            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        val currentFragment = supportFragmentManager.getCurrentNavigationFragment()
        currentFragment?.onActivityResult(requestCode, resultCode, data)

    }

    override fun onMessageReceive(message: String, event: String) {

    }


}