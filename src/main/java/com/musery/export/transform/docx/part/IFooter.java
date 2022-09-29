package com.musery.export.transform.docx.part;

import java.math.BigInteger;
import java.util.List;
import org.docx4j.jaxb.Context;
import org.docx4j.model.structure.SectionWrapper;
import org.docx4j.openpackaging.exceptions.InvalidFormatException;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.openpackaging.parts.WordprocessingML.FooterPart;
import org.docx4j.relationships.Relationship;
import org.docx4j.wml.CTSimpleField;
import org.docx4j.wml.FooterReference;
import org.docx4j.wml.Ftr;
import org.docx4j.wml.HdrFtrRef;
import org.docx4j.wml.HpsMeasure;
import org.docx4j.wml.Jc;
import org.docx4j.wml.JcEnumeration;
import org.docx4j.wml.ObjectFactory;
import org.docx4j.wml.P;
import org.docx4j.wml.PPr;
import org.docx4j.wml.PPrBase.PStyle;
import org.docx4j.wml.RPr;
import org.docx4j.wml.SectPr;

public class IFooter {

  private static final PPr footerPPr;

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
    IStyle.customPStyle("footer", "Footer", pPr, rPr);

    footerPPr = objectFactory.createPPr();
    PStyle pStyle = objectFactory.createPPrBasePStyle();
    pStyle.setVal("footer");
    footerPPr.setPStyle(pStyle);
  }

  public static void init(WordprocessingMLPackage docx) throws InvalidFormatException {
    ObjectFactory objectFactory = Context.getWmlObjectFactory();
    FooterPart footerPart = new FooterPart();
    Ftr ftr = objectFactory.createFtr();
    P p = objectFactory.createP();
    p.setPPr(footerPPr);
    CTSimpleField pageNum = objectFactory.createCTSimpleField();
    pageNum.setInstr(" PAGE \\* MERGEFORMAT ");
    p.getContent().add(objectFactory.createPFldSimple(pageNum));
    ftr.getContent().add(p);
    footerPart.setJaxbElement(ftr);
    Relationship relationship = docx.getMainDocumentPart().addTargetPart(footerPart);
    List<SectionWrapper> sections = docx.getDocumentModel().getSections();
    SectPr sectPr = sections.get(sections.size() - 1).getSectPr();
    if (sectPr == null) {
      sectPr = objectFactory.createSectPr();
      docx.getMainDocumentPart().addObject(sectPr);
      sections.get(sections.size() - 1).setSectPr(sectPr);
    }
    FooterReference footerReference = objectFactory.createFooterReference();
    footerReference.setId(relationship.getId());
    footerReference.setType(HdrFtrRef.DEFAULT);
    sectPr.getEGHdrFtrReferences().add(footerReference);
  }
}
