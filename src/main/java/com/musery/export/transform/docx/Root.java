package com.musery.export.transform.docx;

import cn.hutool.core.collection.CollectionUtil;
import com.musery.parse.AST;
import java.util.List;
import org.docx4j.jaxb.Context;
import org.docx4j.wml.Body;
import org.docx4j.wml.ObjectFactory;

public class Root implements DOCX4TR {

  @Override
  public List transform(AST ast) {
    ObjectFactory objectFactory = Context.getWmlObjectFactory();
    Body body = objectFactory.createBody();
    body.getContent().addAll(traverseChildren(ast));
    return CollectionUtil.newArrayList(body);
  }

  @Override
  public boolean adapt(AST ast) {
    return ast.getType().equals("root");
  }
}
