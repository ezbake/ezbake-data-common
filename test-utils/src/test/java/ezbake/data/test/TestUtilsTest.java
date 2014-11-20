package ezbake.data.test;

import static org.junit.Assert.assertNotNull;

import static ezbake.data.test.TestUtils.MOCK_APP_SEC_ID;

import java.util.Set;

import org.junit.Test;

import com.google.common.collect.Sets;

/**
 *
 * @author blong
 */
public class TestUtilsTest {
    /**
     * Test of createTSUser method, of class TestUtils.
     */
    @Test
    public void testCreateTSUser() {
        assertNotNull(TestUtils.createTSUser());
    }

    /**
     * Test of createTS_S_B_User method, of class TestUtils.
     */
    @Test
    public void testCreateTS_S_B_User() {
        assertNotNull(TestUtils.createTS_S_B_User());
    }

    /**
     * Test of createTestToken method, of class TestUtils.
     */
    @Test
    public void testCreateTestToken() {
        final String sid = "SOME_TEST_SID";
        final Set<String> auths = Sets.newHashSet("SOME_CLEARANCE", "S", "D");
        assertNotNull(TestUtils.createTestToken(sid, auths, MOCK_APP_SEC_ID));
    }
}
