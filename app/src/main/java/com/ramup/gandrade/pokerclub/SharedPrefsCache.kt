package com.ramup.gandrade.pokerclub

import android.content.SharedPreferences
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.Deferred
import kotlinx.coroutines.experimental.async

class SharedPrefsCache(private val sharedPreferences:SharedPreferences):Cache<String,String>
{
    override fun get(key: String): Deferred<String?> {
        return async(CommonPool){
            sharedPreferences.getString(key,null)
        }
    }

    override fun set(key: String, value: String): Deferred<Unit> {
        return async(CommonPool){
            sharedPreferences.edit().let{
                it.putString(key,value)
                it.apply()
            }
        }
    }

}
interface Cache<Key : Any, Value : Any> {
    fun get(key: Key): Deferred<Value?>
    fun set(key: Key, value: Value): Deferred<Unit>
}