package yfdc.bytedance.download

import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import com.google.gson.JsonPrimitive
import okhttp3.HttpUrl
import yfdc.LogUtil
import yfdc.MyAsyncTask
import yfdc.Util
import yfdc.YFCallBack
import java.util.TimerTask
import java.util.Timer

class MainActivity : AppCompatActivity(), View.OnClickListener {
    private fun screen() {
        if (requestedOrientation != ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        }
    }

    private var logUtil: LogUtil? = null
    private lateinit var openDocumentLauncher: ActivityResultLauncher<String>
    final override fun onCreate(var0: android.os.Bundle?) {
        super.onCreate(var0)
        logUtil = LogUtil.getInstance(this).apply {
            start()
        }
        screen()
        setContentView(R.layout.activity_main)
        result = findViewById(R.id.result)
        setListener(findViewById(R.id.id_btn_download))
        setListener(findViewById(R.id.id_btn_log))
        setListener(findViewById(R.id.id_btn_decode))
        this.openDocumentLauncher = registerForActivityResult(Document.INSTANCE) {
            if (it !== null) {
                logUtil?.Log("uri:$it")
                val request = okhttp3.Request.Builder()
                        .header("User-Agent", App.USER_AGENT)
                        // .addHeader("User-Agent", App.USER_AGENT)
                        .url(downloadUrl ?: "https://m.baidu.com")
                        .get().build()
                DownloadDialog(this@MainActivity, it, request).show()
            }
        }
    }

    private var result: TextView? = null
    override fun onBackPressed() = Unit
    private fun setListener(v: View?) {
        if (v !== null) {
            v.setOnClickListener(this)
        } else {
            logUtil?.Log("null view")
            Log.e("YFDC", "null view.")
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == 4 || keyCode == 111) {
            finish()
            return true
        }
        return super.onKeyDown(keyCode, event)
    }

    override fun onStart() {
        super.onStart()
        screen()
    }

    override fun onRestart() {
        super.onRestart()
        screen()
    }

    override fun onResume() {
        super.onResume()
        screen()
    }

    override fun onDestroy() {
        super.onDestroy()
        logUtil?.stop()
        kotlin.system.exitProcess(0)
    }

    @Volatile
    private var downloadUrl: String? = null
    private var date:java.util.Date = java.util.Date()
    private var dateSdf:java.text.SimpleDateFormat =
            java.text.SimpleDateFormat("yyyy_MM_dd_HH_mm_ss",
                    java.util.Locale.ENGLISH)
    @Volatile
    private var downloadCall: okhttp3.Call? = null;
    public final override fun onClick(v: View?) {
        val _this: MainActivity = this;
        if (v === null) {
            throw java.lang.IllegalStateException("parameter \"v\" require not null.")
        }
        when (v.id) {
            R.id.id_btn_decode -> {
                result?.text = ""
                Log.d("YFDC", "click decode")
                var decode = findViewById<EditText>(R.id.txt_share_string)!!.text.toString()
                var toApp = "pre decode:$decode"
                logUtil?.Log(toApp)
                Log.d("YFDC", toApp)
                result?.append(toApp)
                if (decode.length > 3) {
                    decode = yfdc.Util.decodeShare(decode)
                    val condition = decode.startsWith("http://").or(decode.startsWith("https://"))
                    if (condition) {
                        var c1: MyAsyncTask? = null
                        var c2: MyAsyncTask? = null
                        c1 = OkHttpUtil.get302Url(decode, object : YFCallBack {
                            override fun onSuccess(s: String) {
                                toApp = "found url ${App.API}$s"
                                _this.runOnUiThread {
                                    result?.append("$toApp\n")
                                }
                                logUtil?.Log(toApp)
                                Log.d("YFDC", toApp)
                                c2 = OkHttpUtil.getVideoUrl("${App.API}$s", object : YFCallBack {
                                    override fun onSuccess(s: String) {
                                        var obj: JsonObject = try{
                                            com.google.gson.internal.bind.TypeAdapters.JSON_ELEMENT.fromJson(s)!! as JsonObject
                                        }catch (e:Throwable){
                                            e.printStackTrace(System.out)
                                            JsonObject()
                                        }
                                        if (obj.has("status_code") && !obj["status_code"].isJsonNull) {
                                            val st = obj.getAsJsonPrimitive("status_code").asInt
                                            if (st != 0) {
                                                return
                                            }
                                            obj = obj.getAsJsonArray("item_list")[0].asJsonObject
                                            obj = obj["video"].asJsonObject
                                            val vid: String = obj.getAsJsonPrimitive("vid").asString
                                            var ratio: String? = (obj["ratio"] as JsonPrimitive?)?.asString
                                            if (ratio !== null) {
                                                val rp = ratio.substring(0, ratio.length-1).toInt()
                                                if (rp < 720){
                                                    ratio = "720p"
                                                }
                                            }
                                            val url: HttpUrl = VideoUrl().setParam(vid, ratio).create()
                                            Log.d("SUCCESS", url.toString())
                                            val request = okhttp3.Request.Builder()
                                                    .header("User-Agent", App.USER_AGENT)
                                                    // .addHeader("User-Agent", App.USER_AGENT)
                                                    .url(url)
                                                    .get().build()
                                            _this.runOnUiThread {
                                                _this.findViewById<EditText>(R.id.txt_share_string)!!.setText("")
                                                result?.append("download url:$url")
                                                logUtil?.Log("$url")
                                                date.time = System.currentTimeMillis()
                                                openDocumentLauncher.launch("${dateSdf.format(date)}_save.mp4")
                                            }
                                            synchronized(_this) {
                                                _this.downloadUrl = url.toString()
                                                _this.downloadCall = App.getClient().newCall(request)
                                            }
                                        }
                                    }

                                    override fun onFailed(s: String) {
                                        
                                        _this.runOnUiThread {
                                            _this.downloadUrl = null
                                            result?.run {
                                                append(s)
                                            }
                                        }
                                        Log.d("err", s);
                                    }
                                })
                            }

                            override fun onFailed(s: String) {
                                _this.runOnUiThread {
                                    _this.downloadUrl = null
                                    result?.run {
                                        append(s)
                                    }
                                }
                                Log.e("ERR", s)
                            }
                        })
                    }
                    toApp = "after decode:$decode"
                    _this.runOnUiThread {
                        result?.append("\n$toApp\n")
                    }
                    Log.d("YFDC", toApp)

                } else {
                    result?.text = ""
                    toApp = "decode str length(${decode.length}) to low:$decode"
                    result?.append(toApp)
                    Log.e("YFDC", toApp)
                }
            }
            R.id.id_btn_download -> {
                if (ContextCompat.checkSelfPermission(_this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(_this,
                            arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE,
                                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE),
                            0xcafe)
                    return
                }
                download();
                Log.d("YFDC", "click download")
            }
            R.id.id_btn_log -> {
                Log.d("YFDC", "click log")
                // TODO
            }
        }
    }

    private fun download() {
        val btn = findViewById<android.widget.Button>(R.id.id_btn_download)!!
        val activity: MainActivity = this;
        if (downloadUrl.isNullOrEmpty()) {
            Toast.makeText(this, "", Toast.LENGTH_SHORT)
                    .apply { setText("????????????") }.show()
        } else {
            btn.isEnabled = false
            val download: String = downloadUrl!!
            Log.d("YFDC", "click download")
            Toast.makeText(this, "", Toast.LENGTH_SHORT)
                    .apply { setText(downloadUrl) }.show()
            downloadUrl = null;
            val path: String = this.getExternalFilesDir(null)!!.absolutePath
            val file = yfdc.FileUtil.makeFile("$path/save.mp4")
            val fileStream = yfdc.FileUtil.stream(file)
            FileDownloadThread().apply {
                this.out = fileStream
                this.request = okhttp3.Request.Builder()
                        .url(download)
                        .get().build()
                this.init(object : FileDownloadThread.CallBack {
                    override fun onSucceed() {
                        activity.runOnUiThread {
                            btn.setText(R.string.str_download)
                            btn.isEnabled = true
                            activity.result?.append("\n????????????\n")
                        }
                    }

                    override fun onProgress(percent: String) {
                        var s = percent
                        s = activity.resources.getString(R.string.str_download_rate, s)
                        activity.runOnUiThread {
                            btn.isEnabled = false
                            btn.text = s;
                        }
                    }

                    override fun onError(error: Throwable) {
                        activity.runOnUiThread {
                            btn.text = "????????????"
                            btn.isEnabled = true
                            val w1 = java.io.StringWriter()
                            val w2 = java.io.PrintWriter(w1)
                            error.printStackTrace(w2)
                            activity.result?.append("\n$w1")
                            val timer: Timer = Timer()
                            timer.schedule(object : TimerTask() {
                                override fun run() {
                                    activity.runOnUiThread{
                                        btn.setText(R.string.str_download)
                                    }
                                    timer.cancel()
                                }
                            }, 5000L)
                        }
                    }
                })
            }.start()
        }
    }
}