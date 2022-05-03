package yfdc.gson;
import kotlin.jvm.internal.Intrinsics;
import okhttp3.Request;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.TypeAdapter;
import com.google.gson.internal.bind.TypeAdapters;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
public final class OkhttpRequest extends TypeAdapter<Request> {
	@Contract("null -> new")
	@NotNull
	public static JsonObject fromRequest(@Nullable final Request r) {
		if (r == null) {
			return new JsonObject();
		} else {
			String json = INSTANCE.toJson(r);
			try {
				JsonElement element = TypeAdapters.JSON_ELEMENT.fromJson(json);
				Intrinsics.checkExpressionValueIsNotNull(element, "TypeAdapters.JSON_ELEMENT.fromJson(...)");
				return (JsonObject) java.util.Objects.requireNonNull(element);
			} catch (Throwable e) {
				throw CheckedException.getInstance(e);
			}
		}
	}

	private OkhttpRequest() {
		super();
	}

	public static final OkhttpRequest INSTANCE = (new OkhttpRequest());
	private static final Request DEFAULT =
			(new Request.Builder())
					.url("https://www.baidu.com/")
					.get().build();

	@Override
	public final void write(@NotNull final JsonWriter out, @NotNull final Request value) {
		Intrinsics.checkParameterIsNotNull(out, "out");
		Intrinsics.checkParameterIsNotNull(value, "value");
		try {
			write0(out, value);
		} catch (final Throwable ex) {
			throw CheckedException.getInstance(ex);
		}
	}

	@Override
	@NotNull
	public final Request read(@Nullable final JsonReader input) {
		Request rtn = null;
		try {
			rtn = read0(input);
		} catch (final Throwable ex) {
			throw CheckedException.getInstance(ex);
		}
		if (rtn == null) {
			return DEFAULT;
		}
		return rtn;
	}

	private Request read0(JsonReader input) throws java.io.IOException {
		if (input == null) {
			return DEFAULT;
		}
		JsonElement element = TypeAdapters.JSON_ELEMENT.read(input);
		if (element == null) {
			throw new IllegalStateException("element == null");
		}
		if (!element.isJsonObject()) {
			throw new IllegalStateException("element isn't JsonObject");
		}
		JsonObject object = (JsonObject) element;
		String url = object.getAsJsonPrimitive("url").getAsString();
		okhttp3.HttpUrl httpUrl = null;
		try {
			httpUrl = okhttp3.HttpUrl.parse(url);
			Intrinsics.checkExpressionValueIsNotNull(httpUrl, "okhttp3.HttpUrl.parse(url)");
		} catch (Throwable ex0) {
			httpUrl = (new okhttp3.HttpUrl.Builder())
					.scheme("http3")
					.host("m.baidu.com")
					.port(443)
					.build();
		}
		return (new Request.Builder()).url(httpUrl).get().build();
	}

	private void write0(JsonWriter out, Request value) throws java.io.IOException {
		out.beginObject();
		out.name("url").value(value.url().toString());
		out.name("method").value(value.method());
		out.name("headers");
		out.beginArray();
		final java.util.Iterator<String> set = value.headers().names().iterator();
		while (set.hasNext()) {
			String item = set.next();
			out.beginObject();
			out.name("key").value(item);
			out.name("value").value(value.header(item));
			out.endObject();
		}
		out.endArray();
		out.name("cache_control");
		out.beginObject();// begin cache control
		final okhttp3.CacheControl cacheControl = value.cacheControl();
		out.name("immutable").value(cacheControl.immutable());
		out.name("maxAgeSeconds").value(cacheControl.maxAgeSeconds());
		out.name("isPrivate").value(cacheControl.isPrivate())
				.name("isPublic").value(cacheControl.isPublic());
		out.name("onlyIfCached").value(cacheControl.onlyIfCached());
		out.name("noCache").value(cacheControl.noCache());
		out.name("noStore").value(cacheControl.noStore());
		out.endObject(); //end cache control
		out.endObject();
	}
}
