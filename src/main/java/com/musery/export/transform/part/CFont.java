package com.musery.export.transform.part;

import cn.hutool.core.lang.UUID;
import com.musery.export.FontElement;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.docx4j.dml.wordprocessingDrawing.Inline;
import org.docx4j.jaxb.Context;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.openpackaging.parts.WordprocessingML.BinaryPartAbstractImage;
import org.docx4j.wml.*;
import org.docx4j.wml.PPrBase.PStyle;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.math.BigInteger;
import java.util.List;

/** 首页配置 */
@Slf4j
public class CFont {

  private static final PPr title;
  private static final PPr subtitle;

  static {
    initPPr(44L, "title", "Title", true);
    initPPr(20L, "subtitle", "Subtitle", false);
    ObjectFactory objectFactory = Context.getWmlObjectFactory();
    title = objectFactory.createPPr();
    PStyle pStyle = objectFactory.createPPrBasePStyle();
    pStyle.setVal("title");
    title.setPStyle(pStyle);

    subtitle = objectFactory.createPPr();
    PStyle subtitleStyle = objectFactory.createPPrBasePStyle();
    subtitleStyle.setVal("subtitle");
    subtitle.setPStyle(subtitleStyle);
  }

  private static void initPPr(Long size, String id, String name, boolean b) {
    ObjectFactory objectFactory = Context.getWmlObjectFactory();
    PPr pPr = objectFactory.createPPr();
    Jc jc = objectFactory.createJc();
    jc.setVal(JcEnumeration.CENTER);
    pPr.setJc(jc);

    RPr rPr = objectFactory.createRPr();
    HpsMeasure hpsMeasure = objectFactory.createHpsMeasure();
    hpsMeasure.setVal(BigInteger.valueOf(size));
    rPr.setSz(hpsMeasure);
    rPr.setSzCs(hpsMeasure);
    if (b) {
      rPr.setB(objectFactory.createBooleanDefaultTrue());
      rPr.setBCs(objectFactory.createBooleanDefaultTrue());
    }
    CStyle.customPStyle(id, name, pPr, rPr);
  }

  public static void init(WordprocessingMLPackage docx, List<FontElement> fontElements) {
    ObjectFactory objectFactory = Context.getWmlObjectFactory();
    docx.getMainDocumentPart().getContent().add(0, getPageBr());
    for (int i = fontElements.size() - 1; i >= 0; i--) {
      FontElement element = fontElements.get(i);
      P p = objectFactory.createP();
      R run = objectFactory.createR();
      p.getContent().add(run);
      Text text = objectFactory.createText();
      switch (element.getType()) {
        case BR:
          run.getContent().add(objectFactory.createBr());
          break;
        case PICTURE:
          try {
            p.setPPr(title);
            BinaryPartAbstractImage image;
            if (element.getContent() instanceof File) {
              image = BinaryPartAbstractImage.createImagePart(docx, (File) element.getContent());
            } else if (element.getContent() instanceof ByteArrayInputStream) {
              try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
                IOUtils.copy(((ByteArrayInputStream) element.getContent()), byteArrayOutputStream);
                image =
                    BinaryPartAbstractImage.createImagePart(
                        docx, byteArrayOutputStream.toByteArray());
              }
            } else {
              break;
            }
            Inline inline =
                image.createImageInline(UUID.fastUUID().toString(true), "", 9999, 9999, false);
            Drawing drawing = objectFactory.createDrawing();
            drawing.getAnchorOrInline().add(inline);
            run.getContent().add(drawing);
          } catch (Exception e) {
            log.error("图片加载失败", e);
          }
          break;
        case TITLE:
          p.setPPr(title);
          text.setValue((String) element.getContent());
          run.getContent().add(text);
          break;
        case SUBTITLE:
          p.setPPr(subtitle);
          text.setValue((String) element.getContent());
          run.getContent().add(text);
          break;
        default:
          break;
      }
      docx.getMainDocumentPart().getContent().add(0, p);
    }
  }

  public static P getPageBr() {
    ObjectFactory objectFactory = Context.getWmlObjectFactory();
    P p = objectFactory.createP();
    R run = objectFactory.createR();
    Br br = objectFactory.createBr();
    br.setType(STBrType.PAGE);
    run.getContent().add(br);
    p.getContent().add(run);
    return p;
  }
}
