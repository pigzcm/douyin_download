package yfdc.bytedance.download;
import android.util.Log;
import org.jetbrains.annotations.NotNull;

import java.io.InputStream;
import java.util.Locale;
import kotlin.jvm.internal.Intrinsics;
import okhttp3.Call;
public final class FileDownloadThread extends Thread {
    public static interface CallBack {
        void onSucceed();
        void onProgress(@NotNull String percent);
        void onError(@NotNull Throwable error);
    }
    public FileDownloadThread init(@NotNull final CallBack c) {
        Intrinsics.checkParameterIsNotNull(c,"c");
        callback = c;
        return this;
    }
    public okhttp3.Request request;
    private CallBack callback = null;
    public java.io.InputStream stream = null;
    public java.io.OutputStream out = null;

    public FileDownloadThread() {
        super((Runnable) null, "file-download-thread");
    }

    public void run() {
        try {
            Call call = App.getClient().newCall(request);
            okhttp3.Response resp = call.execute();
            long totalL = 0;
            if (resp.isSuccessful()) {
                final okhttp3.ResponseBody respbd = resp.body();
                Intrinsics.checkNotNull(respbd, "response body require not null");
                java.util.Objects.requireNonNull(respbd);
                stream = respbd.byteStream();
                totalL = respbd.contentLength();
                if (totalL > Integer.MAX_VALUE) {
                    Log.e("Thread", "ccontent lenth too large");
                }
            } else {
                return;
            }
            if (out == null) {
                return;
            }
            if (stream == null) {
                return;
            }
            byte[] buf = new byte[1024];
            int ch = 0;
            int total = Integer.MAX_VALUE;
            int position = 0;
            try {
                total = stream.available();
            } catch (Throwable ex) {
                // ignore
            }
            if (total <= 0) {
                while (totalL > Integer.MAX_VALUE) {
                    totalL /= 10;
                    Log.e("Thread", "too large");
                }
                total = (int) totalL;
            }
            //while ((ch = stream.read(buf, 0, 1024)) != (-1)) {
            while ((ch = readMy(stream, buf)) != (-1)) {
                out.write(buf, 0, ch);
                position += ch;
                String m = String.format(Locale.ENGLISH, "%.2f", ((double) ((double) position * 100.0d) / (double) total));
                if (callback != null) {
                    callback.onProgress(m);
                }
            }
            try {
                out.close();
            } catch (Throwable ex) {
                // ignore
            }
            if (callback != null) {
                callback.onProgress("100.00");
                callback.onSucceed();
            }
        } catch (Throwable ex) {
            if (callback != null) {
                callback.onError(ex);
            }
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (Throwable e) {
                    // ignore
                }
            }
            if (stream != null) {
                try {
                    stream.close();
                } catch (Throwable e) {
                    // ignore
                }
            }
        }
    }
    private static int readMy(InputStream in, byte[] buf){
        if (in == null){
            return (-1);
        }
        int rtn = (-1);
        try {
            rtn = in.read(buf, 0, 1024);
        }catch (Throwable ex){
            Throwable e = null;
            if (ex.getCause() == null){
                Log.e("YDC",ex.toString(),ex);
                ex.printStackTrace(System.out);
            } else {
                e = ex.getCause();
                do {
                    e = e.getCause();
                    if (e != null){
                        Log.e("YFDC",e.getClass().getName()+':'+e.getMessage());
                    }
                }while ((e != null)&&!e.equals(e.getCause()));
            }
            throw new IllegalStateException("cafe babe");
        }
        return rtn;
    }
}
