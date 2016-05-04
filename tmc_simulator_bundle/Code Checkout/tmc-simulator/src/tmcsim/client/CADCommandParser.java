package tmcsim.client;

import java.util.NoSuchElementException;
import java.util.StringTokenizer;
import java.util.Vector;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import tmcsim.common.CADProtocol.CAD_COMMANDS;
import tmcsim.common.CADProtocol.CAD_FIELD_CODES;
import tmcsim.common.CADProtocol.DATA_TAGS;


/**
 * CADCommandParser is used to parse user input from the CAD command line.
 * The text is parsed for specific CAD command tokens and Field Codes
 * as specified by the offical CAD syntax.  The parseCommand() method
 * is used to parse a command line into the XML command Element that
 * will be sent to the CAD Simulator.  
 * TODO Where is this specified??? 
 * 
 * @author Matthew Cechini
 * @version
 */
public class CADCommandParser {
    
    /**
     * Inner class used in parsing CAD Command Lines.
     * @author Matthew
     */
    private class CommandItem {
        
        /** Field Code Mnemonic. */
        public String mnemonic;

        /** Associated data. */
        public String value;        
        
        public CommandItem(String m, String v) {
            mnemonic = m;
            value    = v;
        }
    }
   
    /**
     * This method parses the parameter command line for CAD commands.  The
     * command text is tokenized by the '.' character, which is a delimiter
     * for CAD commands.  The first parsed token specifies which CAD
     * command is being performed.  If the token matches a valid CAD command,
     * the remaining command line is parsed for other '.' delimeted information 
     * that accompanies that command.  <br>  
     * As the command line is parsed, XML Elements are added to the root
     * parameter Element to create a command message that will be sent to 
     * the CAD Simulator.  The individual XML schemas are viewable ????
     * If the command line is not properly formed, an Exception is thrown. 
     * CAD Command syntax is viewable ????
     * TODO Where are they available???  
     * 
     * 
     * The following table specifies the valid CAD command tokens, their 
     * associated CAD Command, the additional information, and error states.
     * 
     *<table cellpadding="2" cellspacing="2" border="1"
     * style="text-align: left; width: 250px;">
     *  <tbody>
     *    <tr>
     *      <th>CAD Command Token</th>
     *      <th>CAD Command</th>
     *      <th>Additional Information</th>
     *      <th>Possible Errors</th>
     *    </tr>
     *    <tr>
     *      <td>IB</td>
     *      <td>Incident Board</td>
     *      <td>None</td>
     *      <td>None</td>
     *    </tr>
     *    <tr>
     *      <td>II</td>
     *      <td>Incident Inquiry</td>
     *      <td>Numerical log number</td>
     *      <td>Log number contains invalid characters.</td>
     *    </tr>
     *    <tr>
     *      <td>SA</td>
     *      <td>Incident Summary By Area</td>
     *      <td>None</td>
     *      <td>None</td>
     *    </tr>
     *    <tr>
     *      <td>UI</td>
     *      <td>Incident Update</td>
     *      <td>1) Numerical log number (optional)<br>
     *          2) CAD Field Codes with associated data </td>
     *      <td>Missing Field Codes.</td>
     *    </tr>
     *    <tr>
     *      <td>TO</td>
     *      <td>Routed Mssage</td>
     *      <td>1) Recipient CAD Position Numbers or Mnemonics<br>
     *          2) Message text </td>
     *      <td>1) Missing Recipients <br>
     *          2) Missing Message Field Code</td>
     *    </tr>
     *    <tr>
     *      <td>OF</td>
     *      <td>Log Terminal Off</td>
     *      <td>None</td>
     *      <td>None</td>
     *    </tr>
     *  </tbody>
     *</table>
     * 
     * 
     * @param currElem Document Element to add command tags to.
     * @param commandText Command line text to parse.
     * @throws Exception 
     */
    public void parseCommand(Element currElem, String commandText) throws Exception {
                        
        StringTokenizer tokenizer = new StringTokenizer(commandText, ".");   
        String          token;
        
        Document theDoc  = currElem.getOwnerDocument();
        Element  cmdElem = null;
                      
        if(tokenizer.countTokens() >= 1 && commandText.indexOf(".") != -1) {
            token = tokenizer.nextToken(); 
                  
            //INCIDENT_BOARD Request
            if(token.equals(CAD_COMMANDS.INCIDENT_BOARD.mnemonic)) {
                cmdElem = theDoc.createElement(CAD_COMMANDS.INCIDENT_BOARD.fullName);       
                currElem.appendChild(cmdElem);
            }
            //INCIDENT_INQUIRY Request
            else if(token.equals(CAD_COMMANDS.INCIDENT_INQUIRY.mnemonic)) {  
                try {
                    Integer logNumber = Integer.valueOf(tokenizer.nextToken().trim());
                    
                    cmdElem = theDoc.createElement(CAD_COMMANDS.INCIDENT_INQUIRY.fullName);
                    cmdElem.appendChild(theDoc.createTextNode(logNumber.toString()));       
                    currElem.appendChild(cmdElem);
                } catch (Exception e) {
                    //catch NumberFormatException
                    throw new Exception();
                }
            }
            //INCIDENT_SUMMARY Request
            else if(token.equals(CAD_COMMANDS.INCIDENT_SUMMARY.mnemonic)) {   
                cmdElem = theDoc.createElement(CAD_COMMANDS.INCIDENT_SUMMARY.fullName);           
                currElem.appendChild(cmdElem);                  
            }                 
            //INCIDENT_UPDATE Request      
            else if(token.equals(CAD_COMMANDS.INCIDENT_UPDATE.mnemonic)) {      
                                     
                Vector<CommandItem> commands = new Vector<CommandItem>();
                
                //ascertain if the next token is a number, if so, we're updating a 
                //specific log, else updating the current log(assuming we're looking at one)

                cmdElem = theDoc.createElement(CAD_COMMANDS.INCIDENT_UPDATE.fullName);           
                currElem.appendChild(cmdElem);                  
                
                try {
                    token = tokenizer.nextToken().trim();
                    Integer logNumber = new Integer(token);
                    cmdElem.setAttribute(DATA_TAGS.LOG_NUM.tag, logNumber.toString());
                }                   
                //"UI." missing information
                catch (NoSuchElementException nsee) {throw new Exception();}  
                //UI.xxx where xxx not a number, assume UI.D/ format
                catch (NumberFormatException nfe) {
                    commands.add(new CommandItem(token.substring(0,2), token.substring(2,token.length())));
                }                       
                
                while(tokenizer.hasMoreTokens()) {
                    token = tokenizer.nextToken().trim();       
                    commands.add(new CommandItem(token.substring(0,2), token.substring(2,token.length()))); 
                }
                
                parseIncidentUpdate(cmdElem, commands);                     
       
            }
            //ROUTED_MESSAGE Request
            else if(token.equals(CAD_COMMANDS.ROUTED_MESSAGE.mnemonic)) {
                String  message       = "";
                String  destPositions = "";

                //comma delimited
                destPositions = tokenizer.nextToken();
                                
                //in case there is a . in the following text
                while(tokenizer.hasMoreTokens())
                    message += tokenizer.nextToken();
                    
                    
                if(message.substring(0,2).equalsIgnoreCase(CAD_FIELD_CODES.MESSAGE.mnemonic)) {
                    cmdElem = theDoc.createElement(CAD_COMMANDS.ROUTED_MESSAGE.fullName);           
                    currElem.appendChild(cmdElem);                  

                    Element destElem = theDoc.createElement(DATA_TAGS.DESTINATION.tag);
                    destElem.appendChild(theDoc.createTextNode(destPositions));
                    cmdElem.appendChild(destElem);                  

                    Element msgElem = theDoc.createElement(DATA_TAGS.MESSAGE.tag);
                    msgElem.appendChild(theDoc.createTextNode(message.substring(2, message.length())));
                    cmdElem.appendChild(msgElem);
                }
                else throw new Exception();                     
            } 
            //ENTER_INCIDENT Request
            else if(token.equals(CAD_COMMANDS.ENTER_INCIDENT.mnemonic)) {
                //TODO EI Implementation??
                /*
                Vector<CommandItem> commands = new Vector<CommandItem>();
                
                cmdElem = theDoc.createElement(CAD_COMMANDS.ENTER_INCIDENT.fullName);           
                currElem.appendChild(cmdElem);                      
                
                while(tokenizer.hasMoreTokens()) {
                    token = tokenizer.nextToken().trim();       
                    commands.add(new CommandItem(token.substring(0,2), token.substring(2,token.length()))); 
                }
                
                parseIncidentUpdate(cmdElem, commands);
                */
                
            }
            //TERMINAL_OFF Request
            else if(token.equals(CAD_COMMANDS.TERMINAL_OFF.mnemonic)) {
                cmdElem = theDoc.createElement(CAD_COMMANDS.TERMINAL_OFF.fullName);       
                currElem.appendChild(cmdElem);  
            }
            //APP_CLOSE Request
            else if(token.equals(CAD_COMMANDS.APP_CLOSE.mnemonic)) {
                cmdElem = theDoc.createElement(CAD_COMMANDS.APP_CLOSE.fullName);       
                currElem.appendChild(cmdElem);  
            }
            else throw new Exception();
        }
        else {
            
            try {
                Integer cadLog = Integer.parseInt(commandText.trim());
                
                cmdElem = theDoc.createElement(CAD_COMMANDS.INCIDENT_INQUIRY.fullName);
                cmdElem.appendChild(theDoc.createTextNode(cadLog.toString()));       
                currElem.appendChild(cmdElem);
                
            } catch (Exception e) {
                throw new Exception();
            }
        }       
    }   


    /**
     * This method iterates through the parameter Vector of CommandItem objects.
     * For each object, the appropriate Incident information is extracted 
     * and added as a tag to the parameter XML Element.
     * 
     * @param currElem XML Element to append Incident information tags to.
     * @param commands Vector of CommandItems to parse for Incident information.
     */
    public void parseIncidentUpdate(Element currElem, Vector<CommandItem> commands) {
        
        String witnessName  = null;
        String witnessAddr  = null;
        String witnessPhone = null;
        
        Document theDoc = currElem.getOwnerDocument();
        
        for(CommandItem item : commands) {
                                            
            //Address of Witness
            if(item.mnemonic.equals(CAD_FIELD_CODES.WITNESS_ADDRESS.mnemonic)) {
                //System.out.print("A");        
                witnessAddr = item.value;               
            }               
            //Beat
            else if(item.mnemonic.equals(CAD_FIELD_CODES.BEAT.mnemonic)) {              
                //System.out.print("B");            
                Element beatElem = theDoc.createElement(CAD_FIELD_CODES.BEAT.fullName);
                beatElem.appendChild(theDoc.createTextNode(item.value));
                currElem.appendChild(beatElem);
            }
            //Callbox
            else if(item.mnemonic.equals(CAD_FIELD_CODES.CALLBOX.mnemonic)) {
                //System.out.print("C");    
                //xmlOut.writeTag(CALLBOX, commandMap.get(key));
            }
            //Details
            else if(item.mnemonic.equals(CAD_FIELD_CODES.DETAILS.mnemonic)) {
                //System.out.print("D");
                Element detailElem = theDoc.createElement(CAD_FIELD_CODES.DETAILS.fullName);
                
                //if the details contains a '|' character, the text is sensitive.  Remove
                //the pipe and mark the detail as sensitive.
                if(item.value.contains("|")) {
                    StringBuffer detailBuf = new StringBuffer();
                    detailBuf.append(item.value.substring(0, item.value.indexOf("|")));
                    detailBuf.append(item.value.substring(item.value.indexOf("|") + 1, 
                            item.value.length()));
                    
                    detailElem.setAttribute(DATA_TAGS.SENSITIVE.tag, 
                            new Boolean(true).toString());
                    detailElem.appendChild(theDoc.createTextNode(detailBuf.toString()));
                }
                else {
                    detailElem.setAttribute(DATA_TAGS.SENSITIVE.tag, 
                            new Boolean(false).toString());
                    detailElem.appendChild(theDoc.createTextNode(item.value));
                }
                                
                currElem.appendChild(detailElem);       
            }
            //Handling Unit
            else if(item.mnemonic.equals(CAD_FIELD_CODES.HANDLING_UNIT.mnemonic)) {
                //System.out.print("H");    
                Element unitElem = theDoc.createElement(CAD_FIELD_CODES.HANDLING_UNIT.fullName);
                unitElem.appendChild(theDoc.createTextNode(item.value));
                currElem.appendChild(unitElem);
            }               
            //IncidentNumber
            else if(item.mnemonic.equals(CAD_FIELD_CODES.INCIDENT_NUMBER.mnemonic)) {
                //System.out.print("I");
                currElem.setAttribute(CAD_FIELD_CODES.INCIDENT_NUMBER.fullName, item.value);
            }
            //Location
            else if(item.mnemonic.equals(CAD_FIELD_CODES.LOCATION.mnemonic)) {
                //System.out.print("L");
                currElem.setAttribute(DATA_TAGS.FULL_LOCATION.tag, item.value);
                currElem.setAttribute(DATA_TAGS.TRUNC_LOCATION.tag, item.value);
            }
            //Priority
            else if(item.mnemonic.equals(CAD_FIELD_CODES.PRIORITY.mnemonic)) {
                //System.out.print("P");                    
                currElem.setAttribute(CAD_FIELD_CODES.PRIORITY.fullName, item.value);               
            }                       
            //Routing mnemonic
            else if(item.mnemonic.equals(CAD_FIELD_CODES.ROUTE.mnemonic)) {
                //System.out.print("R");
                Element unitElem = theDoc.createElement(CAD_FIELD_CODES.ROUTE.fullName);
                unitElem.appendChild(theDoc.createTextNode(item.value));
                currElem.appendChild(unitElem);
            }   
            //Witness Phone Number
            else if(item.mnemonic.equals(CAD_FIELD_CODES.WITNESS_PHONE.mnemonic)) {
                //System.out.print("N");    
                witnessPhone = item.value;
            }
            //Type
            else if(item.mnemonic.equals(CAD_FIELD_CODES.TYPE.mnemonic)) {
                //System.out.print("T");
                currElem.setAttribute(CAD_FIELD_CODES.TYPE.fullName, item.value);
            }
            //Tow
            else if(item.mnemonic.equals(CAD_FIELD_CODES.TOW.mnemonic)) {
                //System.out.print("V");
                Element towElem = theDoc.createElement(CAD_FIELD_CODES.TOW.fullName);
                towElem.appendChild(theDoc.createTextNode("name=\"" + item.value + "\""));
                currElem.appendChild(towElem);
            }               
            //Witness
            else if(item.mnemonic.equals(CAD_FIELD_CODES.WITNESS.mnemonic)) {
                //System.out.print("W");    
                witnessName = item.value;
            }                       
        }   
        
        if(witnessName != null)
        {

            Element witnessElem = theDoc.createElement(CAD_FIELD_CODES.WITNESS.fullName);
            currElem.appendChild(witnessElem);
            
            witnessElem.setAttribute(DATA_TAGS.WITNESS_NAME.tag, witnessName);
        
            if(witnessAddr != null)
                witnessElem.setAttribute(DATA_TAGS.WITNESS_ADDR.tag, witnessAddr);  
                
            if(witnessPhone != null)
                witnessElem.setAttribute(DATA_TAGS.WITNESS_PHONE.tag, witnessPhone);
                
        }
    }
}
