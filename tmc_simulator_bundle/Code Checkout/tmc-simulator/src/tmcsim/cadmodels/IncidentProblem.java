package tmcsim.cadmodels;

import java.io.Serializable;

/**
 * This class is a further subdivision of Incident to hold data.
 * @author Vincent
 *
 */
public class IncidentProblem implements Serializable
{
   private String problemCode;
   private String priority;
   
   private static enum PROBLEM_ENUMS
    {
        CODE       ("CODE"), 
        PRIORITY   ("PRIORITY");
        
        public String tag;
        
        private PROBLEM_ENUMS(String t)
        {
            tag = t;
        }
    }
   
   /*
     * Constructor. Initializes all objects to avoid null pointers.
     */
   public IncidentProblem(String code, String prio)
   {
       setProblemCode(code);
       setPriority(prio);
   }

   public IncidentProblem() 
   {
       problemCode = "";
       priority = "";
   }

   public String getProblemCode() {
       return problemCode;
   }

   public void setProblemCode(String problemCode) {
       this.problemCode = problemCode;
   }

   public String getPriority() {
       return priority;
   }

   public void setPriority(String priority) {
       this.priority = priority;
   }
   
   /*
     * Called from the tmc.simulator.cadclient.data.Incident.java. Handles storing data based on script tag.
     */
   public void readXMLNode(String tag_name, String value) {
        if(tag_name.equals(PROBLEM_ENUMS.CODE.tag))
        {
            setProblemCode(value);
        }
        else if(tag_name.equals(PROBLEM_ENUMS.PRIORITY.tag))
        {
            setPriority(value);
        }
    }
   
   
}
