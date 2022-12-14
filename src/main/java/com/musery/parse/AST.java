package com.musery.parse;

import lombok.Data;

import java.util.List;

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
  // foot
  private String identifier;
  private String label;
  // table
  private List<String> align;

  private String lang;
}
