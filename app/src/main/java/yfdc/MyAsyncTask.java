package yfdc;

import android.util.Log;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import kotlin.Pair;
import kotlin.jvm.internal.Intrinsics;
import okhttp3.Request;
import okhttp3.Response;
import yfdc.bytedance.download.App;
import yfdc.gson.OkhttpRequest;
import yfdc.gson.OkhttpResponse;

public abstract class MyAsyncTask implements Runnable {
	private static final String TAG = MyAsyncTask.class.getSimpleName();
	public static @NotNull MyAsyncTask execute(@NotNull Request req, @NotNull Good good, @NotNull Bad bad) {
		Intrinsics.checkParameterIsNotNull(req, "req");
		Intrinsics.checkParameterIsNotNull(good, "good");
		Intrinsics.checkParameterIsNotNull(bad, "bad");
		MyAsyncTask create = new MyAsyncTask(good, bad) {
			@Override
			public @NotNull Request getRequest() {
				return req;
			}
		};
		EXECUTOR.submit(create);
		return create;
	}
	private static JsonElement obj(@Nullable final Object oh){
		if(oh == null){
			return JsonNull.INSTANCE;
		}
		if (oh instanceof Response){
			Response response = (Response) oh;
			JsonObject element = new JsonObject();
			element.addProperty("code",response.code());
			element.addProperty("good", response.isSuccessful());
			element.addProperty("msg", response.message());
			JsonArray  headers = new JsonArray();
			java.util.Iterator<Pair<String,String>> it = response.headers().iterator();
			while (it.hasNext()){
				Pair<String,String> p = it.next();
				JsonObject o = new JsonObject();
				o.addProperty(p.component1(),p.component2());
				headers.add(o);
			}
			element.add("headers", headers);
			return element;
		}
		final Gson gson = yfdc.Util.getGson();
		try{
			final String v1 = gson.toJson(oh, oh.getClass());
			final JsonElement element = gson.fromJson(v1, JsonElement.class);
			return (element == null)?JsonNull.INSTANCE:element;
		} catch (Throwable ex){
			JsonObject o = new JsonObject();
			o.addProperty("class", ex.getClass().getName());
			o.addProperty("msg",String.valueOf(ex.getMessage()));
			StackTraceElement[] elements = ex.getStackTrace();
			JsonArray a = new JsonArray();
			for(StackTraceElement element:elements){
				a.add(gson.toJson(element,StackTraceElement.class));
			}
			o.add("stack", a);
			o.addProperty("<err>", true);
			return o;
		}
	}
	@NotNull public final String toString(){
		JsonObject o = new JsonObject();
		o.addProperty("request_url", getRequest().url().toString());
		synchronized (this){
			if (response != null){
				o.add("response", obj(response));
			}
			if (ex != null) {
				o.add("error", obj(ex));
			}
		}
		return o.toString();
	}
	private final Good good;
	private final Bad bad;

	private MyAsyncTask(Good theGood, Bad theBad) {
		super();
		this.good = theGood;
		this.bad = theBad;
	}

	private volatile Response response;
	private volatile Throwable ex;
	private static final ThreadPoolExecutor EXECUTOR =
			new ThreadPoolExecutor(2,
					100,
					60,
					TimeUnit.SECONDS,
					new SynchronousQueue<Runnable>(),
					r -> new Thread(r, "mock ok-http executor"));

	@NotNull
	public abstract Request getRequest();

	@Override
	public final void run() {
		final Request req = getRequest();
		Intrinsics.checkNotNull(req, "getRequest() returns null");
		java.util.Objects.requireNonNull(req);
		Log.d(TAG, "request: " + OkhttpRequest.INSTANCE.toJson(req));
		final okhttp3.Call call = App.getClient().newCall(req);
		try {
			final Response r = call.execute();
			Intrinsics.checkNotNullExpressionValue(r, "call.execute()");
			Log.d(TAG, "response: " + OkhttpResponse.INSTANCE.toJson(r));
			java.util.Objects.requireNonNull(r);
			good.onGood(r);
			this.response = r;
		} catch (Throwable ex0) {
			bad.onBad(ex0);
			this.ex = ex0;
		}
	}

	@Nullable
	public synchronized final Response getResponse() {
		return this.response;
	}

	@Nullable
	public synchronized final Throwable getError() {
		return this.ex;
	}

	@FunctionalInterface
	public static interface Good {
		void onGood(@NotNull Response good);
	}

	@FunctionalInterface
	public static interface Bad {
		void onBad(@NotNull Throwable ex);
	}
}
