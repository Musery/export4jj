package com.musery.export.transform.part;

import java.math.BigInteger;
import org.docx4j.jaxb.Context;
import org.docx4j.openpackaging.exceptions.InvalidFormatException;
import org.docx4j.openpackaging.parts.WordprocessingML.FootnotesPart;
import org.docx4j.wml.CTFootnotes;
import org.docx4j.wml.CTFtnEdn;
import org.docx4j.wml.ObjectFactory;
import org.docx4j.wml.P;
import org.docx4j.wml.R;
import org.docx4j.wml.R.ContinuationSeparator;
import org.docx4j.wml.R.Separator;
import org.docx4j.wml.STFtnEdn;
import org.jvnet.jaxb2_commons.ppp.Child;

public class CFootnote {

  public static FootnotesPart init() throws InvalidFormatException {
    ObjectFactory objectFactory = Context.getWmlObjectFactory();
    FootnotesPart footnotesPart = new FootnotesPart();
    CTFootnotes footnotes = objectFactory.createCTFootnotes();
    Separator separator = objectFactory.createRSeparator();
    footnotes.getFootnote().add(buildF(BigInteger.valueOf(-1L), STFtnEdn.SEPARATOR, separator));
    ContinuationSeparator continuationSeparator = objectFactory.createRContinuationSeparator();
    footnotes
        .getFootnote()
        .add(
            buildF(BigInteger.valueOf(0L), STFtnEdn.CONTINUATION_SEPARATOR, continuationSeparator));
    footnotesPart.setContents(footnotes);
    return footnotesPart;
  }

  private static CTFtnEdn buildF(BigInteger id, STFtnEdn type, Child child) {
    ObjectFactory objectFactory = Context.getWmlObjectFactory();
    CTFtnEdn ctFtnEdn = objectFactory.createCTFtnEdn();
    ctFtnEdn.setType(type);
    ctFtnEdn.setId(id);
    P p = objectFactory.createP();
    R r = objectFactory.createR();
    r.getContent().add(child);
    p.getContent().add(r);
    ctFtnEdn.getContent().add(p);
    return ctFtnEdn;
  }
}
