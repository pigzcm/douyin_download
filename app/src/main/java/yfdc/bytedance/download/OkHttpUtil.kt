package yfdc.bytedance.download

import yfdc.YFCallBack
import android.util.Log
import okhttp3.internal.closeQuietly
import yfdc.MyAsyncTask
object OkHttpUtil {
    @JvmField
    val ERR: String = "ERR"

    @JvmStatic
    fun get302Url(url: String, yfCall: YFCallBack): MyAsyncTask {
        val req = okhttp3.Request.Builder()
                .url(url)
                .get()
                .header("User-Agent", App.USER_AGENT)
                .addHeader("User-Agent", App.USER_AGENT)
                .build()
        return MyAsyncTask.execute(req,
                {
                    val path = "/share/video"
                    if (it.isSuccessful && it.request.url.encodedPath.indexOf(path) != -1) {
                        var i = it.request.url.encodedPath
                        i = i.substring(i.indexOf(path), i.length - 1)
                                .substring(path.length + 1)
                        yfCall.onSuccess(i)
                    } else {
                        yfCall.onFailed("response bad with ${it.code}")
                    }
                }, {
            Log.e(ERR, "network failed")
            val v0 = java.io.StringWriter()
            val v1 = java.io.PrintWriter(v0)
            it.printStackTrace(v1)
            yfCall.onFailed(v0.toString())
        })
    }

    @JvmStatic
    fun getVideoUrl(url: String, yfCall: YFCallBack): MyAsyncTask {
        val req = okhttp3.Request.Builder()
                .url(url)
                .header("User-Agent", App.USER_AGENT)
                .addHeader("User-Agent", App.USER_AGENT)
                .get()
                .build()
        return MyAsyncTask.execute(req, {
            if (it.isSuccessful && it.body !== null) {
                val body: okhttp3.ResponseBody = it.body ?: error("empty body")
                val res: String = body.string()
                body.closeQuietly()
                it.closeQuietly()
                yfCall.onSuccess(res)
            } else {
                yfCall.onFailed("response bad code ${it.code} \nor empty body.")
            }
        }, {
            Log.e(ERR, "network failed")
            val v0 = java.io.StringWriter()
            val v1 = java.io.PrintWriter(v0)
            it.printStackTrace(v1)
            yfCall.onFailed(v0.toString())
        })
    }
}
