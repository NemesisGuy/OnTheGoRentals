package za.ac.cput.factory.impl;
/**AboutUsFactoryTest.java
 * Factory Test Class for About us
 * Author: Cwenga Dlova (214310671)
 * Date: 24/09/2023
 * */
import org.junit.jupiter.api.Test;
import za.ac.cput.domain.AboutUs;

import static org.junit.jupiter.api.Assertions.*;

class AboutUsFactoryTest {
    @Test
    void testAboutUsFactory() {

        AboutUsFactory factory = new AboutUsFactory();
        AboutUs aboutUs = factory.create();
        assertNotNull(aboutUs);
        assertNotNull(aboutUs.getId());
    }
}