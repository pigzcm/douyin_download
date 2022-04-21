package yfdc
import android.content.ComponentName
import android.content.ServiceConnection
import android.os.IBinder
import android.util.Log
class LogServConnection :ServiceConnection{
    override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
        Log.d("yfdc","onServiceConnected name:$name")
        service?.let {
            if(it is LogServIdl){
                (it as LogServIdl).log("babe");
            }
        }
    }
    override fun onServiceDisconnected(name: ComponentName?) {
        Log.d("yfdc","onServiceDisconnected name:$name")
    }
    override fun onBindingDied(name: ComponentName?) {
        super.onBindingDied(name)
        Log.d("yfdc","onBind died $name")
    }
    override fun onNullBinding(name: ComponentName?) {
        super.onNullBinding(name)
        Log.d("yfdc","onNullBinding $name")
    }
}
