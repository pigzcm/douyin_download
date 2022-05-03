package yfdc.gson;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import kotlin.jvm.internal.Intrinsics;

/**
 * Ignore checked exceptions and throw if occurs.
 */
public final class CheckedException extends RuntimeException {
	private CheckedException(String msg, Throwable cause) {
		super(msg, cause);
	}

	@Contract("_ -> new")
	@NotNull
	public static CheckedException getInstance(@NotNull final Throwable cause) {
		Intrinsics.checkParameterIsNotNull(cause, "cause");
		if (cause instanceof RuntimeException) {
			throw ((RuntimeException) cause);
		}
		String msg = cause.getMessage();
		if (msg == null) {
			msg = cause.getStackTrace()[0].toString();
		}
		if (msg == null) {
			msg = cause.getClass().getName();
		}
		return new CheckedException(msg, cause);
	}
}
