package tmcsim.client.cadclientgui;

import java.rmi.RemoteException;

import tmcsim.client.cadclientgui.data.CADData;
import tmcsim.client.cadclientgui.data.CardfileData;
import tmcsim.client.cadclientgui.screens.Login;
import tmcsim.client.cadclientgui.screens.ScreenManager;
import tmcsim.client.CADClient;

/**
 * Main class to run CADClient.
 * @author Nicholas
 *
 */


public class CADClientGUI {

    private CADData cadData;
    private CardfileData cardfileData;
    public static ScreenManager screen;
    public static Login login;
    public static CADClient client;
    
    public CADClientGUI(){
        //cadData = new CADData();
        cardfileData = new CardfileData();
    }
    
    public CADData getCadData(){
        return this.cadData;
    }
    
    public void setCadData(CADData cadData){
        this.cadData = cadData;
    }
    
    public CardfileData getCardfileData(){
        return this.cardfileData;
    }
    
    public void setCardfileData(CardfileData cardfileData){
        this.cardfileData = cardfileData;
    }

    
}
