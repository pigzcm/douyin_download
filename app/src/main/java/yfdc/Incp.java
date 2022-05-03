package yfdc;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;
import com.google.gson.JsonObject;
import org.jetbrains.annotations.NotNull;
import kotlin.jvm.internal.Intrinsics;
import okhttp3.Response;
import yfdc.gson.OkhttpRequest;
public final class Incp implements okhttp3.Interceptor{
    private Incp(){
        super();
    }
    public static final Incp INSTANCE = (new Incp());
    @Override
    public @NotNull final Response intercept(@NotNull final Chain chain){
        Intrinsics.checkParameterIsNotNull(chain,"chain");
        Response r = null;
        try {
            final okhttp3.Request req = chain.request();
            final okhttp3.Request.Builder bd = req.newBuilder();
            Log.w("request", OkhttpRequest.INSTANCE.toJson(req));
            // bd.removeHeader("User-Agent");
            bd.removeHeader("Cookie");
            // bd.addHeader("User-Agent", App.USER_AGENT);
            final java.util.Iterator<kotlin.Pair<String, String>> it0 = req.headers().iterator();
            while (it0.hasNext()){
                kotlin.Pair<String, String> pair = it0.next();
                JsonObject o = new JsonObject();
                o.addProperty("key", pair.getFirst());
                o.addProperty("value", pair.getSecond());
                Log.d("YFDC", "_req_header_ " + o.toString());
            }
            r = chain.proceed(bd.build());
            if (r.isRedirect()){
                String location = r.header("location","");
                if (TextUtils.isEmpty(location)){
                    throw new IllegalStateException("need a location header.");
                }
                if (location == null){
                    throw new IllegalStateException("location is null");
                }
                final String httpScheme = "http://";
                if (location.startsWith(httpScheme)){
                    String href = "https://" + location.substring
                            (location.indexOf(httpScheme)+httpScheme.length());
                    Log.d("href",href);
                    Log.d("Incp","2 before:" + location);
                    Log.d("Incp","2 after:" + href);
                    return r.newBuilder()
                            .addHeader("location",href)
                            .header("location",href).build();
                }
                final okhttp3.HttpUrl before = okhttp3.HttpUrl.parse(location);
                Intrinsics.checkNotNull(before,"okhttp3.HttpUrl.parse(location) is null");
                java.util.Objects.requireNonNull(before);
                if(!before.isHttps()){
                    final okhttp3.HttpUrl after = before.newBuilder()
                            .scheme("https")
                            .port(443).build();
                    Log.d("Incp","before:"+before);
                    Log.d("Incp","after:"+after);
                    return r.newBuilder()
                            .header("location",after.toString())
                            .build();
                }
                java.util.Iterator<String> it = r.headers().names().iterator();
                if (Build.VERSION.SDK_INT >= 24){
                    final Response finalR = r;
                    it.forEachRemaining((i)->{
                        JsonObject o = new JsonObject();
                        o.addProperty("name",i);
                        o.addProperty("value",finalR.header(i,"<err>"));
                        Log.d("header",o.toString());
                    });
                } else {
                    while (it.hasNext()){
                        JsonObject o = new JsonObject();
                        final String name = it.next();
                        o.addProperty("name",name);
                        o.addProperty("value",r.header(name,"<err>"));
                        Log.d("header",o.toString());
                    }
                }
                r.close();
            }
        }catch (Throwable ex){
            ex.printStackTrace(System.out);
        }
        Intrinsics.checkNotNull(r, "bad condition");
        return java.util.Objects.requireNonNull(r);
    }
}
