/*
 * Copyright (c) CISIAD, UNED, Spain,  2019. Licensed under the GPLv3 licence
 * Unless required by applicable law or agreed to in writing,
 * this code is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OF ANY KIND.
 */

package org.openmarkov.gui.dialog.io;

import org.apache.commons.io.FileUtils;
import org.openmarkov.core.developmentStaticAnalysis.ToCheck;
import org.openmarkov.core.exception.ParserException;
import org.openmarkov.core.exception.UnreacheableException;
import org.openmarkov.core.exception.UnrecoverableException;
import org.openmarkov.core.exception.WriterException;
import org.openmarkov.core.io.format.annotation.NoReaderForFileException;
import org.openmarkov.core.model.network.EvidenceCase;
import org.openmarkov.core.model.network.ProbNet;
import org.openmarkov.core.io.ProbNetInfo;
import org.openmarkov.core.io.ProbNetReader;
import org.openmarkov.core.io.ProbNetWriter;
import org.openmarkov.core.io.format.annotation.FormatManager;
import org.openmarkov.gui.exception.CorruptNetworkFile;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * This class contains some routines to load and to save nets.
 *
 * @author jmendoza
 * @version 1.1 - jlgozalo - Catch block deleted form OpenNetwork (not required)
 * private constructor added
 */
public class NetsIO {
    
    // private constructor for a class with only static members
    private NetsIO() {
    
    }
    
    /**
     * Opens a network saved in a file and returns the object that contains its
     * information.
     *
     * @param fileName file where the network is saved.
     *
     * @return an ProbNetInfo object with the information of the network.
     *
     * @throws Exception if the file doesn't exist or the file format isn't correct.
     */
    @ToCheck(reasonKind = {ToCheck.ReasonKind.CODE_QUALITY, ToCheck.ReasonKind.EXCEPTIONS_REWORK},
            reasonDescription = "Reading a network file should always throw the exceptions " +
                    "ParserException.BadlyStructuredFile and CorruptNetworkFile. However, these exceptions are thrown " +
                    "in 'getProbNetReader' instead of 'loadProbNetInfo', meaning someplaces read ProbNet files without " +
                    "the awareness these exceptions give." +
                    "\n" +
                    "If it this leads to a reworks of the ProbNetReader interface (where loadProbNetInfo comes from), it" +
                    " is likely we want it to receive an URL to the file instead of a String containing the filename. " +
                    "Duplicated methods should be avoided if doing this, as the current implementation duplicates some."
    )
    public static ProbNetInfo openNetworkFile(String fileName) throws IOException,
            SAXException,
            ParserException, NoReaderForFileException, CorruptNetworkFile {
        URL fileToRead = new File(fileName).toURI().toURL();
        ProbNetReader probNetReader = FormatManager.getInstance().getProbNetReader(fileToRead);
        try {
            return probNetReader.loadProbNetInfo(fileName, new FileInputStream(fileName));
        } catch (UnrecoverableException | UnreacheableException e) {
            throw e;
        } catch (RuntimeException e) {
            throw new CorruptNetworkFile(fileToRead, e);
        }
        
        /*
         * if (fileExtension.contentEquals("elv")) { //return
         * ElviraParser.getUniqueInstance().loadProbNet(fileName); } else if
         * (fileExtension.contentEquals("xml")) { /*ProbNet probNet =
         * XMLReader.getUniqueInstance().loadProbNet(fileName); if (probNet ==
         * null) { System.out.println("NetsIO.openNetworkFile from " + fileName
         * + ": probNet null"); } return probNet;
         */
        /*
         * } else if (fileExtension.contentEquals("pgmx")) { ProbNet probNet =
         * PGMXReader.getUniqueInstance().loadProbNet(fileName); if (probNet ==
         * null) { System.out.println("NetsIO.openNetworkFile from " + fileName
         * + ": probNet null"); } return probNet;
         *
         * }
         */
        
    }
    
    //	/**
    //	 * Saves a network in a file.
    //	 *
    //	 * @param network
    //	 *            - network to save in the file
    //	 * @param evidence
    //	 *            - list of evidence cases
    //	 * @param fileName
    //	 *            - file where the network is going to be saved
    //	 * @throws NotRecognisedNetworkFileExtensionException
    //	 *             - if file extension is not recognised
    //	 * @throws CanNotWriteNetworkToFileException
    //	 *             - if an I/O error has happened
    //	 */
    //	public static void saveNetworkFile(ProbNet network, List<EvidenceCase> evidence, String fileName)
    //			throws NotRecognisedNetworkFileExtensionException, CanNotWriteNetworkToFileException {
    //		String fileExtension = getFileExtension(fileName);
    //		FormatManager formatManager = FormatManager.getInstance();
    //		ProbNetWriter probNetWriter = formatManager.getProbNetWriter(fileExtension);
    //		try {
    //			probNetWriter.writeProbNet(fileName, network, evidence);
    //			/*
    //			 * if (fileExtension.contentEquals("elv")) {
    //			 * //ElviraWriter.getUniqueInstance().writeProbNet(fileName,
    //			 * network); } else if (fileExtension.contentEquals("xml")) {
    //			 * //XMLWriter.getUniqueInstance().writeProbNet(fileName, network);
    //			 * } else if (fileExtension.contentEquals("pgmx")) {
    //			 * PGMXWriter0_2.getUniqueInstance().writeProbNet(fileName, network); }
    //			 * else if (fileExtension.contentEquals("bif")) {
    //			 * //HuginWriter.getUniqueInstance().writeProbNet(fileName,
    //			 * network); } else { throw new
    //			 * NotRecognisedNetworkFileExtensionException(fileName); } } catch
    //			 * (IOException ex) { throw new
    //			 * CanNotWriteNetworkToFileException(fileName); }
    //			 */
    //		} catch (WriterException ex) {
    //			throw new CanNotWriteNetworkToFileException(fileName);
    //		}
    //	}
    
    /**
     * Saves a network in a file.
     *
     * @param network    - network to save in the file
     * @param evidence   - list of evidence cases
     * @param fileName   - file where the network is going to be saved
     * @param fileFormat - the extension and format of file where the network is going to be saved
     */
    public static void saveNetworkFile(ProbNet network, List<EvidenceCase> evidence, String fileName, String fileFormat) throws WriterException {
        String fileExtension = getFileExtension(fileName);
        FormatManager formatManager = FormatManager.getInstance();
        ProbNetWriter probNetWriter = formatManager.getProbNetWriter(fileExtension, fileFormat);
        try {
            probNetWriter.writeProbNet(fileName, network, evidence);
        } catch (WriterException.UnknownNetworkType e) {
            if (fileExtension.equals("elv")) {
                new File(fileName).delete();
                fileName = network.getNetworkType().toString().toLowerCase().replaceAll("_", " ");
            }
            throw e;
        }
    }
    
    
    //	/**
    //	 * Saves a network in a file.
    //	 *
    //	 * @param network
    //	 *            - network to save in the file
    //	 * @param fileName
    //	 *            - file where the network is going to be saved
    //	 * @throws NotRecognisedNetworkFileExtensionException
    //	 *             - if file extension is not recognised
    //	 * @throws CanNotWriteNetworkToFileException
    //	 *             - if an I/O error has happened
    //	 */
    //	public static void saveNetworkFile(ProbNet network, String fileName)
    //			throws NotRecognisedNetworkFileExtensionException, CanNotWriteNetworkToFileException {
    //
    //		saveNetworkFile(network, new ArrayList<EvidenceCase>(), fileName);
    //	}
    
    /**
     * Saves a network in a file.
     *
     * @param network  - network to save in the file
     * @param fileName - file where the network is going to be saved
     */
    public static void saveNetworkFile(ProbNet network, String fileName, String fileFormat) throws WriterException {
        saveNetworkFile(network, new ArrayList<EvidenceCase>(), fileName, fileFormat);
    }
    
    private static String getFileExtension(String fileName) {
        
        String fileExtension = null;
        int i = fileName.lastIndexOf('.');
        if ((i > 0) && (i < (fileName.length() - 1))) {
            fileExtension = fileName.substring(i + 1).toLowerCase();
        }
        
        return fileExtension;
        
    }
    
    /**
     * Opens a network from a URL.
     *
     * @param url The full url of the file to be opened file where the network
     *            is saved.
     *
     * @return an ProbNetInfo object with the information of the network.
     *
     * @throws Exception if the file doesn't exist or the file format isn't correct.
     */
    public static ProbNetInfo openNetworkURL(URL url) throws SAXException, IOException, org.openmarkov.core.exception.ParserException, NoReaderForFileException {
        String networkName = url.getPath();
        networkName = networkName.substring(networkName.lastIndexOf('/') + 1);
        
        String fileExtension = getFileExtension(networkName);
        FormatManager formatManager = FormatManager.getInstance();
        
        File file = FileUtils.toFile(url);
        ProbNetReader probNetReader = formatManager.getProbNetReader(url);
        
        ProbNetInfo probNetInfo = probNetReader.loadProbNetInfo(networkName, url.openStream());
        
        if (probNetInfo == null) {
            System.out.println("NetsIO.openNetworkFile from " + networkName + ": probNet null");
        }
        return probNetInfo;
    }
    
}
