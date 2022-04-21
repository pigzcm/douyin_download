package yfdc;
import org.jetbrains.annotations.NotNull;
import kotlin.jvm.internal.Intrinsics;
public final class Util {
    private Util() {
        throw new RuntimeException();
    }
    @NotNull
    public static String decodeShare(@NotNull final String from) {
        Intrinsics.checkParameterIsNotNull(from, "from");
        if (from.length() <= 2){
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
            if (end > rtn.length()){
                end = rtn.length();
            }
            return rtn.substring(index, end);
        } else {
            return from;
            // throw new IllegalStateException("bad condition, url not found.");
        }
    }
}
