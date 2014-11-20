package ezbake.data.common.classification;


import ezbake.base.thrift.Visibility;
import org.junit.Test;
import ezbake.classification.ClassificationConversionException;

import static org.junit.Assert.*;

public class ClassificationUtilsTests {
    @Test
    public void testCAPCOToVisibility() throws Exception {
        Visibility visibility = ClassificationUtils.getVisibilityFromCAPCO("UNCLASSIFIED//FOUO");
        assertEquals("U&FOUO", visibility.getFormalVisibility());
    }

    @Test(expected = ClassificationConversionException.class)
    public void testCAPCOVisibility() throws Exception {
        ClassificationUtils.getVisibilityFromCAPCO("INVALID");
        fail();
    }
}
