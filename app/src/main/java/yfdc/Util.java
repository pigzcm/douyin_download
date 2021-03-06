package yfdc;

import org.jetbrains.annotations.NotNull;

import kotlin.jvm.internal.Intrinsics;
import yfdc.gson.OkhttpRequest;
import yfdc.gson.OkhttpResponse;

import com.google.gson.GsonBuilder;
import com.google.gson.Gson;

public final class Util {
	private volatile static Gson gson = null;

	@NotNull
	public static synchronized Gson getGson() {
		if (gson == null) {
			synchronized (Util.class) {
				if (gson == null) {
					gson = new GsonBuilder()
							.serializeNulls()
							.disableHtmlEscaping()
							.registerTypeAdapter(okhttp3.Request.class, OkhttpRequest.INSTANCE)
							.registerTypeAdapter(okhttp3.Response.class, OkhttpResponse.INSTANCE)
							.create();
				}
			}
		}
		return java.util.Objects.requireNonNull(gson);
	}

	private Util() {
		throw new RuntimeException();
	}

	@NotNull
	public static String decodeShare(@NotNull final String from) {
		Intrinsics.checkParameterIsNotNull(from, "from");
		if (from.length() <= 2) {
			throw new IllegalStateException("string to small.");
		}
		char[] arr = from.toCharArray();
		int count = 0;
		char[] newArr = new char[arr.length];
		java.util.Arrays.fill(newArr, (char) 0);
		for (char c : arr) {
			final int ch = (((int) c) & (0xFFFF));
			if ((ch >= 0x20) && (ch <= 0x7e)) {
				newArr[count] = (char) ch;
				count = count + 1;
			}
		}
		newArr = java.util.Arrays.copyOf(newArr, count);
		String rtn = new String(newArr, 0, count);
		int index = rtn.indexOf("https://");
		if (index != -1) {
			int end = index + 29;
			if (end > rtn.length()) {
				end = rtn.length();
			}
			return rtn.substring(index, end);
		} else {
			return from;
			// throw new IllegalStateException("bad condition, url not found.");
		}
	}
}
