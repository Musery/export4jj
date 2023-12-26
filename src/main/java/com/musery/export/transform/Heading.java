package com.musery.export.transform;

import cn.hutool.core.collection.CollectionUtil;
import com.musery.export.ExportOption;
import com.musery.export.transform.part.CStyle;
import com.musery.parse.AST;
import org.docx4j.jaxb.Context;
import org.docx4j.wml.*;
import org.docx4j.wml.PPrBase.OutlineLvl;
import org.docx4j.wml.PPrBase.PStyle;
import org.docx4j.wml.PPrBase.Spacing;

import java.math.BigInteger;
import java.util.List;

public class Heading implements DOCX4TR {

    private static final PPr[] heading = new PPr[3];

    static {
        ObjectFactory objectFactory = Context.getWmlObjectFactory();
        for (int i = 0; i < 3; i++) {
            PPr pPr = objectFactory.createPPr();
            pPr.setKeepNext(objectFactory.createBooleanDefaultTrue());
            pPr.setKeepLines(objectFactory.createBooleanDefaultTrue());
            Spacing spacing = objectFactory.createPPrBaseSpacing();
            spacing.setBefore(BigInteger.valueOf(330L - i * 70L));
            spacing.setAfter(BigInteger.valueOf(330L - i * 70L));
            spacing.setLine(BigInteger.valueOf(600L - i * 150L));
            spacing.setLineRule(STLineSpacingRule.EXACT);
            OutlineLvl outlineLvl = objectFactory.createPPrBaseOutlineLvl();
            outlineLvl.setVal(BigInteger.valueOf(i));
            pPr.setOutlineLvl(outlineLvl);
            pPr.setSpacing(spacing);

            RPr rPr = objectFactory.createRPr();
            rPr.setB(objectFactory.createBooleanDefaultTrue());
            rPr.setBCs(objectFactory.createBooleanDefaultTrue());
            HpsMeasure hpsMeasure = objectFactory.createHpsMeasure();
            hpsMeasure.setVal(BigInteger.valueOf(44L - i * 12L));
            rPr.setKern(hpsMeasure);
            rPr.setSz(hpsMeasure);
            rPr.setSzCs(hpsMeasure);
            CStyle.customPStyle("IHeading" + i, "IHeading " + i, pPr, rPr);

            heading[i] = objectFactory.createPPr();
            PStyle pStyle = objectFactory.createPPrBasePStyle();
            pStyle.setVal("IHeading" + i);
            heading[i].setPStyle(pStyle);
        }
    }

    @Override
    public List transform(AST ast) {
        ObjectFactory objectFactory = Context.getWmlObjectFactory();
        P p = objectFactory.createP();
        int level = (ast.getDepth() - 1);
        if (level == 0) {
            // 记录当前子节点所在章节
            int part = (Integer) ExportOption.getThreadLocal().get().getExtend().getOrDefault("CUR_PART", 0);
            ExportOption.getThreadLocal().get().getExtend().put("CUR_PART", part + 1);
            ExportOption.getThreadLocal().get().getExtend().put("CUR_CHILD_PART", 1);
        }
        p.setPPr(heading[level]);
        p.getContent().addAll(traverseChildren(ast));
        return CollectionUtil.newArrayList(p);
    }

    @Override
    public boolean adapt(AST ast) {
        return ast.getType().equals("heading");
    }
}
