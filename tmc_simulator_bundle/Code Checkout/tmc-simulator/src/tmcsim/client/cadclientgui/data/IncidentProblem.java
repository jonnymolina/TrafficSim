package tmcsim.client.cadclientgui.data;

import java.io.Serializable;

import tmcsim.client.cadclientgui.enums.CADDataEnums;

/**
 * This class is a further subdivision of Incident to hold data.
 * 
 * @author Vincent
 * 
 */
public class IncidentProblem implements Serializable {
    
    /* The init variables are set only from the XML script (readXMLNode method)
     * and are only used for resetCADDataSimulation purposes
     */
    private String init_problem = "";
    private String init_problemCode = "";
    private String init_priority = "";
    
    private String problem;
    private String problemCode;
    private String priority;

    /*
     * Constructor. Initializes all objects to avoid null pointers.
     */
    public IncidentProblem(String code, String prio) {
        problem = "";
        setProblemCode(code);
        setPriority(prio);
    }

    public IncidentProblem() {
        problem = "";
        problemCode = "";
        priority = "";
    }
    
    public void resetCADDataSimulation(){
        problem = init_problem;
        problemCode = init_problemCode;
        priority = init_priority;
    }

    public String getProblem() {
        return problem;
    }

    public void setProblem(String problem) {
        this.problem = problem;
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
     * Called from the tmc.simulator.cadclient.data.Incident.java. Handles
     * storing data based on script tag.
     */
    public void readXMLNode(String tag_name, String value) {
        if (tag_name.equals(CADDataEnums.INC_PROBLEM.PROBLEM.tag)) {
            init_problem = value;
            setProblem(value);
        } else if (tag_name.equals(CADDataEnums.INC_PROBLEM.CODE.tag)) {
            init_problemCode = value;
            setProblemCode(value);
        } else if (tag_name.equals(CADDataEnums.INC_PROBLEM.PRIORITY.tag)) {
            init_priority = value;
            setPriority(value);
        }
    }

}
