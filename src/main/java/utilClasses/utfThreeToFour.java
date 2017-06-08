package utilClasses;

/**
 * Created by Ashok on 3/24/2017.
 */
public class utfThreeToFour {
    public static final String LAST_3_BYTE_UTF_CHAR = "\uFFFF";
    public static final String REPLACEMENT_CHAR = "\uFFFD";

    public static String toValid3ByteUTF8String(String s)  {
        final int length = s.length();
        StringBuilder b = new StringBuilder(length);
        for (int offset = 0; offset < length; ) {
            final int codepoint = s.codePointAt(offset);

            // do something with the codepoint
            if (codepoint > utfThreeToFour.LAST_3_BYTE_UTF_CHAR.codePointAt(0)) {
                b.append(utfThreeToFour.REPLACEMENT_CHAR);
            } else {
                if (Character.isValidCodePoint(codepoint)) {
                    b.appendCodePoint(codepoint);
                } else {
                    b.append(utfThreeToFour.REPLACEMENT_CHAR);
                }
            }
            offset += Character.charCount(codepoint);
        }
        return b.toString();
    }
}
