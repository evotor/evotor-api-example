package ru.fhs.evotor.remonline.prefs

import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

@Suppress("UNCHECKED_CAST")
class PreferencesDelegate<TValue>(
    private val preferences: SharedPreferences,
    private val defValue: TValue,
    name: String? = null
) : ReadWriteProperty<Any?, TValue> {

    private var propertyName: String? = null

    private var lastValue: TValue? = null

    private val preferenceKey by lazy { name ?: propertyName!! }
    private val gson = Gson()

    override fun getValue(thisRef: Any?, property: KProperty<*>): TValue {
        if (propertyName == null) {
            propertyName = property.name
        }

        with(preferences) {
            return when (defValue) {
                is Boolean -> (getBoolean(preferenceKey, defValue) as? TValue) ?: defValue
                is Int -> (getInt(preferenceKey, defValue) as TValue) ?: defValue
                is Float -> (getFloat(preferenceKey, defValue) as TValue) ?: defValue
                is Long -> (getLong(preferenceKey, defValue) as TValue) ?: defValue
                is String -> (getString(preferenceKey, defValue) as TValue) ?: defValue

                is List<*> -> gson.fromJson(
                    getString(preferenceKey, EMPTY_ARRAY_JSON),
                    object : TypeToken<TValue>() {}.type
                ) as? TValue ?: defValue
                else -> throw NotFoundRealizationException(defValue)
            }.also {
                //Logg.d { "$propertyName: $it" }
                lastValue = it
            }
        }
    }

    override fun setValue(thisRef: Any?, property: KProperty<*>, value: TValue) {
        if (lastValue == value) {
            return
        }
        if (propertyName == null) {
            propertyName = property.name
        }
        with(preferences.edit()) {
            when (value) {
                is Boolean -> putBoolean(preferenceKey, value)
                is Int -> putInt(preferenceKey, value)
                is Float -> putFloat(preferenceKey, value)
                is Long -> putLong(preferenceKey, value)
                is String -> putString(preferenceKey, value)
                is List<*> -> putString(preferenceKey, gson.toJson(value))
                else -> throw NotFoundRealizationException(value)
            }
            apply()
            lastValue = value
        }
    }

    class NotFoundRealizationException(defValue: Any?) :
        Exception("not found realization for $defValue")

    companion object {
        private const val EMPTY_ARRAY_JSON = "[]"
    }
}