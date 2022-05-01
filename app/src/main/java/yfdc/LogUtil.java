package yfdc;
import android.content.Context;
import android.content.Intent;
import org.jetbrains.annotations.NotNull;
import kotlin.jvm.internal.Intrinsics;
public final class LogUtil {
    private Context mContext;
    private final LogServConnection con;
    private LogUtil() {
        super();
        con = new LogServConnection();
    }
    @NotNull public static LogUtil getInstance(@NotNull Context c) {
        Intrinsics.checkParameterIsNotNull(c, "c");
        java.util.Objects.requireNonNull(c);
        LogUtil rtn = new LogUtil();
        rtn.mContext = c;
        return rtn;
    }
    public void stop() {
        mContext.unbindService(con);
    }
    public void start() {
        final Intent i = new Intent(mContext, LogService.class);
        mContext.bindService(i, con, Context.BIND_AUTO_CREATE);
    }
    public void Log(String msg) {
        final Context c = this.mContext;
        Intrinsics.checkNotNull(c, "this.mContext is null");
        java.util.Objects.requireNonNull(c);
        if (msg != null) {
            con.log(msg);
        }
    }
}
