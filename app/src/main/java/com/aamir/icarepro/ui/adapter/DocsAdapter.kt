package com.aamir.icarepro.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.aamir.icarepro.R
import com.aamir.icarepro.data.models.login.LoginResponse
import com.aamir.icarepro.databinding.RvItemDoctorBinding
import com.aamir.icarepro.ui.fragment.docters.DoctorsFragment
import com.aamir.icarepro.utils.loadImage

/**
 * Created by Aamir Bashir on 27-11-2021.
 */
class DocsAdapter(
    private val fragment: Fragment,
    private val items: ArrayList<LoginResponse>
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {


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
        return ViewHolder(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.rv_item_doctor, parent, false
            )
        )
    }

    override fun getItemCount(): Int = items.size


    inner class ViewHolder(val binding: RvItemDoctorBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                if (fragment is DoctorsFragment)
                    fragment.clickItem(items[adapterPosition])
            }
        }

        fun bind(item: LoginResponse) = with(binding) {
            tvName.text = item.user_profile.fullname
            tvDesc.text = item.category.title
            tvAddress.text = "${item.user_profile.address}, ${item.user_profile.country}"
            if (item.user_profile.image_url != null) {
                item.user_profile.image_url = item.user_profile.image_url.replace(
                    "http://127.0.0.1:8000/",
                    "https://icare.codewithbhat.info/public/"
                )
                binding.ivPic.loadImage(item.user_profile.image_url, R.drawable.error)
            }

        }
    }

}
