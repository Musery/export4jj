package com.musery.export.transform.docx;

import cn.hutool.core.collection.CollectionUtil;
import com.musery.parse.AST;
import java.util.List;
import org.docx4j.jaxb.Context;
import org.docx4j.wml.CTShd;
import org.docx4j.wml.ObjectFactory;
import org.docx4j.wml.R;
import org.docx4j.wml.RPr;
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
    org.docx4j.wml.Text text = objectFactory.createText();
    text.setValue(ast.getValue());
    RPr rPr = objectFactory.createRPr();
    rPr.setShd(shd);
    R run = objectFactory.createR();
    run.getContent().add(text);
    run.setRPr(rPr);
    return CollectionUtil.newArrayList(run);
  }

  @Override
  public boolean adapt(AST ast) {
    return ast.getType().equals("inlineCode");
  }
}
