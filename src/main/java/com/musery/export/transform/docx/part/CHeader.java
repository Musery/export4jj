package com.musery.export.transform.docx.part;

import java.math.BigInteger;
import java.util.List;
import org.docx4j.jaxb.Context;
import org.docx4j.model.structure.SectionWrapper;
import org.docx4j.openpackaging.exceptions.InvalidFormatException;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.openpackaging.parts.WordprocessingML.HeaderPart;
import org.docx4j.relationships.Relationship;
import org.docx4j.wml.Hdr;
import org.docx4j.wml.HdrFtrRef;
import org.docx4j.wml.HeaderReference;
import org.docx4j.wml.HpsMeasure;
import org.docx4j.wml.Jc;
import org.docx4j.wml.JcEnumeration;
import org.docx4j.wml.ObjectFactory;
import org.docx4j.wml.P;
import org.docx4j.wml.PPr;
import org.docx4j.wml.PPrBase.PStyle;
import org.docx4j.wml.R;
import org.docx4j.wml.RPr;
import org.docx4j.wml.SectPr;
import org.docx4j.wml.Text;

public class CHeader {

  private static final PPr headerPPr;

  static {
    ObjectFactory objectFactory = Context.getWmlObjectFactory();
    PPr pPr = objectFactory.createPPr();
    Jc jc = objectFactory.createJc();
    jc.setVal(JcEnumeration.RIGHT);
    pPr.setJc(jc);

    RPr rPr = objectFactory.createRPr();
    HpsMeasure hpsMeasure = objectFactory.createHpsMeasure();
    hpsMeasure.setVal(BigInteger.valueOf(12L));
    rPr.setSz(hpsMeasure);
    rPr.setSzCs(hpsMeasure);
    CStyle.customPStyle("header", "Header", pPr, rPr);

    headerPPr = objectFactory.createPPr();
    PStyle pStyle = objectFactory.createPPrBasePStyle();
    pStyle.setVal("header");
    headerPPr.setPStyle(pStyle);
  }

  public static void init(WordprocessingMLPackage docx, String header)
      throws InvalidFormatException {
    ObjectFactory objectFactory = Context.getWmlObjectFactory();
    HeaderPart headerPart = new HeaderPart();
    Hdr hdr = objectFactory.createHdr();
    P p = objectFactory.createP();
    p.setPPr(headerPPr);
    R run = objectFactory.createR();
    Text text = objectFactory.createText();
    text.setValue(header);
    run.getContent().add(text);
    p.getContent().add(run);
    hdr.getContent().add(p);
    headerPart.setJaxbElement(hdr);
    Relationship relationship = docx.getMainDocumentPart().addTargetPart(headerPart);
    List<SectionWrapper> sections = docx.getDocumentModel().getSections();
    SectPr sectPr = sections.get(sections.size() - 1).getSectPr();
    if (sectPr == null) {
      sectPr = objectFactory.createSectPr();
      docx.getMainDocumentPart().addObject(sectPr);
      sections.get(sections.size() - 1).setSectPr(sectPr);
    }
    HeaderReference headerReference = objectFactory.createHeaderReference();
    headerReference.setId(relationship.getId());
    headerReference.setType(HdrFtrRef.DEFAULT);
    sectPr.getEGHdrFtrReferences().add(headerReference);
  }
}
