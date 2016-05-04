package tmcsim.client.cadclientgui;

import java.io.File;
import java.io.IOException;
import java.rmi.RemoteException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.SAXException;

import tmcsim.client.cadclientgui.data.CardfileData;

public class CardfileReader {

    public CardfileReader(String filePath, CardfileData cfd)
            throws RemoteException {
        CardfileHandler ch = new CardfileHandler(cfd);
        try {
            SAXParserFactory.newInstance().newSAXParser()
                    .parse(new File(filePath), ch);
        } catch (SAXException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (ParserConfigurationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }
}
