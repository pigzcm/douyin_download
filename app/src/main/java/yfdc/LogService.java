package yfdc;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import org.jetbrains.annotations.NotNull;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Locale;
import kotlin.jvm.internal.Intrinsics;
public final class LogService extends Service {
    public LogService(){
        super();
        Log.d(TAG,"<init>() called");
    }
    private static final String TAG = LogService.class.getSimpleName();
    @NotNull
    @Override
    public final IBinder onBind(Intent intent) {
        return new LogServIdl.Stub(){
            @Override public void log(String msg){
                android.util.Log.d(TAG,"log called");
                android.util.Log.d(TAG,msg);
            }
        };
    }
    @Override
    public final int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG,"servicce!!");
        final Date d = new Date();
        final java.text.SimpleDateFormat sdf
                = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
        if (intent != null){
            String log = intent.getStringExtra("log");
            if (log != null){
                final java.io.File f = this.getExternalFilesDir(null);
                Intrinsics.checkNotNull(f,"this.getExternalFilesDir");
                java.util.Objects.requireNonNull(f);
                String path = f.getAbsolutePath() + "/service_log.txt";
                try {
                    java.io.FileOutputStream s = new java.io.FileOutputStream(path,true);
                    d.setTime(System.currentTimeMillis());
                    StringBuffer buf = new StringBuffer();
                    buf.append(sdf.format(d)).append((char)32);
                    buf.append(log).append('\n');
                    byte[] len = buf.toString().getBytes(StandardCharsets.UTF_8);
                    s.write(len,0,len.length);
                    s.close();
                } catch (Throwable e) {
                    e.printStackTrace(System.out);
                }
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }
}
