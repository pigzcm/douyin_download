package yfdc;
import android.content.Context;
import android.content.Intent;
import org.jetbrains.annotations.NotNull;
import kotlin.jvm.internal.Intrinsics;
public final class LogUtil {
    private Context mContext;
    private LogUtil(){
        super();
    }
    @NotNull public static LogUtil getInstance(@NotNull Context c){
        Intrinsics.checkParameterIsNotNull(c,"c");
        java.util.Objects.requireNonNull(c);
        LogUtil rtn = new LogUtil();
        rtn.mContext = c;
        return rtn;
    }

    public void Log(String msg){
        final Context c = this.mContext;
        Intrinsics.checkNotNull(c,"this.mContext is null");
        java.util.Objects.requireNonNull(c);
        if (msg != null){
            Intent i = new Intent(c,LogService.class);
            i.putExtra("log", msg);
            c.startService(i);
            c.bindService(i,new LogServConnection(),0);
        }
    }
}
