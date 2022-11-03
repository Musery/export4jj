package com.musery.export.transform;

import cn.hutool.core.collection.CollectionUtil;
import com.musery.parse.AST;
import org.docx4j.jaxb.Context;
import org.docx4j.wml.ObjectFactory;
import org.docx4j.wml.P;

import java.util.List;

public class Paragraph implements DOCX4TR {

  @Override
  public List transform(AST ast) {
    ObjectFactory objectFactory = Context.getWmlObjectFactory();
    P paragraph = objectFactory.createP();
    paragraph.getContent().addAll(traverseChildren(ast));
    return CollectionUtil.newArrayList(paragraph);
  }

  @Override
  public boolean adapt(AST ast) {
    return ast.getType().equals("paragraph")
        || (ast.getType().equals("html") && ast.getValue().equals("</br>"));
  }
}
