/*
 * Copyright (c) CISIAD, UNED, Spain,  2018. Licensed under the GPLv3 licence
 * Unless required by applicable law or agreed to in writing,
 * this code is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OF ANY KIND.
 */

package org.openmarkov.gui.localize;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.jdom2.located.LocatedJDOMFactory;
import org.junit.jupiter.api.*;
import org.openmarkov.core.test.TestSpeed;
import org.openmarkov.gui.localize.spi.LocalizeResourcesProvider;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@TestInstance(TestInstance.Lifecycle.PER_METHOD)
public class LocalizeXMLCompare {
    private List<String> englishFiles;
    private List<String> spanishFiles;
    private LocalizeResourcesProvider resourceBundleProvider;
    
    
    private static final int LANGUAGE_CODE_PLUS_EXTENSION_LENGHT = 7;
    
    @BeforeEach public void setUp() {
        this.englishFiles = new ArrayList<>();
        this.spanishFiles = new ArrayList<>();
        
        this.resourceBundleProvider = new GUIResourceBundleProvider();
        var localizeDir = this.resourceBundleProvider.getClass().getResource(
                this.resourceBundleProvider.getRootOfResources() + "/localize");
        
        File[] localizeDirFiles = new File(localizeDir.getFile().substring(1)).listFiles();
        var files = Arrays.stream(localizeDirFiles).map(File::getName);
        
        for (String file : files.toList()) {
            boolean isXMLFile = file.toLowerCase().endsWith("xml");
            if (!isXMLFile)
                continue;
            String fileWithoutLang = file.substring(0, file.length() - LocalizeXMLCompare.LANGUAGE_CODE_PLUS_EXTENSION_LENGHT);
            this.englishFiles.add(fileWithoutLang + "_en.xml");
            this.spanishFiles.add(fileWithoutLang + "_es.xml");
        }
    }
    
    @Test public void checkFilesNamesAndNumber() {
        try {
            checkSameFiles();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            Assertions.assertTrue(false);
        }
    }
    
    public void checkSameFiles() throws Exception {
        if (englishFiles.size() != spanishFiles.size()) {
            throw new Exception("There is not the same number of files for both languages.");
        }
        //Assertions.assertEquals(englishFiles.size(), spanishFiles.size());
        int languageCodePlusExtensionLenght = 7;
        for (int i = 0; i < englishFiles.size(); i++) {
            String englishFileWithoutLang = englishFiles.get(i)
                                                        .substring(0, englishFiles.get(i)
                                                                                  .length() - languageCodePlusExtensionLenght);
            String spanishFileWithoutLang = spanishFiles.get(i)
                                                        .substring(0, spanishFiles.get(i)
                                                                                  .length() - languageCodePlusExtensionLenght);
            if (!englishFileWithoutLang.equals(spanishFileWithoutLang)) {
                throw new Exception("The setup of this test class is not well defined.");
            }
        }
    }
    
    @Tag(TestSpeed.SLOW)
    @Test public void checkSameStructure() {
        for (int i = 0; i < englishFiles.size(); i++) {
            String englishXML = englishFiles.get(i);
            String spanishXML = spanishFiles.get(i);
            try {
                checkStructure(englishXML, spanishXML);
            } catch (Exception e) {
                System.out.println(
                        "There is a difference between the XMLs '" + englishXML + "' and '" + spanishXML + "'." + System.lineSeparator());
                e.printStackTrace();
                Assertions.assertTrue(false);
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
            throw new Exception(
                    "There is at least one difference in one of the childrens of the label '" + rootEn.getName() + "'"
                            + e.getMessage());
        }
        
    }
    
    private void checkElements(Element rootEn, Element rootEs) throws Exception {
        
        if (!rootEn.getName().equals(rootEs.getName())) {
            throw new Exception(
                    ". The XML labels are not the same, as '" + rootEn.getName() + "' != '" + rootEs.getName() + "'.");
        }
        
        if (rootEn.getChildren().size() != rootEs.getChildren().size()) {
            throw new Exception(
                    ". The XML label '" + rootEn.getName() + "' have a different number of children " + rootEn
                            .getChildren().size() + " != " + rootEs.getChildren().size());
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
        InputStream stream = this.resourceBundleProvider.getClass().getResourceAsStream(
                this.resourceBundleProvider.getRootOfResources() + "/localize/" + xmlDocument);
        
        // Get root element.
        SAXBuilder builder = new SAXBuilder();
        builder.setJDOMFactory(new LocatedJDOMFactory());
        Document document = null;
        try {
            document = builder.build(stream);
        } catch (JDOMException | IOException e) {
            e.getStackTrace();
        }
        
        return document;
    }
}
