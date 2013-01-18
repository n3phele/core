/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License. 
 */
package n3phele.service.n.helpers;


import java.util.Locale;

/**
 * <p>Escapes and unescapes <code>String</code>s for
 * Java, Java Script, HTML, XML, and SQL.</p>
 *
 * @author Apache Software Foundation
 * @author Apache Jakarta Turbine
 * @author Purple Technology
 * @author <a href="mailto:alex@purpletech.com">Alexander Day Chaffee</a>
 * @author Antony Riley
 * @author Helge Tesgaard
 * @author <a href="sean@boohai.com">Sean Brown</a>
 * @author <a href="mailto:ggregory@seagullsw.com">Gary Gregory</a>
 * @author Phil Steitz
 * @author Pete Gieser
 * @since 2.0
 * @version $Id: StringEscapeUtils.java 905636 2010-02-02 14:03:32Z niallp $
 */
public class StringEscapeUtils {


    /**
     * <p><code>StringEscapeUtils</code> instances should NOT be constructed in
     * standard programming.</p>
     *
     * <p>Instead, the class should be used as:
     * <pre>StringEscapeUtils.escapeJava("foo");</pre></p>
     *
     * <p>This constructor is public to permit tools that require a JavaBean
     * instance to operate.</p>
     */
    public StringEscapeUtils() {
      super();
    }

    /**
     * Generates an escaped Java style string, handling control characters,
     * backslashes, double and single quotes.
     * @param str String to escape values in, may be null
     * @returns escaped string
     */
    public static String escapeJavaString(String str) {
    	return escapeJavaStyleString(str, false, false);
    }

    /**
     * Escape strings for Java or Javascript
     * 
     * @param str String to escape values in, may be null
     * @param escapeSingleQuote escapes single quotes if <code>true</code>
     * @param escapeForwardSlash
     * @returns escaped string
     */
    public static String escapeJavaStyleString(String str, boolean escapeSingleQuote,
            boolean escapeForwardSlash) {
        if (str == null) {
            return null;
        }
        StringBuffer out = new StringBuffer();
        int sz;
        sz = str.length();
        for (int i = 0; i < sz; i++) {
            char ch = str.charAt(i);

            // handle unicode
            if (ch > 0xfff) {
                out.append("\\u" + hex(ch));
            } else if (ch > 0xff) {
                out.append("\\u0" + hex(ch));
            } else if (ch > 0x7f) {
                out.append("\\u00" + hex(ch));
            } else if (ch < 32) {
                switch (ch) {
                    case '\b' :
                        out.append('\\');
                        out.append('b');
                        break;
                    case '\n' :
                        out.append('\\');
                        out.append('n');
                        break;
                    case '\t' :
                        out.append('\\');
                        out.append('t');
                        break;
                    case '\f' :
                        out.append('\\');
                        out.append('f');
                        break;
                    case '\r' :
                        out.append('\\');
                        out.append('r');
                        break;
                    default :
                        if (ch > 0xf) {
                            out.append("\\u00" + hex(ch));
                        } else {
                            out.append("\\u000" + hex(ch));
                        }
                        break;
                }
            } else {
                switch (ch) {
                    case '\'' :
                        if (escapeSingleQuote) {
                            out.append('\\');
                        }
                        out.append('\'');
                        break;
                    case '"' :
                        out.append('\\');
                        out.append('"');
                        break;
                    case '\\' :
                        out.append('\\');
                        out.append('\\');
                        break;
                    case '/' :
                        if (escapeForwardSlash) {
                            out.append('\\');
                        }
                        out.append('/');
                        break;
                    default :
                        out.append(ch);
                        break;
                }
            }
        }
        return out.toString();
    }

    /**
     * <p>Returns an upper case hexadecimal <code>String</code> for the given
     * character.</p>
     * 
     * @param ch The character to convert.
     * @return An upper case hexadecimal <code>String</code>
     */
    private static String hex(char ch) {
        return Integer.toHexString(ch).toUpperCase(Locale.ENGLISH);
    }

 

    /**
     * <p>Unescapes any Java literals found in the <code>String</code>.</p>
     *
     * <p>For example, it will turn a sequence of <code>'\'</code> and
     * <code>'n'</code> into a newline character, unless the <code>'\'</code>
     * is preceded by another <code>'\'</code>.</p>
     * 
     * <p>A <code>null</code> string input has no effect.</p>
     * 
     * @param str  the <code>String</code> to unescape, may be null
     * @returns unescaped string
     * @throws IllegalArgumentException if invalid unicode arguments
     */
    public static String unescapeJavaString(String str) {
       
        if (str == null) {
            return null;
        }
        
        StringBuffer out = new StringBuffer();
        int sz = str.length();
        StringBuffer unicode = new StringBuffer(4);
        boolean hadSlash = false;
        boolean inUnicode = false;
        for (int i = 0; i < sz; i++) {
            char ch = str.charAt(i);
            if (inUnicode) {
                // if in unicode, then we're reading unicode
                // values in somehow
                unicode.append(ch);
                if (unicode.length() == 4) {
                    // unicode now contains the four hex digits
                    // which represents our unicode character
                    try {
                        int value = Integer.parseInt(unicode.toString(), 16);
                        out.append((char) value);
                        unicode.setLength(0);
                        inUnicode = false;
                        hadSlash = false;
                    } catch (NumberFormatException nfe) {
                        throw new IllegalArgumentException("Unable to parse unicode value: " + unicode, nfe);
                    }
                }
                continue;
            }
            if (hadSlash) {
                // handle an escaped value
                hadSlash = false;
                switch (ch) {
                    case '\\':
                        out.append('\\');
                        break;
                    case '\'':
                        out.append('\'');
                        break;
                    case '\"':
                        out.append('"');
                        break;
                    case 'r':
                        out.append('\r');
                        break;
                    case 'f':
                        out.append('\f');
                        break;
                    case 't':
                        out.append('\t');
                        break;
                    case 'n':
                        out.append('\n');
                        break;
                    case 'b':
                        out.append('\b');
                        break;
                    case 'u':
                        {
                            // uh-oh, we're in unicode country....
                            inUnicode = true;
                            break;
                        }
                    default :
                        out.append(ch);
                        break;
                }
                continue;
            } else if (ch == '\\') {
                hadSlash = true;
                continue;
            }
            out.append(ch);
        }
        if (hadSlash) {
            // then we're in the weird case of a \ at the end of the
            // string, let's output it anyway.
            out.append('\\');
        }
        return out.toString();
    }


}
