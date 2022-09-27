package com.musery.export.transform.docx;

import com.musery.parse.AST;
import java.util.List;
import org.docx4j.jaxb.Context;
import org.docx4j.wml.ObjectFactory;
import org.docx4j.wml.P.Hyperlink;
import org.docx4j.wml.R;

public class Delete implements DOCX4TR {

  @Override
  public List transform(AST ast) {
    ObjectFactory objectFactory = Context.getWmlObjectFactory();
    List<Object> list = traverseChildren(ast);
    for (Object r : list) {
      if (r instanceof Hyperlink) {
        for (Object hr : ((Hyperlink) r).getContent()) {
          ((R) hr).getRPr().setStrike(objectFactory.createBooleanDefaultTrue());
        }
      } else {
        ((R) r).getRPr().setStrike(objectFactory.createBooleanDefaultTrue());
      }
    }
    return list;
  }

  @Override
  public boolean adapt(AST ast) {
    return ast.getType().equals("delete");
  }
}
