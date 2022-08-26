import com.c3po.core.AccessControlList;
import com.c3po.core.AccessControlListMode;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class AccessControlListTest {
    @Test
    public void testAllowed() {
        AccessControlList<Integer> acl = new AccessControlList<>();
        acl.allow(1, 2);

        assertTrue(acl.isAllowed(1));
        assertTrue(acl.isAllowed(2));
        assertFalse(acl.isAllowed(3));
    }

    @Test
    public void testDenied() {
        AccessControlList<Integer> acl = new AccessControlList<>();
        acl.deny(1, 2);

        assertFalse(acl.isAllowed(1));
        assertFalse(acl.isAllowed(2));
        assertFalse(acl.isAllowed(3));
    }

    @Test
    public void testDeniedModeAllowUnless() {
        AccessControlList<Integer> acl = new AccessControlList<>(AccessControlListMode.ALLOW_UNLESS_DENIED);
        acl.allow(1, 2);
        acl.deny(4);

        assertTrue(acl.isAllowed(1));
        assertTrue(acl.isAllowed(2));
        assertTrue(acl.isAllowed(3));
        assertFalse(acl.isAllowed(4));
    }
}
