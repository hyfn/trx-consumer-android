package com.trx.consumer.extensions

import com.google.gson.JsonObject
import org.json.JSONArray
import org.json.JSONObject

operator fun JSONArray.iterator(): Iterator<JSONObject> =
    (0 until length()).asSequence().map { get(it) as JSONObject }.iterator()

/**
 * Maps elements of a [JSONArray] into a list of specified type [T] using the results of a given
 * [transform]. Null elements or non-[JSONObject] elements are ignored.
 */
inline fun <T> JSONArray?.map(crossinline transform: (JSONObject) -> T): List<T> {
    if (this == null) return emptyList()
    return (0 until length()).mapNotNull { index ->
        val jsonObject = optJSONObject(index)
        jsonObject?.let { transform(it) }
    }
}

inline fun JSONArray.forEach(action: (JSONObject) -> Unit) {
    for (index in 0 until this.length()) {
        val obj = this.get(index)
        if (obj is JSONObject) action(obj)
    }
}

fun JSONArray.firstOrNull(): JSONObject? {
    return if (this.length() > 0) {
        val firstObj = getJSONObject(0)
        if (firstObj is JSONObject) firstObj else null
    } else null
}

/**
 * Maps elements of a [JSONArray] into a list of specified type [T] if they can be casted. Elements
 * that cannot be casted are ignored.
 */
inline fun <reified T> JSONArray?.map(): List<T> {
    if (this == null) return emptyList()
    return (0 until length()).mapNotNull { index ->
        val item = opt(index)
        item as? T
    }
}
