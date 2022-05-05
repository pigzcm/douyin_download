package yfdc.gson;

import com.google.gson.JsonObject;
import com.google.gson.TypeAdapter;
import com.google.gson.internal.bind.TypeAdapters;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import kotlin.jvm.internal.Intrinsics;
import okhttp3.MediaType;
import okhttp3.Response;
import okhttp3.ResponseBody;

import java.io.IOException;

public final class OkhttpResponse extends TypeAdapter<Response> {
	private OkhttpResponse() {
		super();
	}

	public static final OkhttpResponse INSTANCE = (new OkhttpResponse());

	@Contract("null -> new")
	@NotNull
	public static JsonObject toJsonObject(@Nullable final Response response) {
		if (response == null) {
			return new JsonObject();
		}
		String json = INSTANCE.toJson(response);
		try {
			JsonObject object = (JsonObject) TypeAdapters.JSON_ELEMENT.fromJson(json);
			Intrinsics.checkExpressionValueIsNotNull(object, "TypeAdapters.JSON_ELEMENT.fromJson()");
			return java.util.Objects.requireNonNull(object);
		} catch (Throwable e) {
			throw CheckedException.getInstance(e);
		}
	}

	@Override
	public final void write(@NotNull final JsonWriter out, @NotNull final Response value) {
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
	public final Response read(@NotNull final JsonReader input) {
		try {
			return read0(input);
		} catch (Throwable ex) {
			throw CheckedException.getInstance(ex);
		}
	}

	private void write0(JsonWriter out, Response value) throws IOException {
		out.beginObject();
		out.name("code").value(value.code());
		out.name("isRedirect").value(value.isRedirect());
		out.name("isSuccessful").value(value.isSuccessful());
		out.name("protocol").value(value.protocol().toString());
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

	private @NotNull Response read0(final JsonReader in) throws java.io.IOException {
		JsonObject object = (JsonObject) TypeAdapters.JSON_ELEMENT.read(in);
		final Response.Builder responseBuilder = new Response.Builder();
		if (object.has("body") &&
				object.get("body").isJsonPrimitive() &&
				object.getAsJsonPrimitive("body").isString()
		) {
			responseBuilder.code(200);
			responseBuilder.message("ok");
			responseBuilder.addHeader("Content-Type", "text/plain;charset=utf-8");
			responseBuilder.body(
					ResponseBody.create(
							object.getAsJsonPrimitive("body").getAsString(),
							MediaType.parse("text/plain;charset=utf-8"))
			);
		} else {
			StringBuilder errmsg = new StringBuilder().append("bad condition:");
			if (!object.has("body")) {
				errmsg.append("json object must have a body.");
			}
			if (!object.get("body").isJsonPrimitive()) {
				errmsg.append(" jsonobj.body is not a json primitive;");
			}
			if (!object.getAsJsonPrimitive("body").isString()) {
				errmsg.append(" jsonobj.body is not a string;");
			}
			throw new IllegalStateException(errmsg.toString());
		}
		return responseBuilder.build();
	}
}
