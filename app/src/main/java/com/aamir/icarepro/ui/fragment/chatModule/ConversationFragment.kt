package com.aamir.icarepro.ui.fragment.chatModule

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.aamir.icarepro.R
import com.aamir.icarepro.base.presentation.fragment.BaseContainerFragment
import com.aamir.icarepro.data.dataStore.DataStoreConstants
import com.aamir.icarepro.data.dataStore.DataStoreHelper
import com.aamir.icarepro.data.models.chat.Conversation
import com.aamir.icarepro.data.models.login.LoginResponse
import com.aamir.icarepro.databinding.LayoutChatlistFragmentBinding
import com.aamir.icarepro.ui.adapter.ConversationsAdapter
import com.aamir.icarepro.ui.viewModel.HomeViewModel
import com.google.gson.Gson
import com.pawegio.kandroid.toast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.layout_chatlist_fragment.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject


@ExperimentalCoroutinesApi
@AndroidEntryPoint
class ConversationFragment : BaseContainerFragment<LayoutChatlistFragmentBinding>() {
    private var data: java.util.ArrayList<Conversation>? = null
    private val conversations = ArrayList<Conversation>()
    private var userData: LoginResponse? = null
    private lateinit var binding: LayoutChatlistFragmentBinding
    private lateinit var adapter: ConversationsAdapter

    private var page = 1
    private var moreData: Boolean = false
    var isQuery = false

    var deletedItemPos = 0

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
            swipe?.isRefreshing = it
        })

        viewModel.error.observe(this, Observer {
            toast(it.message!!)
        })
        viewModel.conversations.observe(this, Observer {
//            loader.setLoading(false)
            swipe?.isRefreshing = false
            conversations.clear()
            data = it
            conversations.addAll(it)
            adapter.notifyDataSetChanged()
//            setPagination(it)

        })
    }

    private fun setPagination(data: java.util.ArrayList<Conversation>) {
        val firstPage = page == 1
        adapter.addList(firstPage, data ?: arrayListOf())
        moreData = data?.size == 10
        // if (page == 1) rvNotifications.scrollToPosition((data?.size ?: 0) - 1)
        page++
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = getViewDataBinding()
        listeners()
        setAdapter()
        lifecycleScope.launch {
            mDataStoreHelper.getGsonValue(
                DataStoreConstants.USER_DATA,
                LoginResponse::class.java
            ).collectLatest {
//                imProfile.loadImage(it?.profile_pic_url!!)
//                tvName.text=it!!.name
//                tvEmail.text=it.email
                userData = it
            }
        }
        adapter = ConversationsAdapter(this, conversations, userData!!)
        rvNotifications?.adapter = adapter
        hitApi("")

//        (requireActivity() as HomeActivity)
    }

    private fun listeners() {
        swipe?.setOnRefreshListener {
            page = 1
            isQuery = false
            hitApi("")
        }
        edtSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun afterTextChanged(p0: Editable?) {
                if (edtSearch.text.toString().trim().isNotEmpty()) {
                    var test=conversations.filter {
                        it.user.user_profile.fullname.toLowerCase().contains(
                            edtSearch.text.toString().trim().toLowerCase()
                        )
                    }
                    conversations.clear()
                    conversations.addAll(test)
                    adapter.notifyDataSetChanged()
                } else {
                    conversations.clear()
                    conversations.addAll(data!!)
                    adapter.notifyDataSetChanged()
                }
            }
        })
    }


    private fun setAdapter() {

//        onRecyclerViewScrolled()
    }

    private fun onRecyclerViewScrolled() {
        rvNotifications.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (moreData && !recyclerView.canScrollVertically(1)) {
                    isQuery = false
                    hitApi("")
                }
            }
        })
    }

    private fun hitApi(query: String) {
        swipe.isRefreshing = true
        if (userData != null)
            viewModel.getConversations(userData!!.id)
    }

    fun clickItem(conversation: Conversation) {
        findNavController().navigate(
            R.id.action_conversation_to_chat,
            bundleOf("DATA" to Gson().toJson(conversation))
        )
    }

    override val layoutResourceId: Int
        get() = R.layout.layout_chatlist_fragment


}