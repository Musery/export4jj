package com.musery.export;

import java.io.File;
import java.util.LinkedHashMap;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class ExportOption {

  /** 输出文件路径 */
  private String output;
  /** 输出文件名 */
  private String name;
  /** 输出文件格式 */
  private Format format;
  // 首页 (有序)
  private LinkedHashMap<String, FontElement> font;
  /** 页眉 */
  private String header;

  public File output() {
    return new File(
        output.endsWith("/")
            ? output + name + "." + format.name()
            : output + "/" + name + "." + format.name());
  }

  private static ThreadLocal<Object> threadLocal = new ThreadLocal<>();

  public static ThreadLocal<Object> getThreadLocal() {
    return threadLocal;
  }

  public static void prepare(Object doc) {
    threadLocal.set(doc);
  }

  public static void finished() {
    threadLocal.remove();
  }
}
