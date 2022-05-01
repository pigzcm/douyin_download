package yfdc
import android.content.ComponentName
import android.content.ServiceConnection
import android.os.IBinder
import android.util.Log
class LogServConnection :ServiceConnection{
    companion object{
        const val TAG = "Service-Connection"
    }
    @JvmField public var yfInter:LogServIdl? = null;
    private var binder:LogService.LogBinder? = null;
    fun log(msg:String){
        binder?.log("$msg\n")
    }
    override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
        Log.d(TAG,"onServiceConnected name:$name sv:$service")
        if (service === null) {
            return
        }
        this.binder = (service as LogService.LogBinder)
    }
    override fun onServiceDisconnected(name: ComponentName?) {
        Log.d(TAG,"onServiceDisconnected name:$name")
        binder?.close()
        this.yfInter = null;
    }
    override fun onBindingDied(name: ComponentName?) {
        super.onBindingDied(name)
        binder?.close()
        this.yfInter = null
        Log.d(TAG,"onBind died $name")
    }
    override fun onNullBinding(name: ComponentName?) {
        super.onNullBinding(name)
        binder?.close()
        this.yfInter = null
        Log.d(TAG,"onNullBinding $name")
    }
}
