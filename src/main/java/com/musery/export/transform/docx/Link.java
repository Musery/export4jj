package com.musery.export.transform.docx;

import com.musery.parse.AST;
import java.util.List;
import org.docx4j.jaxb.Context;
import org.docx4j.openpackaging.parts.relationships.Namespaces;
import org.docx4j.relationships.Relationship;
import org.docx4j.wml.ObjectFactory;
import org.docx4j.wml.P.Hyperlink;
import org.docx4j.wml.R;
import org.docx4j.wml.RStyle;

public class Link implements DOCX4TR {

  private static final RStyle link = Context.getWmlObjectFactory().createRStyle();

  static {
    link.setVal("ad");
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
    return List.of(hyperlink);
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
