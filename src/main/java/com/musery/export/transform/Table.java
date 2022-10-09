package com.musery.export.transform;

import cn.hutool.core.collection.CollectionUtil;
import com.musery.export.transform.part.CStyle;
import com.musery.parse.AST;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import org.docx4j.jaxb.Context;
import org.docx4j.sharedtypes.STOnOff;
import org.docx4j.wml.CTBorder;
import org.docx4j.wml.CTCnf;
import org.docx4j.wml.CTShd;
import org.docx4j.wml.CTTblLook;
import org.docx4j.wml.CTTblPrBase.TblStyle;
import org.docx4j.wml.CTTblPrBase.TblStyleColBandSize;
import org.docx4j.wml.CTTblPrBase.TblStyleRowBandSize;
import org.docx4j.wml.CTTblStylePr;
import org.docx4j.wml.Color;
import org.docx4j.wml.ObjectFactory;
import org.docx4j.wml.P;
import org.docx4j.wml.RPr;
import org.docx4j.wml.STBorder;
import org.docx4j.wml.STShd;
import org.docx4j.wml.STTblStyleOverrideType;
import org.docx4j.wml.STThemeColor;
import org.docx4j.wml.Tbl;
import org.docx4j.wml.TblBorders;
import org.docx4j.wml.TblGrid;
import org.docx4j.wml.TblGridCol;
import org.docx4j.wml.TblPr;
import org.docx4j.wml.TblWidth;
import org.docx4j.wml.Tc;
import org.docx4j.wml.TcPr;
import org.docx4j.wml.TcPrInner.TcBorders;
import org.docx4j.wml.Tr;

public class Table implements DOCX4TR {

  private static final TblPr table;

  private static final CTCnf firstRow;
  private static final CTCnf firstCol;
  private static final CTCnf oddHBand;
  private static final CTCnf nothing;

  static {
    ObjectFactory objectFactory = Context.getWmlObjectFactory();
    TblPr tblPr = objectFactory.createTblPr();
    TblStyleRowBandSize styleRowBandSize = objectFactory.createCTTblPrBaseTblStyleRowBandSize();
    styleRowBandSize.setVal(BigInteger.ONE);
    tblPr.setTblStyleRowBandSize(styleRowBandSize);
    TblStyleColBandSize styleColBandSize = objectFactory.createCTTblPrBaseTblStyleColBandSize();
    styleColBandSize.setVal(BigInteger.ONE);
    tblPr.setTblStyleColBandSize(styleColBandSize);
    TblBorders tblBorders = objectFactory.createTblBorders();
    CTBorder ctBorder = objectFactory.createCTBorder();
    ctBorder.setVal(STBorder.SINGLE);
    ctBorder.setSz(BigInteger.valueOf(4L));
    ctBorder.setSpace(BigInteger.ZERO);
    ctBorder.setColor("C9C9C9");
    ctBorder.setThemeColor(STThemeColor.ACCENT_3);
    ctBorder.setThemeTint("99");
    tblBorders.setTop(ctBorder);
    tblBorders.setBottom(ctBorder);
    tblBorders.setLeft(ctBorder);
    tblBorders.setRight(ctBorder);
    tblBorders.setInsideH(ctBorder);
    tblBorders.setInsideV(ctBorder);
    tblPr.setTblBorders(tblBorders);

    List<CTTblStylePr> list = new ArrayList<>();
    list.add(buildFirstRowPr());
    list.add(buildLastRowPr());
    list.add(buildColPr(STTblStyleOverrideType.FIRST_COL));
    list.add(buildColPr(STTblStyleOverrideType.LAST_COL));
    list.add(buildBandPr(STTblStyleOverrideType.BAND_1_VERT));
    list.add(buildBandPr(STTblStyleOverrideType.BAND_1_HORZ));

    CStyle.customTStyle("i-table", "ITable", tblPr, list);

    table = objectFactory.createTblPr();
    TblStyle tblStyle = objectFactory.createCTTblPrBaseTblStyle();
    tblStyle.setVal("i-table");
    table.setTblStyle(tblStyle);
    TblWidth tblWidth = objectFactory.createTblWidth();
    tblWidth.setW(BigInteger.ZERO);
    tblWidth.setType("auto");
    table.setTblW(tblWidth);
    CTTblLook ctTblLook = objectFactory.createCTTblLook();
    ctTblLook.setVal("04A0");
    ctTblLook.setFirstRow(STOnOff.ONE);
    ctTblLook.setLastRow(STOnOff.ZERO);
    ctTblLook.setFirstColumn(STOnOff.ONE);
    ctTblLook.setLastColumn(STOnOff.ZERO);
    ctTblLook.setNoHBand(STOnOff.ZERO);
    ctTblLook.setNoVBand(STOnOff.ONE);
    table.setTblLook(ctTblLook);

    firstRow = objectFactory.createCTCnf();
    firstRow.setVal("100000000000");

    firstCol = objectFactory.createCTCnf();
    firstCol.setVal("001000000000");

    oddHBand = objectFactory.createCTCnf();
    oddHBand.setVal("000000100000");

    nothing = objectFactory.createCTCnf();
    nothing.setVal("000000000000");
  }

  @Override
  public List transform(AST ast) {
    List<Tr> list = traverseChildren(ast);
    ObjectFactory objectFactory = Context.getWmlObjectFactory();
    TblGrid tblGrid = objectFactory.createTblGrid();
    long avg = 9180L / list.get(0).getContent().size();
    long rest = 9180L - avg * list.get(0).getContent().size();
    for (int i = 0; i < list.get(0).getContent().size(); i++) {
      TblGridCol tblGridCol = objectFactory.createTblGridCol();
      tblGridCol.setW(BigInteger.valueOf(avg + (i == 0 ? rest : 0L)));
      tblGrid.getGridCol().add(tblGridCol);
    }
    for (int i = 0; i < list.size(); i++) {
      Tr row = list.get(i);
      if (i == 0) {
        // 第一行
        row.getTrPr()
            .getCnfStyleOrDivIdOrGridBefore()
            .add(objectFactory.createCTTrPrBaseCnfStyle(firstRow));
      } else {
        // 偶数行
        if (i % 2 != 0) {
          row.getTrPr()
              .getCnfStyleOrDivIdOrGridBefore()
              .add(objectFactory.createCTTrPrBaseCnfStyle(oddHBand));
        }
      }
      for (int j = 0; j < row.getContent().size(); j++) {
        TblWidth tblWidth = objectFactory.createTblWidth();
        tblWidth.setType(TblWidth.TYPE_DXA);
        tblWidth.setW(BigInteger.valueOf(avg + (i == 0 ? rest : 0L)));
        ((Tc) row.getContent().get(j)).getTcPr().setTcW(tblWidth);
        if (j == 0) {
          // 第一列
          ((Tc) row.getContent().get(j)).getTcPr().setCnfStyle(firstCol);
        } else {
          Tc tc = ((Tc) row.getContent().get(j));
          for (Object obj : tc.getContent()) {
            P p = (P) obj;
            p.getPPr().setCnfStyle(i == 0 ? firstRow : (i % 2 != 0) ? oddHBand : nothing);
          }
        }
      }
    }
    Tbl tbl = objectFactory.createTbl();
    tbl.setTblPr(table);
    tbl.setTblGrid(tblGrid);
    tbl.getContent().addAll(list);
    return CollectionUtil.newArrayList(tbl);
  }

  @Override
  public boolean adapt(AST ast) {
    return ast.getType().equals("table");
  }

  private static CTTblStylePr buildFirstRowPr() {
    ObjectFactory objectFactory = Context.getWmlObjectFactory();
    CTTblStylePr firstRow = objectFactory.createCTTblStylePr();
    firstRow.setType(STTblStyleOverrideType.FIRST_ROW);
    RPr rPr = objectFactory.createRPr();
    rPr.setB(objectFactory.createBooleanDefaultTrue());
    rPr.setBCs(objectFactory.createBooleanDefaultTrue());
    Color color = objectFactory.createColor();
    color.setVal("FFFFFF");
    color.setThemeColor(STThemeColor.BACKGROUND_1);
    rPr.setColor(color);
    firstRow.setRPr(rPr);
    TcPr tcPr = objectFactory.createTcPr();
    TcBorders tcBorders = objectFactory.createTcPrInnerTcBorders();
    CTBorder ctBorder = objectFactory.createCTBorder();
    ctBorder.setVal(STBorder.SINGLE);
    ctBorder.setSz(BigInteger.valueOf(4L));
    ctBorder.setSpace(BigInteger.ZERO);
    ctBorder.setColor("A5A5A5");
    ctBorder.setThemeColor(STThemeColor.ACCENT_3);
    tcBorders.setTop(ctBorder);
    tcBorders.setBottom(ctBorder);
    tcBorders.setLeft(ctBorder);
    tcBorders.setRight(ctBorder);
    CTBorder inside = objectFactory.createCTBorder();
    inside.setVal(STBorder.NIL);
    tcBorders.setInsideH(inside);
    tcBorders.setInsideV(inside);
    tcPr.setTcBorders(tcBorders);
    CTShd ctShd = objectFactory.createCTShd();
    ctShd.setVal(STShd.CLEAR);
    ctShd.setColor("auto");
    ctShd.setFill("A5A5A5");
    ctShd.setThemeFill(STThemeColor.ACCENT_3);
    tcPr.setShd(ctShd);
    firstRow.setTcPr(tcPr);
    return firstRow;
  }

  private static CTTblStylePr buildLastRowPr() {
    ObjectFactory objectFactory = Context.getWmlObjectFactory();
    CTTblStylePr lastRow = objectFactory.createCTTblStylePr();
    lastRow.setType(STTblStyleOverrideType.LAST_ROW);
    RPr rPr = objectFactory.createRPr();
    rPr.setB(objectFactory.createBooleanDefaultTrue());
    rPr.setBCs(objectFactory.createBooleanDefaultTrue());
    lastRow.setRPr(rPr);
    TcPr tcPr = objectFactory.createTcPr();
    TcBorders tcBorders = objectFactory.createTcPrInnerTcBorders();
    CTBorder ctBorder = objectFactory.createCTBorder();
    ctBorder.setVal(STBorder.DOUBLE);
    ctBorder.setSz(BigInteger.valueOf(4L));
    ctBorder.setSpace(BigInteger.ZERO);
    ctBorder.setColor("A5A5A5");
    ctBorder.setThemeColor(STThemeColor.ACCENT_3);
    tcBorders.setTop(ctBorder);
    tcPr.setTcBorders(tcBorders);
    lastRow.setTcPr(tcPr);
    return lastRow;
  }

  private static CTTblStylePr buildColPr(STTblStyleOverrideType type) {
    ObjectFactory objectFactory = Context.getWmlObjectFactory();
    CTTblStylePr col = objectFactory.createCTTblStylePr();
    col.setType(type);
    RPr rPr = objectFactory.createRPr();
    rPr.setB(objectFactory.createBooleanDefaultTrue());
    rPr.setBCs(objectFactory.createBooleanDefaultTrue());
    col.setRPr(rPr);
    return col;
  }

  private static CTTblStylePr buildBandPr(STTblStyleOverrideType type) {
    ObjectFactory objectFactory = Context.getWmlObjectFactory();
    CTTblStylePr band = objectFactory.createCTTblStylePr();
    band.setType(type);
    TcPr tcPr = objectFactory.createTcPr();
    CTShd ctShd = objectFactory.createCTShd();
    ctShd.setVal(STShd.CLEAR);
    ctShd.setColor("auto");
    ctShd.setFill("EDEDED");
    ctShd.setThemeFill(STThemeColor.ACCENT_3);
    ctShd.setThemeFillTint("33");
    tcPr.setShd(ctShd);
    band.setTcPr(tcPr);
    return band;
  }
}
