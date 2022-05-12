package yfdc;
import org.jetbrains.annotations.NotNull;
import kotlin.jvm.internal.Intrinsics;
import okhttp3.Response;
import yfdc.bytedance.download.App;
import yfdc.gson.CheckedException;
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
            okhttp3.Request req = chain.request();
            String userAgent = req.header("User-Agent");
            if(userAgent == null){
                userAgent = "";
            }
            if (userAgent.isEmpty() || userAgent.startsWith("okhttp")){
                req = req.newBuilder().header("User-Agent", App.USER_AGENT).build();
            }
            r = chain.proceed(req);
        }catch (Throwable ex){
            throw CheckedException.getInstance(ex);
        }
        Intrinsics.checkNotNull(r, "bad condition");
        return java.util.Objects.requireNonNull(r);
    }
}
