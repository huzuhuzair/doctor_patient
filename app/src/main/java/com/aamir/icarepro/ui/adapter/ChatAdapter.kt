package com.aamir.icarepro.ui.adapter

import android.app.Activity
import android.text.format.DateUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.aamir.icarepro.R
import com.aamir.icarepro.data.models.chat.ChatMessage
import com.aamir.icarepro.data.models.login.LoginResponse
import com.aamir.icarepro.utils.timeAgo
import kotlinx.android.synthetic.main.layout_mychat.view.*
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class ChatAdapter(
    private val chattingList: ArrayList<ChatMessage>,
    private val mActivity: Activity,
    private val user: LoginResponse
) : RecyclerView.Adapter<ChatAdapter.Viewholder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Viewholder {
        return if (viewType == 1) {
            Viewholder(
                LayoutInflater.from(mActivity).inflate(
                    R.layout.layout_mychat,
                    parent,
                    false
                )
            )
        } else Viewholder(
            LayoutInflater.from(mActivity).inflate(
                R.layout.layout_otherchat,
                parent,
                false
            )
        )
    }

    override fun getItemViewType(position: Int): Int {
        super.getItemViewType(position)
        return if (user.id == chattingList[position].sender_id) {
            1
        } else 2
    }

    override fun getItemCount(): Int {
        return chattingList.size
    }

    override fun onBindViewHolder(holder: Viewholder, position: Int) {
        val showDateHeader: Boolean
        if (position == 0) {
            showDateHeader = true
        } else {
            val cal1 = Calendar.getInstance()
            cal1.timeInMillis =
                convertStringToDateMillies(chattingList[position - 1].created_at ?: "")
            val cal2 = Calendar.getInstance()
            cal2.timeInMillis = convertStringToDateMillies(chattingList[position].created_at ?: "")
            showDateHeader =
                !(cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) && cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(
                    Calendar.DAY_OF_YEAR
                ))
        }

//        if (showDateHeader) {
//            holder.tvDateHeaderOthers.text =
//                if (chattingList[position].created_at != "") getDateHeader(
//                    convertStringToDateMillies(chattingList[position].created_at ?: "")
//                )
//                else ""
//
//            holder.tvDateHeaderOthers.setTextColor(
//                ContextCompat.getColor(
//                    mActivity,
//                    R.color.colorAccent
//                )
//            )
//
//            if (holder.tvDateHeaderOthers.text.toString()
//                    .equals(mActivity.getString(R.string.today))
//            ) {
//                holder.tvDateHeaderOthers.setTextColor(
//                    ContextCompat.getColor(
//                        mActivity,
//                        R.color.colorAccent
//                    )
//                )
//            }
//            holder.tvDateHeaderOthers.visibility = View.VISIBLE
//        } else {
//            holder.tvDateHeaderOthers.visibility = View.GONE
//        }

        holder.tvTime.text = if (chattingList[position].created_at != null) msgDate(
            chattingList[position].created_at ?: ""
        ) else holder.itemView.context.getString(R.string.now)


        holder.tvMessage.setCompoundDrawablesRelativeWithIntrinsicBounds(
            0, 0, 0, 0
        )
        if (chattingList[position].type == "text") {
            holder.tvMessage.text = chattingList[position].message
            holder.tvMessage.visibility = View.VISIBLE
        }

//        holder.ivMsg.setOnClickListener {
//
//            when (chattingList[position].type) {
//                "location" -> {
//                    holder.itemView.context.startActivity(
//                        Intent(
//                            Intent.ACTION_VIEW,
//                            Uri.parse("google.navigation:q=${chattingList[position].message}")
//                        )
//                    )
//                }
//                "image" -> {
//                    var imageList = arrayListOf<Image>()
//                    imageList.add(Image(orig_path = chattingList[position].message))
//                    val intent = Intent(mActivity, FullScreenImagesActivity::class.java)
//                    intent.putParcelableArrayListExtra(
//                        "images",
//                        imageList
//                    )
//                    intent.putExtra("currentPos", 0)
//                    mActivity.startActivity(intent)
//                }
//
//            }
//        }


//        holder.tvMessage.setOnClickListener {
//            if (chattingList[position].type == "voice_message") {
//                var i = Intent(Intent.ACTION_VIEW);
//                i.setDataAndType(Uri.parse(chattingList[position].message), "audio/*")
//                holder.itemView.context.startActivity(i)
//            } else if (chattingList[position].type == "document") {
//                var intent = Intent(Intent.ACTION_VIEW, Uri.parse(chattingList[position].message))
//                holder.itemView.context.startActivity(Intent.createChooser(intent, "Browse with"))
//            }
//        }

        holder.itemView.msgTick.setBackgroundResource(R.drawable.ic_unread_tick)
        if (chattingList[position].read_at != null) {
            holder.itemView.msgTick.setBackgroundResource(R.drawable.ic_read_tick)
        }

//        holder.btnPlay.setBackgroundResource(R.drawable.ic_play)
//        holder.btnPlay.setOnClickListener {
//            (mActivity as ChatActivity).play(
//                holder.btnPlay,
//                chattingList[position].message ?: "",
//                holder.seekBar
//            )
//        }
    }


    fun  convertStringToDateMillies(dtStart: String): Long {
        var milies = 0L //"2019-09-07T07:28:17.000Z" //2019-09-09 07:02:53
        if (!dtStart.equals("")) {
            val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
            val format2 = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
            try {
                var date = Date()
                if (dtStart.contains("Z")) date = format.parse(dtStart)
                else date = format2.parse(dtStart)
                println(date)
                milies = date.time
            } catch (e: ParseException) {
                e.printStackTrace()
            }
        }

        return milies
    }


    private fun getDateHeader(millis: Long): String? {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = millis
        val dateString: String?
        dateString = when {
            DateUtils.isToday(calendar.timeInMillis) -> mActivity.getString(R.string.today)
            isYesterday(calendar) -> String.format("%s", mActivity.getString(R.string.yesterday))
            existsInWeek(calendar) -> getFormatFromDate(calendar.time, "EEEE")
            else -> getFormatFromDate(calendar.time, "EEEE , MMM dd")
        }
        return dateString
    }

    fun getFormatFromDate(date: Date, format: String): String {
        val sdf = SimpleDateFormat(format, Locale.ENGLISH)
        return try {
            sdf.format(date)
        } catch (e: Exception) {
            e.printStackTrace()
            ""
        }
    }

    fun isYesterday(calendar: Calendar): Boolean {
        val tempCal = Calendar.getInstance()
        tempCal.add(Calendar.DAY_OF_MONTH, -1)
        return calendar.get(Calendar.DAY_OF_MONTH) == tempCal.get(Calendar.DAY_OF_MONTH)
    }

    fun existsInWeek(calendar: Calendar): Boolean {
        val tempCal = Calendar.getInstance()
        tempCal.add(Calendar.DAY_OF_MONTH, -7)
        tempCal.set(Calendar.HOUR_OF_DAY, calendar.get(Calendar.HOUR_OF_DAY))
        tempCal.set(Calendar.MINUTE, calendar.get(Calendar.MINUTE))
        tempCal.set(Calendar.SECOND, calendar.get(Calendar.SECOND))
        return calendar.time.after(tempCal.time)
    }

    class Viewholder(view: View) : RecyclerView.ViewHolder(view) {
        val tvMessage = view.tvMessage
        val tvTime = view.tvTime
//        val btnPlay = view.btnPlay
//        val seekBar = view.seekBar
//        val linearPlayer = view.linearPlayer
//        val tvDateHeaderOthers = view.tvDateHeaderOthers
//        val ivMsg = view.ivMsg
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
//    private fun msgDate(dateStr: String): String {
//        if (!dateStr.equals("")) {
//            val dateFormat: SimpleDateFormat = if (dateStr.contains("Z")) {
//                SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH)
//            } else {
//                SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.ENGLISH)
//            }
//            dateFormat.timeZone = Calendar.getInstance().timeZone
//            val date: Date = dateFormat.parse(dateStr)
//            val newDate = (date.time + dateFormat.timeZone.rawOffset)
//
//            return newDate.timeAgo() ?: ""
//        }
//        return ""
//    }


    fun addList(firstPage: Boolean?, list: ArrayList<ChatMessage>) {
        if (firstPage == true)
            chattingList.clear()
        chattingList.addAll(0, list)
        notifyDataSetChanged()
    }

}
