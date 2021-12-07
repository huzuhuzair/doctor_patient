package com.aamir.icarepro.utils

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Resources
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.location.Address
import android.location.Geocoder
import android.os.Build
import android.util.DisplayMetrics
import android.util.Log
import android.widget.ImageView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.aamir.icarepro.R
import com.google.android.gms.maps.model.LatLng
import jp.wasabeef.glide.transformations.RoundedCornersTransformation
import java.text.DecimalFormat
import java.util.*
import java.util.regex.Pattern

object StaticFunction {

    fun dpToPx(dp: Int): Float {
        return (dp * Resources.getSystem().displayMetrics.density)
    }

    fun changeLocationStroke(color: String, type: String): GradientDrawable {

        val strokeWidth: Int
        if (type == "rate")
            strokeWidth = 30
        else
            strokeWidth = 20

        val gradient = GradientDrawable()
        gradient.shape = GradientDrawable.RECTANGLE
        gradient.cornerRadius = strokeWidth.toFloat()
        gradient.setColor(Color.parseColor(color))

        return gradient
    }

    fun getAddressObject(activity: Activity, latLng: LatLng): Address? {
        var address: Address? = null
        try {
            val geocoder = Geocoder(activity, Locale.getDefault())
            val addresses: List<Address>? =
                geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1)
            if (addresses!!.isNotEmpty()) {
                address = addresses[0]
            }
        } catch (e: Exception) {
            when (e.message) {
                "grpc failed" -> {
                    Log.e("Error", e.message!!)
                }
                else -> throw e
            }
            e.printStackTrace()
        }
        return address
    }

    fun priceFormatter(price: String): String {
        val formatter = DecimalFormat("#,##0.00")
        return formatter.format(java.lang.Double.valueOf(price))
    }

    fun pickImages(): Intent? {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        intent.addCategory(Intent.CATEGORY_DEFAULT)
        return intent
    }

    fun pickVideos(): Intent? {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "video/*"
        intent.addCategory(Intent.CATEGORY_DEFAULT)
        return intent
    }


    fun hasPermission(context: Context?): Boolean {
        return ContextCompat.checkSelfPermission(
            context!!,
            Manifest.permission.READ_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED
    }

    fun hasPermissionLocation(context: Context?): Boolean {
        return ContextCompat.checkSelfPermission(
            context!!,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }
    fun hasPermissionPhone(context: Context?): Boolean {
        return ContextCompat.checkSelfPermission(
            context!!,
            Manifest.permission.CALL_PHONE
        ) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.CALL_PHONE
        ) == PackageManager.PERMISSION_GRANTED
    }

    fun takeMediaPermission(activity: Activity) {
        ActivityCompat.requestPermissions(
            activity,
            arrayOf(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ),
            101
        )
    }

    fun changeStrokeColor(color: String): GradientDrawable {
        val gradient = GradientDrawable()
        gradient.shape = GradientDrawable.RECTANGLE
        gradient.setStroke(1, Color.parseColor(color))
        gradient.cornerRadii = floatArrayOf(8f, 8f, 8f, 8f, 8f, 8f, 8f, 8f)
        return gradient
    }


    fun isValidColorHex(color: String?): Boolean {
        if (color == null) return false
        val colorPattern = Pattern.compile("#([0-9a-f]{3}|[0-9a-f]{6}|[0-9a-f]{8})")
        val m = colorPattern.matcher(color.toLowerCase())
        return m.matches()
    }

    fun pxToDp(px: Float, context: Context?): Float {
        return px / ((context?.resources?.displayMetrics?.densityDpi?.toFloat()
            ?: 0f) / DisplayMetrics.DENSITY_DEFAULT)
    }


    fun isAtLeastVersion(version: Int): Boolean {
        return Build.VERSION.SDK_INT >= version
    }


    fun loadUserImage(url: String?, imageView: ImageView, roundedShape: Boolean) {
        var thumbUrl = ""

        if (url != null) {
            thumbUrl = url.substring(
                0,
                url.lastIndexOf("/") + 1
            ) + "thumb_" + url.substring(url.lastIndexOf("/") + 1)
        }

        val glide = Glide.with(imageView.context)
        val requestOptions = RequestOptions
            .bitmapTransform(
                RoundedCornersTransformation(
                    8, 0,
                    RoundedCornersTransformation.CornerType.ALL
                )
            )
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .placeholder(R.drawable.ic_driver_dp)
            .error(R.drawable.ic_driver_dp)


        glide.load(url)
            .thumbnail(Glide.with(imageView.context).load(thumbUrl))
            .apply(requestOptions).into(imageView)

    }


    fun changeGradientColor(): GradientDrawable {
        val ButtonColors = intArrayOf(Color.parseColor("#1A000000"), Color.parseColor("#1A000000"))

        return GradientDrawable(
            GradientDrawable.Orientation.TOP_BOTTOM, ButtonColors
        )
    }


    fun changeBorderColor(color: String, strokeColor: String, shape: Int): GradientDrawable {
        val gradient = GradientDrawable()
        gradient.shape = shape
        if (strokeColor.isEmpty()) {
            gradient.setColor(Color.parseColor(color))
            gradient.setStroke(3, Color.parseColor(color))
        } else
            gradient.setStroke(2, Color.parseColor(strokeColor))


        if (shape == GradientDrawable.RADIAL_GRADIENT) {
            gradient.cornerRadius = 20f
            // gradient.setCornerRadii(new float[]{3, 3, 3, 3, 0, 0, 0, 0});
        } else {
            gradient.cornerRadius = 10f
        }
        return gradient
    }

    fun getAge(year: Int, month: Int, day: Int): String {
        val dob = Calendar.getInstance()
        val today = Calendar.getInstance()
        dob[year, month] = day
        var age = today[Calendar.YEAR] - dob[Calendar.YEAR]
        if (today[Calendar.DAY_OF_YEAR] < dob[Calendar.DAY_OF_YEAR]) {
            age--
        }
        val ageInt = age
        return ageInt.toString()
    }

    object BookingStatus {
        const val PENDING = 1
        const val UPCOMING = 2
        const val PAST = 3
    }

    object WASH_STATUS {
        const val PENDING = 1
        const val START = 2
        const val ON_THE_WAY = 3
        const val ARIVED = 4
        const val STARTED_CARWASH = 5
        const val COMPLETED = 6
    }

    object CAR_REPAIR_STATUS {
        const val ALL = 0
        const val PENDING = 1
        const val UPCOMING = 2
        const val PAST = 3
        const val AGENTPAST = 4
        const val REJECTED = 5
        const val START = 6
        const val ON_THE_WAY_FOR_PICKUP = 7
        const val ARIVED_FOR_PICKUP = 8
        const val CAR_PICKEDUP = 9
        const val REACHED_TO_SHOP = 10
        const val STARTED_REPAIR = 11
        const val COMPLETED_REPAIR = 12
        const val ON_THE_WAY_FOR_HANDOVER = 13
        const val ARIVED_FOR_HANDOVER = 14
    }

    object FILE_TYPE {
        const val IMAGE = "1"
        const val VIDEO = "2"
    }

    object TIME_TYPE {
        const val DAY = "1"
        const val HOUR = "2"
    }
}