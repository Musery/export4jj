package com.musery.export.transform.part;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.lang.UUID;
import com.musery.export.transform.Image;
import jakarta.xml.bind.JAXBElement;
import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import lombok.extern.slf4j.Slf4j;
import org.docx4j.dml.wordprocessingDrawing.Inline;
import org.docx4j.jaxb.Context;
import org.docx4j.openpackaging.contenttype.ContentType;
import org.docx4j.openpackaging.contenttype.ContentTypes;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.openpackaging.parts.DefaultXmlPart;
import org.docx4j.openpackaging.parts.PartName;
import org.docx4j.openpackaging.parts.WordprocessingML.BinaryPartAbstractImage;
import org.docx4j.openpackaging.parts.relationships.Namespaces;
import org.docx4j.relationships.Relationship;
import org.docx4j.wml.Drawing;
import org.docx4j.wml.ObjectFactory;
import org.docx4j.wml.R;

@Slf4j
public class CImage {

  private static BinaryPartAbstractImage error;

  public static void initErrorPng(WordprocessingMLPackage docx) {
    try {
      error =
          BinaryPartAbstractImage.createImagePart(
              docx, FileUtil.file(Image.class.getClassLoader().getResource("error.png")));
    } catch (Exception e) {
      log.error("占位图片加载失败", e);
    }
  }

  public static R buildWithPICT(BinaryPartAbstractImage image, String alt) {
    if (null == image) {
      // 增加占位图
      image = error;
    }
    if (null != image) {
      try {
        Integer id = getId();
        Inline inline = image.createImageInline(UUID.fastUUID().toString(true), alt, id, id, false);
        ObjectFactory objectFactory = Context.getWmlObjectFactory();
        R run = objectFactory.createR();
        Drawing drawing = objectFactory.createDrawing();
        drawing.getAnchorOrInline().add(inline);
        run.getContent().add(drawing);
        return run;
      } catch (Exception e) {
        log.error("insert image into document error", e);
      }
    }
    return null;
  }

  public static R buildWithSvg(WordprocessingMLPackage docx, String svg, String alt) {
    String filenameHint = UUID.fastUUID().toString(true);
    if (null == alt) {
      alt = "";
    }
    try {
      DefaultXmlPart imagePart =
          new DefaultXmlPart(new PartName(String.format("/word/media/%s.svg", filenameHint)));
      imagePart.setRelationshipType(Namespaces.IMAGE);
      imagePart.setContentType(new ContentType(ContentTypes.IMAGE_SVG));
      imagePart.setDocument(new ByteArrayInputStream(svg.getBytes(StandardCharsets.UTF_8)));
      Relationship rel = docx.getMainDocumentPart().addTargetPart(imagePart);
      Map<String, String> map = new HashMap<>();
      map.put("cx", "5627078");
      map.put("cy", "2813539");
      map.put("filenameHint", filenameHint);
      map.put("altText", alt);
      map.put("rEmbedId", rel.getId());
      Integer id = getId();
      map.put("id1", Integer.toString(id));
      map.put("id2", Integer.toString(id));
      Object o = org.docx4j.XmlUtils.unmarshallFromTemplate(SVG_TEMPLATE, map);
      Inline inline = (Inline) ((JAXBElement) o).getValue();
      ObjectFactory objectFactory = Context.getWmlObjectFactory();
      R run = objectFactory.createR();
      Drawing drawing = objectFactory.createDrawing();
      drawing.getAnchorOrInline().add(inline);
      run.getContent().add(drawing);
      return run;
    } catch (Exception e) {
      log.error("图片加载失败", e);
    }
    return buildWithPICT(null, alt);
  }

  private static final AtomicInteger atomicInteger = new AtomicInteger(0);

  private static int getId() {
    atomicInteger.compareAndSet(10000, 0);
    return atomicInteger.getAndIncrement();
  }

  private static final String namespaces =
      " xmlns:w=\"http://schemas.openxmlformats.org/wordprocessingml/2006/main\" "
          + "xmlns:wp=\"http://schemas.openxmlformats.org/drawingml/2006/wordprocessingDrawing\" "
          + "xmlns:a=\"http://schemas.openxmlformats.org/drawingml/2006/main\" "
          + "xmlns:pic=\"http://schemas.openxmlformats.org/drawingml/2006/picture\" "
          + "xmlns:r=\"http://schemas.openxmlformats.org/officeDocument/2006/relationships\" ";

  private static final String SVG_TEMPLATE =
      "<wp:inline distB=\"0\" distL=\"0\" distR=\"0\" distT=\"0\" "
          + namespaces
          + ">\n"
          + "    <wp:extent cx=\"${cx}\" cy=\"${cy}\" />\n"
          + "    <wp:effectExtent b=\"0\" l=\"0\" r=\"9525\" t=\"0\" />\n"
          + "    <wp:docPr id=\"${id1}\" name=\"${filenameHint}\" descr=\"${altText}\" />\n"
          + "    <wp:cNvGraphicFramePr>\n"
          + "        <a:graphicFrameLocks noChangeAspect=\"true\" />\n"
          + "    </wp:cNvGraphicFramePr>\n"
          + "    <a:graphic>\n"
          + "        <a:graphicData uri=\"http://schemas.openxmlformats.org/drawingml/2006/picture\">\n"
          + "            <pic:pic>\n"
          + "                <pic:nvPicPr>\n"
          + "                    <pic:cNvPr id=\"${id2}\" name=\"${filenameHint}\" />\n"
          + "                    <pic:cNvPicPr />\n"
          + "                </pic:nvPicPr>\n"
          + "                <pic:blipFill>\n"
          + "                    <a:blip>\n"
          + "                        <a:extLst>\n"
          + "                            <a:ext uri=\"{96DAC541-7B7A-43D3-8B79-37D633B846F1}\">\n"
          + "                                <asvg:svgBlip xmlns:asvg=\"http://schemas.microsoft.com/office/drawing/2016/SVG/main\" r:embed=\"${rEmbedId}\" />\n"
          + "                            </a:ext>\n"
          + "                        </a:extLst>\n"
          + "                    </a:blip>\n"
          + "                    <a:stretch>\n"
          + "                        <a:fillRect />\n"
          + "                    </a:stretch>\n"
          + "                </pic:blipFill>\n"
          + "                <pic:spPr>\n"
          + "                    <a:xfrm>\n"
          + "                        <a:off x=\"0\" y=\"0\" />\n"
          + "                        <a:ext cx=\"${cx}\" cy=\"${cy}\" />\n"
          + "                    </a:xfrm>\n"
          + "                    <a:prstGeom prst=\"rect\">\n"
          + "                        <a:avLst />\n"
          + "                    </a:prstGeom>\n"
          + "                </pic:spPr>\n"
          + "            </pic:pic>\n"
          + "        </a:graphicData>\n"
          + "    </a:graphic>\n"
          + "</wp:inline>";
}
