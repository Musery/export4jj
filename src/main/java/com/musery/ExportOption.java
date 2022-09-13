package com.musery;

import java.io.File;
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
  /** 页眉 */
  private String header;
  /** 页脚 */
  private String footer;

  // todo style

  public File output() {
    return new File(
        output.endsWith("/")
            ? output + name + "." + format.name()
            : output + "/" + name + "." + format.name());
  }
}
