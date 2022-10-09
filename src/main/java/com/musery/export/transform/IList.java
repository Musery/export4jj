package com.musery.export.transform;

import com.musery.export.transform.part.CNum;
import com.musery.export.transform.part.CStyle;
import com.musery.parse.AST;
import java.util.ArrayList;
import java.util.List;
import org.docx4j.jaxb.Context;
import org.docx4j.wml.ObjectFactory;
import org.docx4j.wml.P;
import org.docx4j.wml.PPr;
import org.docx4j.wml.PPrBase.PStyle;

public class IList implements DOCX4TR {

  private static final PPr[] orderedList = new PPr[6];
  private static final PPr[] bulletList = new PPr[6];

  static {
    ObjectFactory objectFactory = Context.getWmlObjectFactory();
    for (int i = 0; i < 6; i++) {

      PPr ol = objectFactory.createPPr();
      ol.setContextualSpacing(objectFactory.createBooleanDefaultTrue());
      ol.setNumPr(CNum.getList(true, i));
      CStyle.customPStyle("ol " + i, "OrderList " + i, ol, null);

      orderedList[i] = objectFactory.createPPr();
      PStyle pStyle = objectFactory.createPPrBasePStyle();
      pStyle.setVal("ol " + i);
      orderedList[i].setPStyle(pStyle);

      PPr bl = objectFactory.createPPr();
      bl.setContextualSpacing(objectFactory.createBooleanDefaultTrue());
      bl.setNumPr(CNum.getList(false, i));
      CStyle.customPStyle("bl " + i, "BulletList " + i, bl, null);

      bulletList[i] = objectFactory.createPPr();
      PStyle iPStyle = objectFactory.createPPrBasePStyle();
      iPStyle.setVal("bl " + i);
      bulletList[i].setPStyle(iPStyle);
    }
  }

  @Override
  public List transform(AST ast) {
    return transform(ast, 0);
  }

  private List transform(AST ast, int deep) {
    List<P> list = new ArrayList<>();
    for (AST item : ast.getChildren()) {
      for (AST child : item.getChildren()) {
        switch (child.getType()) {
          case "list":
            for (Object p : transform(child, 1 + deep)) {
              list.add((P) p);
            }
            break;
          default:
            for (Object p : traverseOne(child)) {
              ((P) p).setPPr(ast.isOrdered() ? orderedList[deep] : bulletList[deep]);
              list.add((P) p);
            }
            break;
        }
      }
    }
    return list;
  }

  @Override
  public boolean adapt(AST ast) {
    return ast.getType().equals("list");
  }
}
