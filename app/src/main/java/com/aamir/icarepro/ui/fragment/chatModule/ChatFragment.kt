package com.aamir.icarepro.ui.fragment.chatModule

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.aamir.icarepro.R
import com.aamir.icarepro.base.presentation.fragment.BaseContainerFragment
import com.aamir.icarepro.data.dataStore.DataStoreConstants
import com.aamir.icarepro.data.dataStore.DataStoreHelper
import com.aamir.icarepro.data.models.FbChat
import com.aamir.icarepro.data.models.FirebaseConversation
import com.aamir.icarepro.data.models.chat.ChatMessage
import com.aamir.icarepro.data.models.login.LoginResponse
import com.aamir.icarepro.databinding.FragmentChatBinding
import com.aamir.icarepro.ui.adapter.ChatAdapter
import com.aamir.icarepro.ui.viewModel.HomeViewModel
import com.aamir.icarepro.utils.DateTimeUtils.currentDateTime
import com.aamir.icarepro.utils.loadImage
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import com.pawegio.kandroid.toast
import com.pawegio.kandroid.visible
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_chat.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.json.JSONObject
import javax.inject.Inject


@ExperimentalCoroutinesApi
@AndroidEntryPoint
class ChatFragment : BaseContainerFragment<FragmentChatBinding>() {
    private lateinit var myConref: DatabaseReference
    private var chattingList = ArrayList<ChatMessage>()
    private lateinit var chatAdapter: ChatAdapter
    private var conversation: FirebaseConversation? = null
    private var userData: LoginResponse? = null
    private lateinit var binding: FragmentChatBinding
    private lateinit var database: FirebaseDatabase

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
            pbLoaderBottom?.visible = it
        })

        viewModel.error.observe(this, Observer {
            toast(it.message!!)
        })
        viewModel.chats.observe(this, Observer {
            chattingList.clear()
            chattingList.addAll(it)
            chatAdapter.notifyDataSetChanged()
            rvChatData.scrollToPosition(chattingList.size - 1)
        })
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = getViewDataBinding()
        database = Firebase.database
        myConref = database.getReference("Conversations")

        lifecycleScope.launch {
            mDataStoreHelper.getGsonValue(
                DataStoreConstants.USER_DATA,
                LoginResponse::class.java
            ).collectLatest {
                userData = it
            }
        }
        if (requireArguments()["DATA"] != null) {
            conversation = Gson().fromJson<FirebaseConversation>(
                requireArguments()["DATA"].toString(),
                FirebaseConversation::class.java
            )
            val index = if (conversation?.membersIds?.indexOf(userData?.id) == 0) 1 else 0

            if (conversation?.lastMessageBy != userData?.id) {
                myConref.child(conversation?.id!!).updateChildren(
                    hashMapOf(
                        "read" to true
                    ) as Map<String, Any>
                )
            }
            database.getReference("UserStatus").child(conversation?.membersIds!![index].toString())
                .addValueEventListener(object :
                    ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        binding.tvUserStatus.text=snapshot.value.toString()
                    }

                    override fun onCancelled(error: DatabaseError) {
                    }

                })
//            if (conversation!!.user.user_profile.image_url != null) {
//                conversation!!.user.user_profile.image_url =
//                    conversation!!.user.user_profile.image_url.replace(
//                        "http://127.0.0.1:8000/",
//                        "https://icare.codewithbhat.info/public/"
//                    )
            binding.tvUserName.text = conversation?.membersNames?.get(index) ?: ""
            binding.ivProfile.loadImage(
                conversation!!.membersProfiles[index],
                R.drawable.placeholder
            )
//            }
            hitApi()
        }

        listeners()
        setAdapter()
//        (requireActivity() as HomeActivity)
    }

    private fun setAdapter() {
        var fbChats = ArrayList<FbChat>()
        chatAdapter = ChatAdapter(fbChats, requireActivity(), userData!!)
        rvChatData.adapter = chatAdapter
        database.getReference("chatMessages").child(conversation?.id!!)
            .addValueEventListener(object :
                ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    fbChats.clear()
                    snapshot.children.forEach {
                        var hashMap = it.value as HashMap<*, *>
                        var fbChat = FbChat(
                            hashMap["dated"].toString(),
                            hashMap["message"].toString(),
                            (hashMap["sentBy"] as Long).toInt()
                        )
                        fbChats.add(fbChat)
                    }
                    chatAdapter.notifyDataSetChanged()
                    try {
                        rvChatData.scrollToPosition(fbChats.size - 1)
                    } catch (e: Exception) {
                    }


                }

                override fun onCancelled(error: DatabaseError) {
                }

            })
    }


    private fun listeners() {
        binding.etMessage.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun afterTextChanged(p0: Editable?) {
                if (userData?.id != null) {
                    if (binding.etMessage.text.toString().isNotEmpty()) {
                        sendIamTyping(true)
                    } else sendIamTyping(false)
                }
            }
        })
        ivBack.setOnClickListener {
            hideKeyboard()
            requireActivity().onBackPressed()
        }
        ivProfile.setOnClickListener {
//            findNavController().navigate(
//                R.id.action_chat_to_doctor,
//                bundleOf("DATA" to Gson().toJson(conversation!!.user), "fromChat" to "yes")
//            )
        }
        ivSend.setOnClickListener {
            if (etMessage.text.toString().trim().isNotEmpty()) {
                sendMessage(etMessage.text.toString().trim())
                etMessage.setText("")
            }
        }
    }

    override fun onDestroy() {

        super.onDestroy()
    }

    private fun sendMessage(message: String) {
        var dated = currentDateTime
        var myref = database.getReference("chatMessages")
        var hashMap = hashMapOf(
            "message" to message,
            "dated" to dated,
            "sentBy" to userData?.id!!
        )
        myref.child(conversation?.id!!).child(myref.push().key ?: "").setValue(hashMap)

        myConref.child(conversation?.id!!).updateChildren(
            hashMapOf(
                "lastMessageSent" to message,
                "updatedAt" to dated,
                "read" to false,
                "lastMessageBy" to userData?.id!!
            )
        )
    }

    private fun sendIamTyping(b: Boolean) {
        val arguments = JSONObject()
        arguments.putOpt("is_typing", b)
        arguments.putOpt("sender_id", userData!!.id)
//        arguments.putOpt("receiver_id", conversation!!.user.id)
//        socketManager.sendTyping(arguments, this)
    }

    private fun hitApi() {
        if (conversation != null) {
//            tvUserName.text = conversation!!.user.user_profile.fullname
//            tvUserStatus.text = if (conversation!!.user.online_status == 1) "Online" else "Offline"
//            viewModel.getChat(conversation!!.id)
//            socketManager.connect(requireActivity(), userData!!, this@ChatFragment, 1)
            val arguments = JSONObject()
            arguments.putOpt("sender_id", userData!!.id)
//            arguments.putOpt("receiver_id", conversation!!.user.id)
//            socketManager.readAllMessages(arguments, this)
//            socketManager.addChatMessageReadListener(this)
//            socketManager.addOnlineStatusListener(this)
//            socketManager.onTyping(this)
//            socketManager.addChatMessageListener(this)
        }
    }

    override val layoutResourceId: Int
        get() = R.layout.fragment_chat

//    override fun onMessageReceive(message: String, event: String) {
//        runOnUiThread {
////            toast(event)
//            if (event == LISTENER_ONLINE_STATUS) {
//                var chat = Gson().fromJson<ChatData>(message, ChatData::class.java)
//                if (chat.data != null) {
//                    if (chat.data.user_id == conversation!!.user.id && tvUserStatus != null) {
//                        tvUserStatus.text =
//                            if (chat!!.data.online_status == 1) "Online" else "Offline"
//                    }
//                } else {
//                    var chat = Gson().fromJson<Result>(message, Result::class.java)
//                    if (chat.user_id == conversation!!.user.id && tvUserStatus != null) {
//                        tvUserStatus.text = if (chat!!.online_status == 1) "Online" else "Offline"
//                    }
//                }
//            } else if (event == EVENT_TYPING_MESSAGE) {
//                var chat = Gson().fromJson<Result>(message, Result::class.java)
//                if (chat.sender_id == conversation!!.user.id && chat.receiver_id == userData!!.id && tvTyping != null) {
//                    tvTyping.visible = chat!!.is_typing!!
//                }
//            } else if (event == EVENT_ADD_MESSAGE) {
//                var chat =
//                    Gson().fromJson<ChatMessageData>(message, ChatMessageData::class.java).data
//                if (chat == null) {
//                    chat = Gson().fromJson<ChatMessage>(message, ChatMessage::class.java)
//                }
//                if (conversation!!.id == 0) {
//                    if (chat != null) {
//                        conversation!!.id = chat.chat_conversation_id
//                    }
//                }
//                if ((chat.chat_conversation_id == conversation!!.id)) {
//                    if (chat != null) {
//                        if (chat.receiver_id == conversation!!.user!!.id) {
//                            val arguments = JSONObject()
//                            arguments.putOpt("sender_id", chat.sender_id)
//                            arguments.putOpt("receiver_id", chat.receiver_id)
//                            socketManager.readAllMessages(arguments, this)
//                        }
//                        chattingList.add(chat)
//                        chatAdapter.notifyDataSetChanged()
//                        binding.rvChatData.scrollToPosition(chattingList.size - 1)
//                    }
//                }
//
//            } else if (event == SocketManager.LISTENER_READALL) {
//                if (message != null) {
//                    var chat = Gson().fromJson<ChatMessage>(message, ChatMessage::class.java)
//                    if (chat.sender_id == conversation!!.user.id) {
//                        chattingList.groupBy {
//                            if (it.sender_id == userData!!.id) {
//                                it.read_at = "test"
//                            }
//                        }
//                        chatAdapter.notifyDataSetChanged()
//                    }
//                }
//            }
//        }
//    }

    override fun onResume() {
//        socketManager.connect(requireActivity(), userData!!, this@ChatFragment, 1)
        super.onResume()
    }


}