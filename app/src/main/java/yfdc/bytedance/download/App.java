package yfdc.bytedance.download;
import org.jetbrains.annotations.NotNull;
import java.util.concurrent.TimeUnit;

import yfdc.Incp;

public final class App extends android.app.Application {
    public App() {
        super();
    }
    private volatile static okhttp3.OkHttpClient client = null;
    public static final String USER_AGENT = "Mozilla/5.0 (Linux; Android 11; PECM30) " +
            "AppleWebKit/537.36 (KHTML, like Gecko) " +
            "Chrome/99.0.4844.73 " +
            "Mobile Safari/537.36";
    public static final String API = "https://www.iesdouyin.com/web/api/v2/aweme/iteminfo/?item_ids=";
    @NotNull
    public static synchronized okhttp3.OkHttpClient getClient() {
        if (client == null) {
            synchronized (App.class) {
                if (client == null) {
                    client = (new okhttp3.OkHttpClient.Builder())
                            .addNetworkInterceptor(Incp.INSTANCE)
                            .callTimeout(50L, TimeUnit.SECONDS)
                            .connectTimeout(50L, TimeUnit.SECONDS)
                            .build();
                }
            }
        }
        return java.util.Objects.requireNonNull(client);
    }
}
