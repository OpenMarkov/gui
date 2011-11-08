/**
 * 
 */
package openmarkov.core.gui.menutoolbar.tool;


import java.io.FileInputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;


/**
 * @author jlgozalo
 * @version 1.0 30/11/2008
 */
public class ValidateXMLOpenMarkovConfigurationDTD {

	public static int contFatalErrors = 0;
	public static int contErrors = 0;
	public static int contWarnings = 0;

	public static void main(String args[]) {

		try {

			DocumentBuilderFactory factory =
				DocumentBuilderFactory.newInstance();
			factory.setValidating(true);
			DocumentBuilder builder = factory.newDocumentBuilder();
			builder.setErrorHandler(new org.xml.sax.ErrorHandler() {

				// To handle Fatal Errors
				public void fatalError(SAXParseException exception)
								throws SAXException {

					System.out.println("Line: " + exception.getLineNumber()
						+ "\nFatal Error: " + exception.getMessage());
					contFatalErrors++;
				}

				// To handle Errors
				public void error(SAXParseException e) throws SAXParseException {

					System.out.println("Line: " + e.getLineNumber()
						+ "\nError: " + e.getMessage());
					contErrors++;
				}

				// To Handle warnings
				public void warning(SAXParseException err)
								throws SAXParseException {

					System.out.println("Line: " + err.getLineNumber()
						+ "\nWarning: " + err.getMessage());
					contWarnings++;
				}
			});
			Document xmlDocument =
				builder.parse(new FileInputStream("CarmenMenu.xml"));
			DOMSource source = new DOMSource(xmlDocument);
			StreamResult result = new StreamResult(System.out);
			TransformerFactory tf = TransformerFactory.newInstance();
			Transformer transformer = tf.newTransformer();
			transformer.setOutputProperty(
				OutputKeys.DOCTYPE_SYSTEM, "CarmenDTDSpec.dtd");
			if (contFatalErrors == 0 && contErrors == 0 && contWarnings == 0) {
				System.out.println("Configuration file is OK");
			} else {
				transformer.transform(source, result);
				System.out.println(">>>FatalErrors: " + contFatalErrors);
				System.out.println(">>>Errors: " + contErrors);
				System.out.println(">>>Warnings: " + contWarnings);

			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}
}