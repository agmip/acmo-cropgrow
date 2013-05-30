package org.agmip.translators.acmo.cropgrow;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import org.agmip.acmo.translators.cropgrownau.CropGrowNAUAcmo;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

/**
 * Unit test for simple App.
 */
/**
 *
 * @author Meng Zhang
 */
public class CropGrowAcmoTest {

    CropGrowNAUAcmo runner;
    URL resource;

    @Before
    public void setUp() throws Exception {
        runner = new CropGrowNAUAcmo();
        resource = this.getClass().getClassLoader().getResource("ACMO_meta.dat");
    }

    @Test
    public void test() throws IOException, Exception {
        File file = runner.execute(new File(resource.getPath()).getParent(), "");
        if (file != null) {
            assertTrue(file.exists());
            assertTrue(file.getName().matches("ACMO-MACHAKOS-1-0XFX-0-0-CropGrow-NAU( \\(\\d\\))*.csv"));
            assertTrue(file.delete());
        }
    }
}