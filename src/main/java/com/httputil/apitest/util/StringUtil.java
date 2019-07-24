package com.httputil.apitest.util;

import org.apache.commons.lang.StringUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;


/**
 * Created by yangyu on 2019/3/20.
 */
public class StringUtil {
    public StringUtil() {
    }

    public static String repeat(String str, int times) {
        StringBuilder buffer = new StringBuilder(str.length() * times);

        for (int i = 0; i < times; ++i) {
            buffer.append(str);
        }

        return buffer.toString();
    }

    public static List<String> asList(String... strings) {
        ArrayList listOfStrings = new ArrayList(strings.length);

        for (int i = 0; i < strings.length; ++i) {
            listOfStrings.add(strings[i]);
        }

        return listOfStrings;
    }

    public static String newEmail() {
        StringBuffer buffer = new StringBuffer("0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ");
        StringBuffer sb = new StringBuffer();
        Random r = new Random();
        int range = buffer.length();

        for (int i = 0; i < 15; ++i) {
            sb.append(buffer.charAt(r.nextInt(range)));
        }

        sb.append("@apitest.com");
        return sb.toString();
    }

    public static String newString(int length) {
        StringBuffer buffer = new StringBuffer("abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ");
        StringBuffer sb = new StringBuffer();
        Random r = new Random();
        int range = buffer.length();

        for (int i = 0; i < length; ++i) {
            sb.append(buffer.charAt(r.nextInt(range)));
        }

        return sb.toString();
    }

    public static String newIntString(int length) {
        StringBuffer buffer = new StringBuffer("123456789");
        StringBuffer sb = new StringBuffer();
        Random r = new Random();
        int range = buffer.length();

        for (int i = 0; i < length; ++i) {
            sb.append(buffer.charAt(r.nextInt(range)));
        }

        return sb.toString();
    }

    public static String newString(String seed, int length) {
        StringBuffer buffer = new StringBuffer(seed);
        StringBuffer sb = new StringBuffer();
        Random r = new Random();
        int range = buffer.length();

        for (int i = 0; i < length; ++i) {
            sb.append(buffer.charAt(r.nextInt(range)));
        }

        return sb.toString();
    }

    public static String normalize(String str) {
        return str.trim().toUpperCase();
    }

    public static int length(String str) {
        return str.length();
    }

    public static boolean isEmpty(String value) {
        int strLen;
        if (value != null && (strLen = value.length()) != 0) {
            for (int i = 0; i < strLen; ++i) {
                if (!Character.isWhitespace(value.charAt(i))) {
                    return false;
                }
            }

            return true;
        } else {
            return true;
        }
    }

    public static boolean isNumeric(Object obj) {
        if (obj == null) {
            return false;
        } else {
            String str = obj.toString();
            int sz = str.length();

            for (int i = 0; i < sz; ++i) {
                if (!Character.isDigit(str.charAt(i))) {
                    return false;
                }
            }

            return true;
        }
    }

    public static boolean areNotEmpty(String... values) {
        boolean result = true;
        if (values != null && values.length != 0) {
            String[] var2 = values;
            int var3 = values.length;

            for (int var4 = 0; var4 < var3; ++var4) {
                String value = var2[var4];
                result &= !isEmpty(value);
            }
        } else {
            result = false;
        }

        return result;
    }

    public static String unicodeToChinese(String unicode) {
        StringBuilder out = new StringBuilder();
        if (!isEmpty(unicode)) {
            for (int i = 0; i < unicode.length(); ++i) {
                out.append(unicode.charAt(i));
            }
        }

        return out.toString();
    }

    public static String convertUnicode(String ori) {
        int len = ori.length();
        StringBuffer outBuffer = new StringBuffer(len);
        int x = 0;

        while (true) {
            while (true) {
                while (x < len) {
                    char aChar = ori.charAt(x++);
                    if (aChar == 92) {
                        aChar = ori.charAt(x++);
                        if (aChar == 117) {
                            int value = 0;

                            for (int i = 0; i < 4; ++i) {
                                aChar = ori.charAt(x++);
                                switch (aChar) {
                                    case '0':
                                    case '1':
                                    case '2':
                                    case '3':
                                    case '4':
                                    case '5':
                                    case '6':
                                    case '7':
                                    case '8':
                                    case '9':
                                        value = (value << 4) + aChar - 48;
                                        break;
                                    case ':':
                                    case ';':
                                    case '<':
                                    case '=':
                                    case '>':
                                    case '?':
                                    case '@':
                                    case 'G':
                                    case 'H':
                                    case 'I':
                                    case 'J':
                                    case 'K':
                                    case 'L':
                                    case 'M':
                                    case 'N':
                                    case 'O':
                                    case 'P':
                                    case 'Q':
                                    case 'R':
                                    case 'S':
                                    case 'T':
                                    case 'U':
                                    case 'V':
                                    case 'W':
                                    case 'X':
                                    case 'Y':
                                    case 'Z':
                                    case '[':
                                    case '\\':
                                    case ']':
                                    case '^':
                                    case '_':
                                    case '`':
                                    default:
                                        throw new IllegalArgumentException("Malformed   \\uxxxx   encoding.");
                                    case 'A':
                                    case 'B':
                                    case 'C':
                                    case 'D':
                                    case 'E':
                                    case 'F':
                                        value = (value << 4) + 10 + aChar - 65;
                                        break;
                                    case 'a':
                                    case 'b':
                                    case 'c':
                                    case 'd':
                                    case 'e':
                                    case 'f':
                                        value = (value << 4) + 10 + aChar - 97;
                                }
                            }

                            outBuffer.append((char) value);
                        } else {
                            if (aChar == 116) {
                                aChar = 9;
                            } else if (aChar == 114) {
                                aChar = 13;
                            } else if (aChar == 110) {
                                aChar = 10;
                            } else if (aChar == 102) {
                                aChar = 12;
                            }

                            outBuffer.append(aChar);
                        }
                    } else {
                        outBuffer.append(aChar);
                    }
                }

                return outBuffer.toString();
            }
        }
    }

    public static String stripNonValidXMLCharacters(String input) {
        if (input != null && !"".equals(input)) {
            StringBuilder out = new StringBuilder();

            for (int i = 0; i < input.length(); ++i) {
                char current = input.charAt(i);
                if (current == 9 || current == 10 || current == 13 || current >= 32 && current <= '\ud7ff' || current >= '\ue000' && current <= '�' || current >= 65536 && current <= 1114111) {
                    out.append(current);
                }
            }

            return out.toString();
        } else {
            return "";
        }
    }

    public static String findFile(String input) {
        String target = "";
        String TAIL = ".json";
        if (input != null && !"".equals(input)) {
            target = StringUtils.substringAfterLast(input, "/");
            String filename = target.concat(TAIL);
            File file = new File("target/classes/" + filename);
            return file.exists() ? filename : target;
        } else {
            return target;
        }
    }
}
