package com.musery.export;

import cn.hutool.core.collection.CollectionUtil;
import com.musery.export.transform.*;
import lombok.Data;
import lombok.experimental.Accessors;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;

import java.io.File;
import java.util.List;
import java.util.Map;

@Data
@Accessors(chain = true)
public class ExportOption {

  /** 输出文件路径 */
  private String output;
  /** 输出文件名 */
  private String name;
  /** 输出文件格式 */
  private Format format;
  // 首页信息
  private List<FontElement> font;
  /** 页眉 */
  private String header;
  /** 扩展参数 */
  private Map<String, Object> extend;
  /** 扩展文件 */
  private WordprocessingMLPackage docx;
  /** 解析模组 */
  private List<DOCX4TR> trs;

  public File output() {
    return new File(
        output.endsWith("/")
            ? output + name + "." + format.name()
            : output + "/" + name + "." + format.name());
  }

  public String tmp() {
    return output.endsWith("/") ? output + name + ".tmp" : output + "/" + name + ".tmp";
  }

  public ExportOption() {
    trs =
        CollectionUtil.newLinkedList(
            new Text(),
            new Strong(),
            new Emphasis(),
            new InlineCode(),
            new Delete(),
            new Echarts(),
            new Image(),
            new ILink(),
            new Break(),
            new Paragraph(),
            new Heading(),
            new Code(),
            new Blockquote(),
            new IList(),
            new ThematicBreak(),
            new Table(),
            new TableRow(),
            new TableCell(),
            new Footnote());
  }

  private static ThreadLocal<ExportOption> threadLocal = new ThreadLocal<>();

  public static ThreadLocal<ExportOption> getThreadLocal() {
    return threadLocal;
  }

  public static void prepare(ExportOption doc) {
    threadLocal.set(doc);
  }

  public static void finished() {
    threadLocal.remove();
  }
}
