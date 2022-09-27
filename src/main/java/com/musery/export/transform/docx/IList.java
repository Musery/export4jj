package com.musery.export.transform.docx;

import com.musery.parse.AST;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import org.docx4j.jaxb.Context;
import org.docx4j.wml.ObjectFactory;
import org.docx4j.wml.P;
import org.docx4j.wml.PPr;
import org.docx4j.wml.PPrBase.Ind;
import org.docx4j.wml.PPrBase.PStyle;

public class IList implements DOCX4TR {

  private static final PPr[] orderedList = new PPr[5];
  private static final String[] styleID = new String[] {"a", "2", "3", "4", "5"};
  private static final PPr[] bulletList = new PPr[5];
  private static final String[] iStyleID = new String[] {"a0", "20", "30", "40", "50"};

  static {
    ObjectFactory objectFactory = Context.getWmlObjectFactory();
    for (int i = 0; i < 5; i++) {

      Ind ind = objectFactory.createPPrBaseInd();
      ind.setLeft(BigInteger.valueOf(420L * (i + 1)));
      ind.setHanging(BigInteger.valueOf(420L));

      orderedList[i] = objectFactory.createPPr();
      PStyle pStyle = objectFactory.createPPrBasePStyle();
      pStyle.setVal(styleID[i]);
      orderedList[i].setPStyle(pStyle);
      orderedList[i].setInd(ind);

      bulletList[i] = objectFactory.createPPr();
      PStyle iPStyle = objectFactory.createPPrBasePStyle();
      iPStyle.setVal(iStyleID[i]);
      orderedList[i].setPStyle(iPStyle);
      orderedList[i].setInd(ind);
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
            for (Object p : transform(child)) {
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
