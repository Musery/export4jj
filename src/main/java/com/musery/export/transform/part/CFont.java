package com.musery.export.transform.part;

import cn.hutool.core.lang.UUID;
import com.musery.export.FontElement;
import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.JAXBException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.docx4j.XmlUtils;
import org.docx4j.dml.Graphic;
import org.docx4j.dml.wordprocessingDrawing.Anchor;
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

/**
 * 首页配置
 */
@Slf4j
public class CFont {

    private static final PPr title;
    private static final PPr subtitle;

    static {
        initPPr(56L, "title", "Title", true);
        initPPr(24L, "subtitle", "Subtitle", false);
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
        jc.setVal(JcEnumeration.LEFT);
        pPr.setJc(jc);

        RPr rPr = objectFactory.createRPr();
        HpsMeasure hpsMeasure = objectFactory.createHpsMeasure();
        hpsMeasure.setVal(BigInteger.valueOf(size));
        rPr.setSz(hpsMeasure);
        rPr.setSzCs(hpsMeasure);
        Color color = new Color();
        color.setVal("1F497D");
        rPr.setColor(color);
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
                case BACKGROUND_PICTURE:
                    try {
                        RPr rPr = objectFactory.createRPr();
                        rPr.setNoProof(new BooleanDefaultTrue());
                        run.setRPr(rPr);
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
                        if (null != image) {
                            Drawing drawing = objectFactory.createDrawing();
                            Object anchorXml = XmlUtils.unmarshalString(generateXmlAnchor(9999, image.getRels().get(0).getId()));
                            Anchor anchor = (Anchor) ((JAXBElement) anchorXml).getValue();
                            drawing.getAnchorOrInline().add(anchor);
                            run.getContent().add(drawing);
                        }
                    } catch (Exception e) {
                        log.error("图片加载失败", e);
                    }
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

    private static String generateXmlAnchor(int id, String rId) {
        return
                "<wp:anchor xmlns:mc=\"http://schemas.openxmlformats.org/markup-compatibility/2006\" xmlns:r=\"http://schemas.openxmlformats.org/officeDocument/2006/relationships\" xmlns:wp=\"http://schemas.openxmlformats.org/drawingml/2006/wordprocessingDrawing\" xmlns:wp14=\"http://schemas.microsoft.com/office/word/2010/wordprocessingDrawing\" distT=\"0\" distB=\"0\" distL=\"114300\" distR=\"114300\" simplePos=\"0\"\n" +
                        "                        relativeHeight=\"251658240\" behindDoc=\"1\" locked=\"0\" layoutInCell=\"1\"\n" +
                        "                        allowOverlap=\"1\">\n" +
                        "                        <wp:simplePos x=\"0\" y=\"0\" />\n" +
                        "                        <wp:positionH relativeFrom=\"page\">\n" +
                        "                            <wp:posOffset>0</wp:posOffset>\n" +
                        "                        </wp:positionH>\n" +
                        "                        <mc:AlternateContent>\n" +
                        "                            <mc:Choice Requires=\"wp14\">\n" +
                        "                                <wp:positionV relativeFrom=\"page\">\n" +
                        "                                    <wp14:pctPosVOffset>0</wp14:pctPosVOffset>\n" +
                        "                                </wp:positionV>\n" +
                        "                            </mc:Choice>\n" +
                        "                            <mc:Fallback>\n" +
                        "                                <wp:positionV relativeFrom=\"page\">\n" +
                        "                                    <wp:posOffset>0</wp:posOffset>\n" +
                        "                                </wp:positionV>\n" +
                        "                            </mc:Fallback>\n" +
                        "                        </mc:AlternateContent>\n" +
                        "                        <wp:extent cx=\"7642800\" cy=\"11386800\" />\n" +
                        "                        <wp:effectExtent l=\"0\" t=\"0\" r=\"3175\" b=\"5715\" />\n" +
                        "                        <wp:wrapNone />\n" +
                        "                        <wp:docPr id=\"" + id +"\" name=\"图片 4\" descr=\"\" />\n" +
                        "                        <wp:cNvGraphicFramePr>\n" +
                        "                            <a:graphicFrameLocks\n" +
                        "                                xmlns:a=\"http://schemas.openxmlformats.org/drawingml/2006/main\"\n" +
                        "                                noChangeAspect=\"1\" />\n" +
                        "                        </wp:cNvGraphicFramePr>\n" +
                        "                        <a:graphic xmlns:a=\"http://schemas.openxmlformats.org/drawingml/2006/main\">\n" +
                        "                            <a:graphicData\n" +
                        "                                uri=\"http://schemas.openxmlformats.org/drawingml/2006/picture\">\n" +
                        "                                <pic:pic\n" +
                        "                                    xmlns:pic=\"http://schemas.openxmlformats.org/drawingml/2006/picture\">\n" +
                        "                                    <pic:nvPicPr>\n" +
                        "                                        <pic:cNvPr id=\"" + id +"\" name=\"图片 4\" descr=\"\" />\n" +
                        "                                        <pic:cNvPicPr />\n" +
                        "                                    </pic:nvPicPr>\n" +
                        "                                    <pic:blipFill>\n" +
                        "                                        <a:blip r:embed=\""+ rId +"\">\n" +
                        "                                            <a:extLst>\n" +
                        "                                                <a:ext uri=\"{28A0092B-C50C-407E-A947-70E740481C1C}\">\n" +
                        "                                                    <a14:useLocalDpi\n" +
                        "                                                        xmlns:a14=\"http://schemas.microsoft.com/office/drawing/2010/main\"\n" +
                        "                                                        val=\"0\" />\n" +
                        "                                                </a:ext>\n" +
                        "                                            </a:extLst>\n" +
                        "                                        </a:blip>\n" +
                        "                                        <a:stretch>\n" +
                        "                                            <a:fillRect />\n" +
                        "                                        </a:stretch>\n" +
                        "                                    </pic:blipFill>\n" +
                        "                                    <pic:spPr>\n" +
                        "                                        <a:xfrm>\n" +
                        "                                            <a:off x=\"0\" y=\"0\" />\n" +
                        "                                            <a:ext cx=\"7642800\" cy=\"11386800\" />\n" +
                        "                                        </a:xfrm>\n" +
                        "                                        <a:prstGeom prst=\"rect\">\n" +
                        "                                            <a:avLst />\n" +
                        "                                        </a:prstGeom>\n" +
                        "                                    </pic:spPr>\n" +
                        "                                </pic:pic>\n" +
                        "                            </a:graphicData>\n" +
                        "                        </a:graphic>\n" +
                        "                        <wp14:sizeRelH relativeFrom=\"margin\">\n" +
                        "                            <wp14:pctWidth>0</wp14:pctWidth>\n" +
                        "                        </wp14:sizeRelH>\n" +
                        "                        <wp14:sizeRelV relativeFrom=\"margin\">\n" +
                        "                            <wp14:pctHeight>0</wp14:pctHeight>\n" +
                        "                        </wp14:sizeRelV>\n" +
                        "                    </wp:anchor>";

    }


}


