package tmcsim.simulationmanager.model;

public class IncidentListTableItem implements Comparable<IncidentListTableItem> {
    
    public Integer logNumber;
    public Long    scheduleTime;
    public String  description;
    
    public IncidentListTableItem(Integer log, Long time, String desc) {
        logNumber    = log;
        scheduleTime = time;
        description  = desc;
    }
    
    public int compareTo(IncidentListTableItem arg) {
        return scheduleTime.compareTo(arg.scheduleTime);
    }
    
    
}
