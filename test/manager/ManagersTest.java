package manager;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ManagersTest {

    @Test
    void getDefaultShouldReturnNotNull() {
       assertNotNull(Managers.getDefault());
    }

    @Test
    void getDefaultHistoryShouldReturnNotNull() {
        assertNotNull(Managers.getDefaultHistory());
    }
}