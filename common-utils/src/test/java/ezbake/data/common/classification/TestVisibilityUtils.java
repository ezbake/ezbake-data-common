package ezbake.data.common.classification;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.accumulo.core.security.VisibilityParseException;
import org.junit.Test;

import ezbake.classification.ClassificationConversionException;

import ezbake.base.thrift.DocumentClassification;

@SuppressWarnings({"unchecked", "rawtypes"})
public class TestVisibilityUtils {

    @Test
    public void generateClassificationTS() {
        final String capcoString = "TS";
        final DocumentClassification dc = new DocumentClassification();
        dc.setClassification(capcoString);

        final List expected = new ArrayList();
        final List viz1 = new ArrayList();
        viz1.add("TS");
        viz1.add("USA");

        expected.add(viz1);

        try {
            final List list = VisibilityUtils.generateVisibilityList(dc);
            // System.out.println("list : " + list);

            assertNotNull(list);
            assertTrue("Lists are not equal", Arrays.equals(expected.toArray(), list.toArray()));
        } catch (final VisibilityParseException e) {
            fail(e.getMessage());
        } catch (final ClassificationConversionException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void generateClassificationUnclassified() {
        final String capcoString = "U";
        final DocumentClassification dc = new DocumentClassification();
        dc.setClassification(capcoString);

        final List expected = new ArrayList();
        final List viz1 = new ArrayList();
        viz1.add("U");

        expected.add(viz1);

        try {
            final List list = VisibilityUtils.generateVisibilityList(dc);
            // System.out.println("list : " + list);

            assertNotNull(list);
            assertTrue("Lists are not equal", Arrays.equals(expected.toArray(), list.toArray()));
        } catch (final VisibilityParseException e) {
            fail(e.getMessage());
        } catch (final ClassificationConversionException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void generateClassificationWithOneAnd() {
        final String capcoString = "S//REL TO USA,GBR,AUS";

        final DocumentClassification dc = new DocumentClassification();
        dc.setClassification(capcoString);

        final List expected = new ArrayList();
        final List viz1 = new ArrayList();
        final List viz2 = new ArrayList();
        final List viz3 = new ArrayList();
        viz1.add("S");
        viz1.add("USA");
        viz2.add("S");
        viz2.add("AUS");
        viz3.add("S");
        viz3.add("GBR");

        expected.add(viz1);
        expected.add(viz2);
        expected.add(viz3);

        try {
            final List list = VisibilityUtils.generateVisibilityList(dc);
            // System.out.println("list : " + list);

            assertNotNull(list);
            assertTrue("Lists are not equal", Arrays.equals(expected.toArray(), list.toArray()));
        } catch (final VisibilityParseException e) {
            fail(e.getMessage());
        } catch (final ClassificationConversionException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void generateClassificationWithMultipleAnd() {
        final String capcoString = "TS//SI/TK//REL TO USA,AUS,GBR";

        final DocumentClassification dc = new DocumentClassification();
        dc.setClassification(capcoString);

        final List expected = new ArrayList();
        final List viz1 = new ArrayList();
        final List viz2 = new ArrayList();
        final List viz3 = new ArrayList();
        viz1.add("TS");
        viz1.add("SI");
        viz1.add("TK");
        viz1.add("USA");
        viz2.add("TS");
        viz2.add("SI");
        viz2.add("TK");
        viz2.add("AUS");
        viz3.add("TS");
        viz3.add("SI");
        viz3.add("TK");
        viz3.add("GBR");

        expected.add(viz1);
        expected.add(viz2);
        expected.add(viz3);

        try {
            final List list = VisibilityUtils.generateVisibilityList(dc);
            // System.out.println("list : " + list);

            assertNotNull(list);
            assertTrue("Lists are not equal", Arrays.equals(expected.toArray(), list.toArray()));
        } catch (final VisibilityParseException e) {
            fail(e.getMessage());
        } catch (final ClassificationConversionException e) {
            fail(e.getMessage());
        }
    }
}
