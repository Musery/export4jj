package com.musery.export.transform;

import cn.hutool.core.collection.CollectionUtil;
import com.musery.export.ExportOption;
import com.musery.parse.AST;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;

public interface DOCX4TR {

  List transform(AST ast);

  boolean adapt(AST ast);
  /**
   * 遍历子节点
   *
   * @param ast
   */
  default List traverseChildren(AST ast) {
    if (CollectionUtil.isEmpty(ast.getChildren())) {
      return Collections.EMPTY_LIST;
    } else {
      List list = new ArrayList<>();
      for (AST child : ast.getChildren()) {
        list.addAll(traverseOne(child));
      }
      return list;
    }
  }

  /**
   * 处理对应index位置子节点
   *
   * @param ast
   * @param index
   */
  default List traverseChildren(AST ast, int index) {
    if (CollectionUtil.isEmpty(ast.getChildren())) {
      return Collections.EMPTY_LIST;
    } else if (index > -1 && index < ast.getChildren().size()) {
      return traverseOne(ast.getChildren().get(index));
    } else {
      return Collections.EMPTY_LIST;
    }
  }

  default List traverseOne(AST ast) {
    for (DOCX4TR tr : Config.list) {
      if (tr.adapt(ast)) {
        return tr.transform(ast);
      }
    }
    return Collections.EMPTY_LIST;
  }

  default WordprocessingMLPackage getWMLPackage() {
    return (WordprocessingMLPackage) ExportOption.getThreadLocal().get();
  }

  class Config {

    protected static List<DOCX4TR> list = new LinkedList<>();
    // 默认加载包
    static {
      tail(new Text());
      tail(new Strong());
      tail(new Emphasis());
      tail(new InlineCode());
      tail(new Delete());
      tail(new Echarts());
      tail(new Image());
      tail(new Link());
      tail(new Break());
      tail(new Paragraph());
      tail(new Heading());
      tail(new Code());
      tail(new Blockquote());
      tail(new IList());
      tail(new ThematicBreak());
      tail(new Table());
      tail(new TableRow());
      tail(new TableCell());
      tail(new Footnote());
    }

    public static void tail(DOCX4TR tr) {
      list.add(tr);
    }

    public static void head(DOCX4TR tr) {
      list.add(0, tr);
    }
  }
}
