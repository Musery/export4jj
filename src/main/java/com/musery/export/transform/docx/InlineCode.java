package com.musery.export.transform.docx;

import com.musery.parse.AST;
import java.util.List;
import org.docx4j.jaxb.Context;
import org.docx4j.wml.CTShd;
import org.docx4j.wml.ObjectFactory;
import org.docx4j.wml.P.Hyperlink;
import org.docx4j.wml.R;
import org.docx4j.wml.STShd;

public class InlineCode implements DOCX4TR {

  private static final CTShd shd;

  static {
    shd = Context.getWmlObjectFactory().createCTShd();
    shd.setColor("auto");
    shd.setFill("FFFFFF");
    shd.setVal(STShd.PCT_15);
  }

  @Override
  public List transform(AST ast) {
    ObjectFactory objectFactory = Context.getWmlObjectFactory();
    List<Object> list = traverseChildren(ast);
    for (Object r : list) {
      if (r instanceof Hyperlink) {
        for (Object hr : ((Hyperlink) r).getContent()) {
          ((R) hr).getRPr().setShd(shd);
        }
      } else {
        ((R) r).getRPr().setShd(shd);
      }
    }
    return list;
  }

  @Override
  public boolean adapt(AST ast) {
    return ast.getType().equals("inlineCode");
  }
}
