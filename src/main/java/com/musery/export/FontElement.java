package com.musery.export;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/** 首页元素 */
@Data
@AllArgsConstructor
public class FontElement {

  private FontElementEnum type;

  private Object content;

  public static List<FontElement> build(
      int n, Object bg, String title, int n2, String... subtitle) {
    List<FontElement> list = new ArrayList<>();
    list.add(new FontElement(FontElementEnum.BACKGROUND_PICTURE, bg));
    for (int i = 0; i < n; i++) {
      list.add(new FontElement(FontElementEnum.BR, ""));
    }
    list.add(new FontElement(FontElementEnum.TITLE, title));
    for (int i = 0; i < n2; i++) {
      list.add(new FontElement(FontElementEnum.BR, ""));
    }
    for (String sub : subtitle) {
      list.add(new FontElement(FontElementEnum.SUBTITLE, sub));
    }
    return list;
  }
}
