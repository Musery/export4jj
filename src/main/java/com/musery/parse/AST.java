package com.musery.parse;

import java.util.List;
import lombok.Data;

/** unified AST 字段 */
@Data
public class AST {
  private String type;
  // heading
  private int depth;
  // inlineCode text
  private String value;
  // link
  private String url;
  // image dynamic charts
  private String alt;
  private String title;
  // List
  private boolean ordered;
  private int start;
  private List<AST> children;
}
