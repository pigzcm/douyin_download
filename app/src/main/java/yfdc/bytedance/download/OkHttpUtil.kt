package yfdc.bytedance.download
import android.os.Build
import yfdc.YFCallBack
import android.util.Log
import com.google.gson.JsonObject
import okhttp3.internal.closeQuietly
import yfdc.MyAsyncTask
import yfdc.gson.CheckedException


object OkHttpUtil {
    @Suppress("UNCHECKED_CAST")
    @JvmStatic private fun gh(f:Any?):String{
        if(f === null){
            return "null"
        }
        if(f.javaClass.isArray){
            if((f as Array<*>).size == 0){
                return "[]<empty>"
            }
            val bd = StringBuilder().append('[')
            (f as Array<Any>).forEach {
                bd.append(it).append(',')
            }
            if (bd[bd.lastIndex] != '[') {
                bd.deleteCharAt(bd.lastIndex)
            }
            bd.append(']');
            return bd.toString()
        }else{
            return f.toString();
        }
    }
    @JvmStatic fun build():String{
        try{
            val build:Class<Build> = Build::class.java
            val version:Class<Build.VERSION> = Build.VERSION::class.java
            val fieleds = build.declaredFields.map {
                it.isAccessible = true
                it
            }
            val rtn = JsonObject()
            fieleds.forEach{
                rtn.addProperty(it.name, gh(it.get(build)))
            }
            val ver = JsonObject()
            version.declaredFields.map { it.isAccessible=true;it }.forEach {
                ver.addProperty(it.name, gh(it.get(version)))
            }
            rtn.add("__ghost",ver);
            return rtn.toString()
        }catch (e:Throwable){
            throw CheckedException.getInstance(e);
        }
    }
    @JvmField
    val ERR: String = "ERR"

    @JvmStatic
    fun get302Url(url: String, yfCall: YFCallBack): MyAsyncTask {
        val req = okhttp3.Request.Builder()
                .url(url)
                .get()
                .header("User-Agent", App.USER_AGENT)
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
