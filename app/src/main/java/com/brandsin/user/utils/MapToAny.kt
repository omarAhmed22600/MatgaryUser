package com.brandsin.user.utils

import com.google.gson.*
import com.google.gson.reflect.TypeToken
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File


fun HashMap<String, Any>.mMapToJsonElement(): JsonElement = this.hashToJe()
private fun HashMap<String, Any>.hashToJe(): JsonElement {
    val type2 =
        object : TypeToken<HashMap<String, Any>>() {}.type // Convert Hashmap OF Type Any To Json
    val mToJsonParams: String = Gson().toJson(this, type2)
    return JsonParser().parse(mToJsonParams)
}

inline fun <reified T : Any> T.mAnyToJsonElement(): JsonElement {
    val type2 = object : TypeToken<T>() {}.type // Convert Hashmap OF Type Any To Json
    val mToJsonParams: String = Gson().toJson(this, type2)
    return JsonParser().parse(mToJsonParams)
}


fun String.mStringToJsonElement(): JsonElement {
    return JsonParser().parse(this)
}


@Throws(JsonSyntaxException::class)
inline fun <reified T : Any> JsonArray.mMapToArrayList(): ArrayList<T> =
    try {

        mapJson(this)
    } catch (e: JsonSyntaxException) {
        e.printStackTrace()
        ArrayList()
    }

@Throws(JsonSyntaxException::class)
inline fun <reified T : Any> JsonArray.mMapToArrayListFix(): ArrayList<T> =
    try {
        val ret = ArrayList<T>()
        this.forEach { js ->
            val obj: T? = js.mMapToObject<T>()
            obj?.let { ob -> ret.add(ob) }
        }
        ret
    } catch (e: JsonSyntaxException) {
        e.printStackTrace()
        ArrayList()
    }


@Throws(JsonSyntaxException::class)
inline fun <reified T : Any> JsonElement.mMapToArrayList(): ArrayList<T> =
    try {

        mapJson(this)
    } catch (e: JsonSyntaxException) {
        e.printStackTrace()
        ArrayList()
    }


inline fun <reified T : Any> JsonElement.mMapToObject(): T? =
    try {
        mapJson(this)
    } catch (e: JsonSyntaxException) {
        e.printStackTrace()
        null
    }

fun File.toMultiPart(key: String): MultipartBody.Part {
    val reqFile = this.asRequestBody("image/*".toMediaTypeOrNull())
    return MultipartBody.Part.createFormData(
        key,
        this.name, // filename, this is optional
        reqFile
    )
}

fun ArrayList<File>.toMultiPartList(start: String): ArrayList<MultipartBody.Part> {
    var picCnt = 0
    val mList = ArrayList<MultipartBody.Part>()
    for (f in this) mList.add(f.toMultiPart("$start[${picCnt++}]"))

    return mList
}

fun <T : Any> T.toRequestBody(): RequestBody =
    this.toString().toRequestBody("text/plain".toMediaTypeOrNull())

//------------------------------
@Throws(JsonSyntaxException::class)
inline fun <reified T : Any> mapJson(je: JsonElement): T {
    val type = object : TypeToken<T>() {}.type//type
    return Gson().fromJson(je, type)
}

//83 line
@Throws(JsonSyntaxException::class)
inline fun <reified T : Any> mapJson(je: JsonArray): T {
    val type = object : TypeToken<T>() {}.type//type
    return Gson().fromJson(je, type)
}