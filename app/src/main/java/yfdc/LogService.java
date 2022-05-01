package yfdc;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import org.jetbrains.annotations.NotNull;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Locale;
import kotlin.jvm.internal.Intrinsics;
public final class LogService extends Service {
    public LogService() {
        super();
        Log.d(TAG, "<init>() called");
    }
    private static final String TAG = LogService.class.getSimpleName();
    private LogBinder servBinder = null;
    @NotNull
    @Override
    public final IBinder onBind(Intent intent) {
        return new LogBinder(this);
    }
    public static final class LogBinder extends android.os.Binder {
        private java.io.FileOutputStream fos = null;
        public LogBinder(@NotNull Context c) {
            Intrinsics.checkNotNullParameter(c, "c");
            final java.io.File f = c.getExternalFilesDir(null);
            Intrinsics.checkExpressionValueIsNotNull(f, "c.getExternalFilesDir(null)");
            java.util.Objects.requireNonNull(f);
            String path = f.getAbsolutePath() + "/service_log.txt";
            final java.io.File logfile = new java.io.File(path);
            try {
                boolean b = true;
                if (!logfile.exists()) {
                    b = logfile.createNewFile() && logfile.canWrite();
                }
                if (b) {
                    fos = new java.io.FileOutputStream(logfile, true);
                }
            } catch (Throwable ex) {
                ex.printStackTrace(System.out);
            }
        }

        public void log(String msg) {
            final byte[] bytes = msg.getBytes(StandardCharsets.UTF_8);
            if (fos != null) {
                try {
                    fos.write(bytes);
                } catch (Throwable e) {
                    e.printStackTrace(System.out);
                }
            }
        }

        public void close() {
            if (fos != null) {
                try {
                    fos.close();
                } catch (Throwable e) {
                    e.printStackTrace(System.out);
                }
            }
        }
    }
    @Override public final int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "servicce!!");
        if (servBinder == null) {
            servBinder = new LogBinder(this);
        }
        final Date d = new Date();
        final java.text.SimpleDateFormat sdf
                = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
        if (intent != null) {
            String log = intent.getStringExtra("log");
            if (log != null) {
                d.setTime(System.currentTimeMillis());
                servBinder.log("\n" + sdf.format(d) + ' ');
                servBinder.log(log);
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }
}
