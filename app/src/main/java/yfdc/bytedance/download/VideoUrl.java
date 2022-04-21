package yfdc.bytedance.download;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import kotlin.jvm.internal.Intrinsics;
import okhttp3.HttpUrl;
public final class VideoUrl {
    public static final String VIDEO_URL = "https://aweme.snssdk.com:443/aweme/v1/playwm/";
    @NotNull
    private final HttpUrl.Builder urlBuilder;
    @NotNull @Override public final String toString() {
        return urlBuilder.toString();
    }
    @NotNull public final HttpUrl create(){
        return urlBuilder.build();
    }
    public VideoUrl() {
        super();
        final HttpUrl url = HttpUrl.Companion.parse(VIDEO_URL);
        this.urlBuilder = (url != null) ? url.newBuilder() : (new HttpUrl.Builder())
                .scheme("https")
                .host("aweme.snssdk.com")
                .addPathSegments("aweme/v1/playwm/")
                .port(443);
    }
    private boolean add = false;
    public VideoUrl setParam(@NotNull final String videoId, @Nullable final String ratio) {
        if (add){
            return this;
        }
        add = true;
        Intrinsics.checkParameterIsNotNull(videoId, "video_id");
        urlBuilder.addQueryParameter("video_id", videoId);
        urlBuilder.addQueryParameter("ratio",
                android.text.TextUtils.isEmpty(ratio) ? "720p" : ratio
        );
        urlBuilder.addQueryParameter("line","0");
        return this;
    }
}
