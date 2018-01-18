package org.openmarkov.gui.localize;

import org.apache.commons.io.Charsets;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.jdom2.located.LocatedJDOMFactory;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.*;
import java.util.*;

public class LocalizeXMLCompare {
    private List<String> englishFiles;
    private List<String> spanishFiles;

    @Before
    public void setUp(){
        try {
            englishFiles = new ArrayList<>();
            spanishFiles = new ArrayList<>();
            List<String> files = IOUtils.readLines(this.getClass().getClassLoader().getResourceAsStream("localize/"), Charsets.UTF_8);

            Set<String> mainNames = new HashSet<>();
            int languageCodePlusExtensionLenght = 7;
            for (String file : files) {
                // We only get the XML localization files
                if (file.substring(file.length()-3).equalsIgnoreCase("XML")) {
                    String fileWithoutLang = file.substring(0,file.length()-languageCodePlusExtensionLenght);
                    mainNames.add(fileWithoutLang);
                } else {
                    continue;
                }
            }

            Iterator<String> iter = mainNames.iterator();
            while(iter.hasNext()) {
                String fileName = iter.next();
                englishFiles.add(fileName + "_en.xml");
                spanishFiles.add(fileName + "_es.xml");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void checkFilesNamesAndNumber(){
        try {
            checkSameFiles();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            Assert.assertTrue(false);

        }
    }


    public void checkSameFiles() throws Exception {
        if (englishFiles.size() != spanishFiles.size()) {
            throw new Exception("There is not the same number of files for both languages.");
        }
        //Assert.assertEquals(englishFiles.size(), spanishFiles.size());
        int languageCodePlusExtensionLenght = 7;
        for (int i = 0; i < englishFiles.size(); i++) {
            String englishFileWithoutLang = englishFiles.get(i).substring(0,englishFiles.get(i).length()-languageCodePlusExtensionLenght);
            String spanishFileWithoutLang = spanishFiles.get(i).substring(0,spanishFiles.get(i).length()-languageCodePlusExtensionLenght);
            if (!englishFileWithoutLang.equals(spanishFileWithoutLang)) {
                throw new Exception("The setup of this test class is not well defined.");
            }
        }
    }

    @Test
    public void checkSameStructure(){

        for (int i = 0; i < englishFiles.size(); i++) {
            String englishXML = englishFiles.get(i);
            String spanishXML = spanishFiles.get(i);

            try {
                checkStructure(englishXML, spanishXML);
            } catch (Exception e) {
                System.out.println("There is a difference between the XMLs '" + englishXML + "' and '" + spanishXML + "'." + e.getMessage());
                Assert.assertTrue(false);
            }
        }



    }

    private void checkStructure(String englishXML, String spanishXML) throws Exception {

        Document englishXMLDocument = getXMLDocument(englishXML);
        Document spanishXMLDocument = getXMLDocument(spanishXML);

        Element rootEn = englishXMLDocument.getRootElement();
        Element rootEs = spanishXMLDocument.getRootElement();

        try {
            checkElements(rootEn, rootEs);
        } catch (Exception e) {
            throw new Exception("There is at least one difference in one of the childrens of the label '" + rootEn.getName() + "'" + e.getMessage());
        }


    }

    private void checkElements(Element rootEn, Element rootEs) throws Exception {

        if(!rootEn.getName().equals(rootEs.getName())) {
            throw new Exception(". The XML labels are not the same, as '" + rootEn.getName() + "' != '" + rootEs.getName() + "'.");
        }

        if(rootEn.getChildren().size() != rootEs.getChildren().size()) {
            throw new Exception(". The XML label '" + rootEn.getName() + "' have a different number of children " + rootEn.getChildren().size() + " != " + rootEs.getChildren().size());
        }


        for (int i = 0; i < rootEn.getChildren().size(); i++) {
            Element childEN = rootEn.getChildren().get(i);
            Element childES = rootEs.getChildren().get(i);
            try {
                checkElements(childEN, childES);
            } catch (Exception e) {
                throw new Exception(" -> '" + childEN.getName() + "'" + e.getMessage());
            }
        }
    }

    private Document getXMLDocument(String xmlDocument) {
        // Get file if not included.
        InputStream stream = this.getClass().getClassLoader().getResourceAsStream("localize/"+xmlDocument);;

        // Get root element.
        SAXBuilder builder = new SAXBuilder();
        builder.setJDOMFactory( new LocatedJDOMFactory() );
        Document document = null;
        try {
            document = builder.build( stream );
        } catch ( JDOMException | IOException e ) {
            e.getStackTrace();
        }

        return document;
    }
}
