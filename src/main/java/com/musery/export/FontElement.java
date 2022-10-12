package com.musery.export;

import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;

/** 首页元素 */
@Data
@AllArgsConstructor
public class FontElement {

  private FontElementEnum type;

  private Object content;

  public static List<FontElement> build(int n, Object logo, String title, String... subtitle) {
    List<FontElement> list = new ArrayList<>();
    for (int i = 0; i < n; i++) {
      list.add(new FontElement(FontElementEnum.BR, ""));
    }
    list.add(new FontElement(FontElementEnum.PICTURE, logo));
    list.add(new FontElement(FontElementEnum.TITLE, title));
    for (String sub : subtitle) {
      list.add(new FontElement(FontElementEnum.SUBTITLE, sub));
    }
    return list;
  }
}
