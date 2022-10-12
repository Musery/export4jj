package com.musery.export.transform;

import cn.hutool.core.collection.CollectionUtil;
import com.musery.export.ExportOption;
import com.musery.parse.AST;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
    for (DOCX4TR tr : ExportOption.getThreadLocal().get().getTrs()) {
      if (tr.adapt(ast)) {
        return tr.transform(ast);
      }
    }
    return Collections.EMPTY_LIST;
  }

  default WordprocessingMLPackage getWMLPackage() {
    return ExportOption.getThreadLocal().get().getDocx();
  }
}
