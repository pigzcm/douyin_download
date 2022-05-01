package yfdc.bytedance.download;

import androidx.activity.result.contract.ActivityResultContract;
import androidx.annotation.NonNull;

import org.jetbrains.annotations.Nullable;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import org.jetbrains.annotations.NotNull;

import kotlin.jvm.internal.Intrinsics;

public final class Document extends ActivityResultContract<@NotNull String,
		@Nullable Uri> {
	private Document() {
		super();
	}

	public static final Document INSTANCE = (new Document());

	@NotNull
	@Override
	public final Intent createIntent(@NotNull final Context context, @NotNull final String input) {
		Intrinsics.checkParameterIsNotNull(context, "context");
		Intrinsics.checkParameterIsNotNull(input, "input");
		final Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
		intent.setType("video/mp4");
		intent.addCategory(Intent.CATEGORY_OPENABLE);
		intent.putExtra(Intent.EXTRA_TITLE, input);
		return intent;
	}

	@NotNull
	public static Intent createIntent0(@NotNull final Context context, @NotNull final String input) {
		return INSTANCE.createIntent(context, input);
	}

	@Nullable
	public static Uri parseResult0(int resultCode, @Nullable final Intent intent) {
		return INSTANCE.parseResult(resultCode, intent);
	}

	@Nullable
	public static SynchronousResult<Uri> getSynchronousResult0() {
		return null;
	}

	@Nullable
	@Override
	public final SynchronousResult<Uri> getSynchronousResult(@NonNull Context context, String input) {
		return null;
	}

	@Nullable
	@Override
	public final Uri parseResult(int resultCode, @Nullable final Intent intent) {
		if (resultCode != android.app.Activity.RESULT_OK || intent == null)
			return null;
		return intent.getData();
	}
}
