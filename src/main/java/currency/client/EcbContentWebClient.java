package currency.client;

import currency.model.CurrencyResponseModel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.namespace.NamespaceContext;
import javax.xml.xpath.*;
import java.util.Iterator;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static currency.util.Messages.CONNECTION_ERROR;

@Component
public class EcbContentWebClient extends ContentWebClient<CurrencyResponseModel<?>> {

    @Value("${currency.ecb.xml}")
    private String ecbXmlFileUrl;

    @Override
    public CurrencyResponseModel<?> getContent() {
        Document currencyXml = getCurrencyXmlFormat(ecbXmlFileUrl);
        XPath xpath = XPathFactory.newInstance().newXPath();

        NamespaceContext nsContext = new NamespaceContext() {
            @Override
            public String getNamespaceURI(String prefix) {
                return prefix.equals("gesmes") ? "http://www.gesmes.org/xml/2002-08-01" : "http://www.ecb.int/vocabulary/2002-08-01/eurofxref";
            }

            @Override
            public String getPrefix(String namespaceURI) {
                return null;
            }

            @Override
            public Iterator<String> getPrefixes(String namespaceURI) {
                return null;
            }
        };

        xpath.setNamespaceContext(nsContext);
        try {
            XPathExpression expr = xpath.compile("//Cube[@currency]");
            NodeList cubes = (NodeList) expr.evaluate(currencyXml, XPathConstants.NODESET);

            Map<String, String> currencyRates = IntStream.range(0, cubes.getLength())
                    .mapToObj(i -> (Element) cubes.item(i))
                    .collect(Collectors.toMap(
                            cube -> cube.getAttribute("currency"),
                            cube -> cube.getAttribute("rate")
                    ));

            return new CurrencyResponseModel<>(currencyRates);
        } catch (XPathExpressionException e) {
            return new CurrencyResponseModel<>(CONNECTION_ERROR);
        }
    }
}
