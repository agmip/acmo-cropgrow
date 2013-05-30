/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor
 */
package org.agmip.acmo.translators.cropgrownau;
import java.io.File;
import java.util.List;
import org.agmip.acmo.translators.AcmoTranslator;
import org.agmip.acmo.translators.cropgrownau.core.AcmoCropGrowNAUCsvOutput;
import org.agmip.acmo.translators.cropgrownau.core.AcmoCropGrowNAUOutputFileInput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * ACMO CSV translator for WheatGrow output data
 *
 * @version 1.0.0
 */
public class CropGrowNAUAcmo implements AcmoTranslator {
    private static final Logger LOG = LoggerFactory.getLogger(CropGrowNAUAcmo.class);
    /**
     * Output ACMO csv File with WheatGrow output data
     *
     * @param sourceFolder
     * @param destFolder
     * @return The output file
     */
    @Override
    public File execute(String sourceFolder, String destFolder) {
        try {
            // Read WheatGrow and RiceGrow output data
            AcmoCropGrowNAUOutputFileInput cropgrowReader = new AcmoCropGrowNAUOutputFileInput();
            //HashMap about acmometa and outputsummary
            List metaList = cropgrowReader.readMeta(sourceFolder);
            List summaryList = cropgrowReader.readSummary(sourceFolder);
            // Output CSV File
            AcmoCropGrowNAUCsvOutput csvWriter = new AcmoCropGrowNAUCsvOutput(sourceFolder);
            csvWriter.writeFile(destFolder, metaList,summaryList);
            return csvWriter.getOutputFile();
        }catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}