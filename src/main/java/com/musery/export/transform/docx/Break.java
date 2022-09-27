package com.musery.export.transform.docx;

import cn.hutool.core.collection.CollectionUtil;
import com.musery.parse.AST;
import java.util.List;
import org.docx4j.jaxb.Context;
import org.docx4j.wml.ObjectFactory;
import org.docx4j.wml.R;
import org.docx4j.wml.RPr;

public class Break implements DOCX4TR {

  @Override
  public List transform(AST ast) {
    ObjectFactory objectFactory = Context.getWmlObjectFactory();
    R run = objectFactory.createR();
    run.getContent().add(objectFactory.createBr());
    RPr rPr = objectFactory.createRPr();
    run.setRPr(rPr);
    return CollectionUtil.newArrayList(run);
  }

  @Override
  public boolean adapt(AST ast) {
    return ast.getType().equals("break");
  }
}
