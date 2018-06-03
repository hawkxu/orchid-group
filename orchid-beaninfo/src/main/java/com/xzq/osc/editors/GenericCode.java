/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xzq.osc.editors;

import java.io.File;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URI;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author zqxu
 */
public class GenericCode {

  private static final Map<Class, String> patterns;

  static {
    patterns = new HashMap<Class, String>();
    patterns.put(CodeString.class, "%s");
    patterns.put(String.class, "\"%s\"");
    patterns.put(Class.class, "%s");
    patterns.put(Boolean.class, "%s");
    patterns.put(Byte.class, "(byte) %s");
    patterns.put(Short.class, "(short) %s");
    patterns.put(Integer.class, "%s");
    patterns.put(Long.class, "%sl");
    patterns.put(Float.class, "%sf");
    patterns.put(Double.class, "%sd");
  }

  public static String getInitCode(Object value) {
    if (value == null) {
      return "null";
    }
    Class vClass = value.getClass();
    if (value instanceof Enum) {
      return getEnumCode((Enum) value);
    } else if (value instanceof Date) {
      return getDateCode((Date) value);
    } else if (value instanceof File) {
      return getFileCode((File) value);
    } else if (value instanceof Class) {
      return getClassCode((Class) value);
    } else if (value instanceof URI) {
      return getURICode((URI) value);
    } else if (value instanceof BigInteger) {
      return getBigIntegerCode((BigInteger) value);
    } else if (value instanceof BigDecimal) {
      return getBigDecimalCode((BigDecimal) value);
    }
    if (!patterns.containsKey(vClass)) {
      throw new IllegalArgumentException(
              "Not supported type: " + vClass.getName());
    }
    return String.format(patterns.get(vClass), value);
  }

  public static String getEnumCode(Enum value) {
    if (value == null) {
      return "null";
    }
    return getClassCode(value.getClass()) + "." + value.name();
  }

  public static String getDateCode(Date value) {
    if (value == null) {
      return "null";
    }
    return getInitCode(Date.class, value.getTime());
  }

  public static String getFileCode(File value) {
    if (value == null) {
      return "null";
    }
    return getInitCode(File.class, value.getAbsolutePath());
  }

  public static String getClassCode(Class type) {
    if (type == null) {
      return "null";
    }
    String className = type.getName().replaceAll("\\$", ".");
    if (!className.startsWith("java.lang.")) {
      return className;
    } else {
      return className.substring(10);
    }
  }

  private static String getURICode(URI uri) {
    if (uri == null) {
      return "null";
    }
    return "new java.net.URI(\"" + uri + "\")";
  }

  public static String getInitCode(Class tClass, Object... params) {
    StringBuilder pattern = new StringBuilder("new ");
    pattern.append(getClassCode(tClass)).append("(");
    for (int i = 0; i < params.length - 1; i++) {
      pattern.append("%s, ");
    }
    pattern.append("%s)");
    return GenericCode.format(pattern.toString(), params);
  }

  public static String getBigIntegerCode(BigInteger value) {
    if (value == null) {
      return "null";
    }
    return getInitCode(BigInteger.class, value.toString());
  }

  private static String getBigDecimalCode(BigDecimal value) {
    if (value == null) {
      return "null";
    }
    return getInitCode(BigDecimal.class, value.toString());
  }

  public static String getGenericCode(Class tClass, Class generic,
          Object... params) {
    StringBuilder pattern = new StringBuilder("new ");
    pattern.append(getInitCode(tClass)).append("<")
            .append(getInitCode(generic)).append(">(");
    for (int i = 0; i < params.length - 1; i++) {
      pattern.append("%s, ");
    }
    pattern.append("%s)");
    return GenericCode.format(pattern.toString(), params);
  }

  public static String format(String pattern, Object... params) {
    String[] values = new String[params.length];
    for (int i = 0; i < params.length; i++) {
      if (params[i] == null) {
        values[i] = "null";
      } else {
        values[i] = GenericCode.getInitCode(params[i]);
      }
    }
    return String.format(pattern, (Object[]) values);
  }

  public static class CodeString {

    private String code;

    public CodeString(String code) {
      this.code = code;
    }

    public String getCode() {
      return code;
    }

    @Override
    public String toString() {
      return code;
    }
  }
}
