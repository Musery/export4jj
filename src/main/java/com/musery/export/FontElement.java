package com.musery.export;

/** 首页元素 */
public class FontElement {

  private FontElementEnum type;

  private String content;

  public enum FontElementEnum {
    /** 空行 */
    BR,
    /** 图片 */
    PICTURE,
    /** 标题 */
    TITLE,
    /** 副标题 */
    SUBTITLE
  }
}
