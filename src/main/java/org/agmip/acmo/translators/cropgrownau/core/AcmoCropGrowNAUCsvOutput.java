/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.agmip.acmo.translators.cropgrownau.core;

import au.com.bytecode.opencsv.CSVWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import org.agmip.acmo.util.AcmoUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author qiuxiaolei
 */
public class AcmoCropGrowNAUCsvOutput {
    
    private static final Logger log = LoggerFactory.getLogger(AcmoCropGrowNAUCsvOutput.class);
    private File outputFile;
    private String metaFilePath = null;
    
    public AcmoCropGrowNAUCsvOutput() {
    }
    
    public AcmoCropGrowNAUCsvOutput(String sourceFolder) {
        File dir = new File(sourceFolder);
        if (!dir.isDirectory()) {
            dir = dir.getParentFile();
        }
        this.metaFilePath = dir.getAbsolutePath() + File.separator + "ACMO_meta.dat";
    }
    /**
     * Generate ACMO CSV file
     *
     * @param outputCsvPath The path for output csv file
     * @param summaryListData The data holder for model output data and meta data
     */
    public void writeFile(String outputCsvPath,List metaListData,List summaryListData) throws IOException,NullPointerException,IndexOutOfBoundsException {
        MetaReader metaReader = new MetaReader(metaListData);
        OutputSummaryReader summaryReader = new OutputSummaryReader(summaryListData);
        //Create output CSV File
        outputFile = AcmoUtil.createCsvFile(outputCsvPath, "CropGrow-NAU", metaFilePath);
        CSVWriter writer = new CSVWriter(new FileWriter(outputFile), ',');	
        //write Meta Header
        writer.writeAll(metaReader.getHeader());
        for (String exname : metaReader.getExname()) {
            List<String> writeData = new ArrayList<String>();
            writeData.addAll(Arrays.asList(metaReader.getData(exname)));
            // Since QuadUI will guarantee generating the meta data until Model the column, no check is required any more
//            //If Something Wrong, Fill blank
//            while (writeData.size() < 47) {
//                writeData.add("");
//            }
//            writeData.set(44, "CropGrow-NAU");
//            //Model Version
//            writeData.set(45, summaryReader.version);
            writeData.add(summaryReader.version);
            writeData.add(summaryReader.GetSummaryData(exname, "hwah"));
            writeData.add(summaryReader.GetSummaryData(exname, "cwah"));
            writeData.add(summaryReader.GetSummaryData(exname, "adat"));
            writeData.add(summaryReader.GetSummaryData(exname, "mdat"));
            writeData.add(summaryReader.GetSummaryData(exname, "hadat"));
            writeData.add(summaryReader.GetSummaryData(exname, "laix"));
            writeData.add(summaryReader.GetSummaryData(exname, "prcp"));
            writeData.add(summaryReader.GetSummaryData(exname, "etcp"));
            writeData.add(summaryReader.GetSummaryData(exname, "nucm"));
            writeData.add(summaryReader.GetSummaryData(exname, "nlcm"));
            writer.writeNext(writeData.toArray(new String[writeData.size()]));
        }
        writer.close();
    }
    /**
     * Inner Class MetaReader, Analysis meta data
     */
    private class MetaReader {
        private List<String[]> data = new ArrayList<String[]>();
	private List<String[]> header=new ArrayList<String[]>();
	private List<String>   exname = new ArrayList<String>();
        private int exNameIdx = -1;
        private int trtNameIdx = -1;
        public MetaReader(List metaList) throws NullPointerException,IndexOutOfBoundsException{
            Iterator i = metaList.iterator();
            if(i.hasNext()){
                header.add((String[])i.next());
            }
            if(i.hasNext()){
                header.add((String[])i.next());
            }
            if(i.hasNext()){
                String[] titles = (String[])i.next();
                setIndex(titles);
                header.add(titles);
            }
            while (i.hasNext()){
                String[] theData = (String[])i.next();
                if(theData.length>0 && theData[0].startsWith("*")){
                    data.add(theData);
                }
            }
            //entry[2] EXNAME, entry[7] TRTNAME
            for(String[] entry:data){
		if(entry[trtNameIdx].trim().equals("")) {
                    exname.add(entry[exNameIdx]);
                } else {
                    exname.add(entry[exNameIdx]+"-"+entry[trtNameIdx]);
                }
		}
        }
        private void setIndex(String[] titles) {
            for (int i = 0; i < titles.length; i++) {
                if (exNameIdx >= 0 && trtNameIdx >= 0) {
                    return;
                } else if (titles[i].toUpperCase().equals("EXNAME")) {
                    exNameIdx = i;
                } else if (titles[i].toUpperCase().equals("TRT_NAME")) {
                    trtNameIdx = i;
                }
            }
            exNameIdx = 2;  // For template version 4.0.1
            trtNameIdx = 7; // For template version 4.0.1
        }
        
        public List<String[]> getHeader(){
            return header;
	}
	public String[] getData(String exp) throws NullPointerException,IndexOutOfBoundsException{
            // There needs to be a blank array or something returned here - CV
            int line = exname.indexOf(exp);
            if (line == -1) {
		log.error("MetaReader, Entry {} not found", exp);
		String [] blank = {};
		return blank;
            } else {
		return data.get(line);
            }
	}
	public List<String> getExname(){
            return exname;
	}
    }
    /**
     * Inner Class OutFileReader, Analysis OUTPUT data
     */
    private class OutputSummaryReader {
	private String version;
	private String title;
	private List<String[]> data = new ArrayList<String[]>();
        private HashMap dataTitleMap = new HashMap();
        private List<String> exname = new ArrayList<String>();
	public OutputSummaryReader(List summaryList) throws NullPointerException,IndexOutOfBoundsException{
            Iterator itr = summaryList.iterator();
            while (itr.hasNext()) {
                String[] theData = (String[])itr.next();
                if(theData.length>0){
                    if(theData[0].startsWith("*")){
                        data.add(theData);
                        //theData[2] EXNAME
                        exname.add(theData[2]);
                    }
                    //First Line, the abstract information about this file
                    else if(theData[0].startsWith("&")){
                        version = theData[1];
                        title = theData[2];
                    }
                    //put summary data variable names into HashMap
                    else if(theData[0].startsWith("#")){
                        for(int j=0;j<theData.length;j++){
                            dataTitleMap.put(theData[j].toUpperCase(),j);
                        }
                    }
                }
            }
	}
       public String GetSummaryData(String exName,String columnName) throws NullPointerException,IndexOutOfBoundsException{
           int line = exname.indexOf(exName);
            if (line == -1) {
		log.error(" OutputSummary.CSV, Entry {} not found", exName);
		return "";
            } else {
               int columnIndex = (Integer)dataTitleMap.get(columnName.toUpperCase());
		return data.get(line)[columnIndex];
            }
       }
    }
    
    /**
     * Get output file object
     */
    public File getOutputFile() {
        return outputFile;
    }
}
