package bugdemo;

import org.eclipse.persistence.jaxb.JAXBContext;
import org.junit.jupiter.api.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.StringWriter;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * This appears to be a bug in EclipseLink MOXy. When marshalling an org.w3c.dom.Element, it includes the namespace
 * from the element, but not from its org.w3c.dom.Attr. It's also missing the prefix, putting the attribute in the
 * default namespace (which is wrong).
 */
public class Testcase
{
	@Test
	public void demoBug() throws Exception
	{
		MyXmlAnyElementType el = new MyXmlAnyElementType();
		Document doc = newDocument();
		Element x = doc.createElementNS("http://example.com/element", "x");
		x.setAttributeNS("http://example.com/attribute", "attr", "value");
		el.someXmlBlock = x;

		StringWriter sw = new StringWriter();
		Marshaller marshaller = getMarshaller(MyXmlAnyElementType.class);
		marshaller.marshal(el, sw);

		System.out.println("Includes namespace from element, but not attribute: " + sw);

		assertTrue(sw.toString().contains("http://example.com/element"));
		assertTrue(sw.toString().contains("http://example.com/attribute"));
	}

	private Document newDocument() throws ParserConfigurationException
	{
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setNamespaceAware(true);
		factory.setExpandEntityReferences(false);
		factory.setXIncludeAware(false);
		return factory.newDocumentBuilder().newDocument();
	}


	private Marshaller getMarshaller(Class<?> clazz) throws JAXBException
	{
		javax.xml.bind.JAXBContext ctx = JAXBContext.newInstance(clazz);
		Marshaller marshaller = ctx.createMarshaller();
		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
		return marshaller;
	}
}
