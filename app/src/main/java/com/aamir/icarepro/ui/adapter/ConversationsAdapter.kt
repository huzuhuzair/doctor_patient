package com.aamir.icarepro.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.aamir.icarepro.R
import com.aamir.icarepro.data.models.chat.Conversation
import com.aamir.icarepro.data.models.login.LoginResponse
import com.aamir.icarepro.databinding.LayoutChatUsersListAdapterBinding
import com.aamir.icarepro.ui.fragment.chatModule.ConversationFragment
import com.aamir.icarepro.utils.loadImage
import com.aamir.icarepro.utils.timeAgo
import com.pawegio.kandroid.hide
import com.pawegio.kandroid.show
import kotlinx.android.synthetic.main.activity_home.view.*
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

/**
 * Created by Aamir Bashir on 27-11-2021.
 */
class ConversationsAdapter(
    private val fragment: Fragment,
    private val items: ArrayList<Conversation>,
    var user: LoginResponse
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {


    private var context: Context? = null

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as ViewHolder).bind(items[position])
//            if(position==0){
//                val params: ViewGroup.LayoutParams = holder.binding.myCard.layoutParams
//                params.height=500
//                params.width = ViewGroup.LayoutParams.MATCH_PARENT
//                holder.binding.myCard.layoutParams = params
//            }

    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        context=parent.context
        return ViewHolder(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.layout_chat_users_list_adapter, parent, false
            )
        )
    }

    override fun getItemCount(): Int = items.size


    inner class ViewHolder(val binding: LayoutChatUsersListAdapterBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                if (fragment is ConversationFragment)
                    fragment.clickItem(items[adapterPosition])
            }
        }

        fun bind(item: Conversation) = with(binding) {
            binding.tvUsername.text = item.user.user_profile.fullname
            tvLastMessage.text = item.last_message.message
            if (item.last_message.receiver_id == user.id) {
                if (item.last_message.read_at != null) {
                    imgUnread.hide()
                    mainLayout.setBackgroundColor(context!!.resources.getColor(R.color.white))
                } else {
                    mainLayout.setBackgroundColor(context!!.resources.getColor(R.color.colorSelected))
                    imgUnread.show()
                }
            }else{
                mainLayout.setBackgroundColor(context!!.resources.getColor(R.color.white))
                imgUnread.hide()
            }
            tvTime?.text =
                if (item.last_message.created_at != "") msgDate(item.last_message.created_at ?: "")
                else ""
//            tvAddress.text = "${item.user_profile.address}, ${item.user_profile.country}"
            if (item.user.user_profile.image_url != null) {
                item.user.user_profile.image_url = item.user.user_profile.image_url.replace(
                    "http://127.0.0.1:8000/",
                    "http://192.168.18.125:1020/hospitalmanagement/public/"
                )
                binding.ivProfile.loadImage(item.user.user_profile.image_url, R.drawable.error)
            }

        }

        private fun msgDate(dateStr: String): String {
            val dateFormat: SimpleDateFormat = if (dateStr.contains("Z")) {
                SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'", Locale.ENGLISH)
            } else {
                SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.ENGLISH)
            }
            var myDate =
                formatDate(dateStr, "yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'", "yyyy-MM-dd HH:mm:ss")
//            dateFormat.timeZone = TimeZone.getTimeZone("UTC")
            var format = SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.ENGLISH)
            val date: Date = format.parse(myDate)
            dateFormat.timeZone = TimeZone.getDefault()
            val formattedDate = dateFormat.format(date)

            val newDate = (date.time)

            return newDate.timeAgo() ?: ""
        }

    }

    fun formatDate(dateToFormat: String, inputFormat: String?, outputFormat: String?): String? {
        try {
//            Logger.e("DATE", "Input Date Date is $dateToFormat")
            val convertedDate = SimpleDateFormat(outputFormat)
                .format(
                    SimpleDateFormat(inputFormat)
                        .parse(dateToFormat)
                )
//            Logger.e("DATE", "Output Date is $convertedDate")

            //Update Date
            return convertedDate
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return null
    }

    fun addList(firstPage: Boolean, list: ArrayList<Conversation>) {
        if (firstPage)
            items.clear()
        items.addAll(list)
        items.addAll(list)
        notifyDataSetChanged()
    }

}
