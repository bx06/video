package com.ops.www.common.util;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author wangzr
 */
public final class StringUtils {

    private StringUtils() {
    }

    private static final int DEFAULT_ABBREVIATE_MAX_WIDTH = 64;

    private static final String NULL_STRING = "null";

    private static final Pattern PATTERN = Pattern.compile("[0-9]*");

    public static String defaultString(final String str, final String defaultStr) {
        return str == null ? defaultStr : str;
    }

    public static boolean isEmpty(final String string) {
        return string == null || string.isEmpty();
    }

    public static boolean hasLength(final String string) {
        return string != null && string.length() > 0;
    }

    public static boolean hasText(String string) {
        if (isEmpty(string)) {
            return false;
        }

        final int length = string.length();
        for (int i = 0; i < length; i++) {
            if (!Character.isWhitespace(string.charAt(i))) {
                return true;
            }
        }
        return false;
    }

    public static int getLength(final String string) {
        return getLength(string, 0);
    }

    public static int getLength(final String string, final int nullValue) {
        if (string == null) {
            return nullValue;
        }
        return string.length();
    }

    public static String toString(final Object object) {
        if (object == null) {
            return NULL_STRING;
        }
        return object.toString();
    }

    public static String abbreviate(final String str) {
        return abbreviate(str, DEFAULT_ABBREVIATE_MAX_WIDTH);
    }

    public static String abbreviate(final String str, final int maxWidth) {
        if (str == null) {
            return NULL_STRING;
        }
        if (maxWidth < 0) {
            throw new IllegalArgumentException("negative maxWidth:" + maxWidth);
        }
        if (str.length() > maxWidth) {
            StringBuilder buffer = new StringBuilder(maxWidth + 10);
            buffer.append(str, 0, maxWidth);
            appendAbbreviateMessage(buffer, str.length());
            return buffer.toString();
        } else {
            return str;
        }
    }

    public static void appendAbbreviate(final StringBuilder builder, final String str, final int maxWidth) {
        if (str == null) {
            return;
        }
        if (maxWidth < 0) {
            return;
        }
        if (str.length() > maxWidth) {
            builder.append(str, 0, maxWidth);
            appendAbbreviateMessage(builder, str.length());
        } else {
            builder.append(str);
        }
    }

    private static void appendAbbreviateMessage(final StringBuilder buffer, final int strLength) {
        buffer.append("...(");
        buffer.append(strLength);
        buffer.append(')');
    }

    public static List<String> tokenizeToStringList(final String str, final String delimiters) {
        return tokenizeToStringList(str, delimiters, true, true);
    }

    public static List<String> tokenizeToStringList(final String str, final String delimiters, final boolean trimTokens,
                                                    final boolean ignoreEmptyTokens) {

        if (isEmpty(str)) {
            return Collections.emptyList();
        }
        StringTokenizer st = new StringTokenizer(str, delimiters);
        List<String> tokens = new ArrayList<>();
        while (st.hasMoreTokens()) {
            String token = st.nextToken();
            if (trimTokens) {
                token = token.trim();
            }
            if (!ignoreEmptyTokens || token.length() > 0) {
                tokens.add(token);
            }
        }
        return tokens;
    }

    public static boolean isBlank(final CharSequence cs) {
        return cs == null || "".equals(cs.toString().trim());
    }

    public static boolean isIp(String address) {
        String regular = "([1-9]|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])(\\.(\\d|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])){3}";
        Pattern pat = Pattern.compile(regular);
        Matcher mat = pat.matcher(address);
        return mat.find();
    }

    public static boolean isBlank(final Object cs) {
        return cs == null || "".equals(cs.toString().trim());
    }

    public static boolean isNotBlank(final CharSequence cs) {
        return !isBlank(cs);
    }

    public static boolean isCn(String data) {
        boolean flag = false;
        String regex = "^[\u4e00-\u9fa5]*$";
        if (data.matches(regex)) {
            flag = true;
        }
        return flag;
    }

    public static String hexToString(String s) {
        try {
            byte[] baKeyword = new byte[s.length() / 2];
            for (int i = 0; i < baKeyword.length; i++) {
                baKeyword[i] = (byte) (0xff & Integer.parseInt(s.substring(i * 2, i * 2 + 2), 16));
            }
            return new String(baKeyword, StandardCharsets.UTF_8).trim();
        } catch (Exception e) {
            return s;
        }
    }

    public static boolean isNumeric(String str) {
        return PATTERN.matcher(str).matches();
    }

    public static String withOutEmpty(String str) {
        if (isBlank(str)) {
            return null;
        }
        return str.replaceAll(" ", "");
    }

    public static boolean checkString(String str) {
        return (str != null && !"".equals(str.trim()));
    }

    public static String getFileNameWithoutExtend(String name) {
        return name.substring(0, name.lastIndexOf("."));
    }

    public static String getFileExtendName(String name) {
        String dot = ".";
        if (!name.contains(dot)) {
            return null;
        }
        return name.substring(name.lastIndexOf(".") + 1);
    }

    public static boolean isEquals(String s1, String s2) {
        if (s1 == null && s2 == null) {
            return true;
        }
        if (s1 == null || s2 == null) {
            return false;
        }
        return s1.equals(s2);
    }
}
