package yfdc.bytedance.download
import yfdc.YFCallBack
import android.util.Log
import okhttp3.Call
import okhttp3.Response
import okhttp3.internal.closeQuietly
import yfdc.Task
import java.io.IOException
object OkHttpUtil {
    @JvmField
    val ERR: String = "ERR"
    @JvmStatic
    fun get302Url(url: String, yfCall:YFCallBack):Task {
        val task = object : Task() {
            override fun onFailure(call: Call, e: IOException) {
                super.onFailure(call, e)
                Log.e(ERR, "network failed")
                val v0 = java.io.StringWriter()
                val v1 = java.io.PrintWriter(v0)
                e.printStackTrace(v1)
                yfCall.onFailed(v0.toString())
            }
            override fun onResponse(call: Call, response: Response) {
                super.onResponse(call, response)
                if (response.isSuccessful){
                    var i = response.request.url.encodedPath
                    i = i.
                    substring(i.indexOf("/share/video"),i.length-1)
                            .substring("/share/video".length+1)
                    yfCall.onSuccess(i)
                    //response.request.url.encodedPath
                } else {
                    yfCall.onFailed("response bad with ${response.code}")
                }
            }
        }
        val req = okhttp3.Request.Builder()
                .url(url)
                .get()
                .header("User-Agent", App.USER_AGENT)
                .addHeader("User-Agent", App.USER_AGENT)
                .build()
        App.getClient().newCall(req).enqueue(task)
        return task
    }
    @JvmStatic
    fun getVideoUrl(url: String,yfCall: YFCallBack):Task{
        val task:Task = object :Task(){
            override fun onFailure(call: Call, e: IOException) {
                super.onFailure(call, e)
                Log.e(ERR, "network failed")
                val v0 = java.io.StringWriter()
                val v1 = java.io.PrintWriter(v0)
                e.printStackTrace(v1)
                yfCall.onFailed(v0.toString())
            }
            override fun onResponse(call: Call, response: Response) {
                super.onResponse(call, response)
                if (response.isSuccessful && response.body !== null){
                    val res:String = response.body!!.string()
                    response.body?.closeQuietly()
                    response.closeQuietly()
                    yfCall.onSuccess(res)
                } else {
                    yfCall.onFailed("response bad code ${response.code} \nor empty body.")
                }
            }
        }
        val req = okhttp3.Request.Builder()
                .url(url)
                .header("User-Agent",App.USER_AGENT)
                .addHeader("User-Agent",App.USER_AGENT)
                .get()
                .build()
        App.getClient().newCall(req).enqueue(task)
        return task;
    }
}