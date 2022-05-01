package yfdc.bytedance.download
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.Window
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatDialog
import java.io.InputStream

class DownloadDialog : AppCompatDialog {
    private val mContext:Context
    private var str:android.net.Uri? = null
    @JvmField var url:okhttp3.Request? = null
    private var progress:ProgressBar? = null
    private var status:TextView? = null
    private var stream:InputStream? = null
    constructor(context: Context,download:android.net.Uri,url:okhttp3.Request) : super(context) {
        mContext = context
        this.str = download
        this.url = url
    }
    constructor(context: Context, style: Int) : super(context, style) {
        mContext = context
    }
    constructor(context: Context, cancelAble: Boolean, listener: DialogInterface.OnCancelListener) : super(context, cancelAble, listener) {
        mContext = context
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.download_dialog)
        progress = findViewById(R.id.status_p)!!
        status = findViewById(R.id.status)!!
        setCancelable(false)
        setCanceledOnTouchOutside(false)
        val t:FileDownloadThread = FileDownloadThread()
        t.request = this.url
        t.out = mContext.applicationContext.contentResolver.openOutputStream(str!!)
        t.init(object:FileDownloadThread.CallBack{
            override fun onSucceed() {
                this@DownloadDialog.dismiss()
                runOnUIThread{
                    Toast.makeText(mContext,"",Toast.LENGTH_SHORT)
                            .apply { setText("下载成功") }
                            .show()
                }
            }
            override fun onProgress(percent: String) {
                var s = percent
                s = this@DownloadDialog.mContext.resources.getString(R.string.str_download_rate,s)
                runOnUIThread{
                    status?.text = s
                    progress?.progress = percent.toDouble().toInt() ?:0
                }
            }
            override fun onError(error: Throwable) {
                error.printStackTrace(System.out)
            }
        })
        t.start()
    }
    private val mHandler:Handler = Handler(Looper.getMainLooper()!!)
    private fun runOnUIThread(r:Runnable){
        mHandler.post(r)
    }
    private fun exit(){
        runOnUIThread{
            dismiss()
        }
    }
}