package yfdc;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;

import kotlin.jvm.internal.Intrinsics;

public final class Tom extends java.io.Writer {
	public static final class Printer extends java.io.PrintWriter {
		private final Tom tom;
		public Printer(@NotNull Tom tom) {
			super((java.io.Writer) nn(tom), false);
			this.tom = tom;
		}
		@Override public final void println(){
			tom.write((int)10);
		}
		@Override public final void println(@Nullable Object o){
			tom.write(String.valueOf(o));
			println();
		}
	}

	private volatile int lines;
	private volatile StringBuffer[] bufs;
	@NotNull public synchronized final StringBuffer[] getBufs(){
		return java.util.Arrays.copyOf(bufs, lines + 1);
	}
	@NotNull
	private static <T> T nn(@Nullable final T obj) {
		Intrinsics.checkParameterIsNotNull(obj, "obj");
		if (obj == null) {
			throw new IllegalStateException("value require not null.");
		}
		return obj;
	}

	public Tom() {
		super();
		this.lines = 0;
		this.bufs = new StringBuffer[20];
		this.bufs[lines] = new StringBuffer();
	}

	@Override
	public final void write(@NotNull final char[] cbuf, int off, int len) {
		Intrinsics.checkParameterIsNotNull(cbuf, "cbuf");
		write(new String(cbuf, off, len));
	}

	@Override
	public void write(@NotNull final char[] cbuf) {
		Intrinsics.checkParameterIsNotNull(cbuf, "cbuf");
		write(new String(cbuf));
	}
	public final void write(int c){
		char[] ch = new char[1];
		ch[0] = (char) c;
		write(ch);
	}

	@Override
	public synchronized final void write(@NotNull final String str) {
		Intrinsics.checkParameterIsNotNull(str, "str");
		char[] arr = str.toCharArray();
		for (char ch : arr) {
			if (ch == '\n') {
				lines += 1;
				if (lines >= bufs.length) {
					bufs = Arrays.copyOf(bufs, bufs.length + 20);
				}
				bufs[lines] = new StringBuffer();
			} else {
				if (ch != '\r') {
					bufs[lines].append(ch);
				}
			}
		}
	}
	@NotNull @Override public final String toString(){
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < lines+1; i++){
			sb.append(bufs[i]).append((char)10);
		}
		return sb.toString();
	}
	@Override
	public final void flush() {
		// do nothing
	}

	@Override
	public final void close() {
		// do nothing
	}
}
