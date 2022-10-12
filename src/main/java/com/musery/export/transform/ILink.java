package com.musery.export.transform;

import cn.hutool.core.collection.CollectionUtil;
import com.musery.export.transform.part.CStyle;
import com.musery.parse.AST;
import org.docx4j.jaxb.Context;
import org.docx4j.openpackaging.parts.relationships.Namespaces;
import org.docx4j.relationships.Relationship;
import org.docx4j.wml.*;
import org.docx4j.wml.P.Hyperlink;

import java.util.List;

public class ILink implements DOCX4TR {

  private static final RStyle link;

  static {
    ObjectFactory objectFactory = Context.getWmlObjectFactory();
    RPr rPr = objectFactory.createRPr();
    rPr.setSmallCaps(objectFactory.createBooleanDefaultTrue());
    Color color = objectFactory.createColor();
    color.setVal("0563C1");
    color.setThemeColor(STThemeColor.HYPERLINK);
    rPr.setColor(color);
    U u = objectFactory.createU();
    u.setVal(UnderlineEnumeration.SINGLE);
    rPr.setU(u);
    CStyle.customCStyle("link", "Hyperlink", rPr);

    link = objectFactory.createRStyle();
    link.setVal("link");
  }

  @Override
  public List transform(AST ast) {
    List<R> list = traverseChildren(ast);
    for (R r : list) {
      r.getRPr().setRStyle(link);
    }
    Relationship relationship = createRS(ast.getUrl());
    ObjectFactory objectFactory = Context.getWmlObjectFactory();
    Hyperlink hyperlink = objectFactory.createPHyperlink();
    hyperlink.getContent().addAll(list);
    hyperlink.setId(relationship.getId());
    return CollectionUtil.newArrayList(hyperlink);
  }

  @Override
  public boolean adapt(AST ast) {
    return ast.getType().equals("link");
  }

  private org.docx4j.relationships.ObjectFactory relationObjectFactory =
      new org.docx4j.relationships.ObjectFactory();

  private Relationship createRS(String url) {
    Relationship relationship = relationObjectFactory.createRelationship();
    relationship.setTargetMode("External");
    relationship.setType(Namespaces.HYPERLINK);
    relationship.setTarget(url);
    getWMLPackage().getMainDocumentPart().getRelationshipsPart().addRelationship(relationship);
    return relationship;
  }
}
