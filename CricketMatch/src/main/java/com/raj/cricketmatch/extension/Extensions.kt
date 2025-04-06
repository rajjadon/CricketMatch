package com.raj.cricketmatch.extension

import android.content.Context
import android.view.View
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.IOException
import java.io.InputStream
import java.nio.charset.Charset

fun ViewModel.getJsonFromAssets(context: Context, fileName: String): String? {
    return try {
        val inputStream: InputStream = context.assets.open(fileName)
        val size: Int = inputStream.available()
        val buffer = ByteArray(size)
        inputStream.read(buffer)
        inputStream.close()
        String(buffer, Charset.forName("UTF-8"))
    } catch (e: IOException) {
        e.printStackTrace()
        "[{\"name\":\"Afghanistan\",\"flag\":\"https://img.cricketworld.com/images/d-046469/afghanistan.jpg\"},{\"name\":\"Australia\",\"flag\":\"https://img.cricketworld.com/images/d-046471/australia.jpg\"},{\"name\":\"Bangladesh\",\"flag\":\"https://img.cricketworld.com/images/d-046472/bangladesh.jpg\"},{\"name\":\"England\",\"flag\":\"https://img.cricketworld.com/images/d-046473/england.jpg\"},{\"name\":\"India\",\"flag\":\"https://img.cricketworld.com/images/d-046474/india.jpg\"},{\"name\":\"New Zealand\",\"flag\":\"https://img.cricketworld.com/images/d-046475/new-zealand.jpg\"},{\"name\":\"Pakistan\",\"flag\":\"https://img.cricketworld.com/images/d-046488/pakistan.jpg\"},{\"name\":\"South Africa\",\"flag\":\"https://img.cricketworld.com/images/d-046477/south-africa.jpg\"},{\"name\":\"Sri Lanka\",\"flag\":\"https://img.cricketworld.com/images/d-046478/sri-lanka.jpg\"},{\"name\":\"West Indies\",\"flag\":\"https://img.cricketworld.com/images/d-046479/west-indies.jpg\"}]"
    }
}

inline fun <reified T> Gson.fromJson(json: String): T{
    return fromJson(json, object : TypeToken<T>() {}.type)
}