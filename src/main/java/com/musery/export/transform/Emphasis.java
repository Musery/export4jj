package com.musery.export.transform;

import com.musery.parse.AST;
import java.util.List;
import org.docx4j.jaxb.Context;
import org.docx4j.wml.ObjectFactory;
import org.docx4j.wml.P.Hyperlink;
import org.docx4j.wml.R;

public class Emphasis implements DOCX4TR {

  @Override
  public List transform(AST ast) {
    ObjectFactory objectFactory = Context.getWmlObjectFactory();
    List<Object> list = traverseChildren(ast);
    for (Object r : list) {
      if (r instanceof Hyperlink) {
        for (Object hr : ((Hyperlink) r).getContent()) {
          ((R) hr).getRPr().setI(objectFactory.createBooleanDefaultTrue());
          ((R) hr).getRPr().setICs(objectFactory.createBooleanDefaultTrue());
        }
      } else {
        ((R) r).getRPr().setI(objectFactory.createBooleanDefaultTrue());
        ((R) r).getRPr().setICs(objectFactory.createBooleanDefaultTrue());
      }
    }
    return list;
  }

  @Override
  public boolean adapt(AST ast) {
    return ast.getType().equals("emphasis");
  }
}
