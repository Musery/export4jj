package com.musery.export.transform;

import cn.hutool.core.collection.CollectionUtil;
import com.musery.parse.AST;
import java.util.List;
import org.docx4j.jaxb.Context;
import org.docx4j.wml.ObjectFactory;
import org.docx4j.wml.Tr;
import org.docx4j.wml.TrPr;

public class TableRow implements DOCX4TR {

  @Override
  public List transform(AST ast) {
    ObjectFactory objectFactory = Context.getWmlObjectFactory();
    Tr tr = objectFactory.createTr();
    TrPr trPr = objectFactory.createTrPr();
    tr.setTrPr(trPr);
    tr.getContent().addAll(traverseChildren(ast));
    return CollectionUtil.newArrayList(tr);
  }

  @Override
  public boolean adapt(AST ast) {
    return ast.getType().equals("tableRow");
  }
}
