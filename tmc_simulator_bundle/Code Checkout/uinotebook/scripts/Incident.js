/**
 * Represents an incident. (Does not contain events. Event links itself to Incident.)
 * @param time {Time}
 * @param numbers {Number} 
 * @param title {String}
 * @param summary {String}
 */
function Incident(time, number, title, summary)
{	
	//========== public read-only members ==========//
	this.time = time;
	this.number = number;
	this.title = title;
	this.summary = summary;
	this.expanded = true;
	
	//========== public methods ==========//
	this.html = html;
	this.expandAction = expandAction;
	
	//========== private methods ==========//
	this.formatHeader = formatHeader;
	this.expandOption = expandOption;
	this.expandOptionSymbol = expandOptionSymbol;
	
	/**
	 * @return The html representation of this incident;
	 */
	function html()
	{
		return "<table class='incident'>" +
			   "<tr>" +
			   "<td class='incidentHeader'>" + this.formatHeader() + "</td>" +
			   "</tr>" +
			   "<tr>" + 
			   "<td class='incidentSummary' id='summary" + this.number + "'>" + 
			     (this.expanded ? this.summary : "") + "</td>" +
			   "</tr>" + 
			   "</table>";
	}
	

	/**
	 * @return The html of the rectangle that contains the expand/collapse symbol, time, 
	 *         incident number
	 *         and incident title.
	 */
	function formatHeader()
	{
		return "<table class='incidentHeader'>" +
				"<tr class='incidentHeader'>" +
				"<td class='incidentExpandOption'>" + this.expandOption() + "</td>" +
				"<td class='incidentTime'>Time: " + this.time.format() + "</td>" +
				"<td class='incidentNumber'>" + this.number + "</td>" +
				"<td class='incidentTitle'>" + this.title + "</td>" + 
				"</tr>" +
				"</table>";
	}
	
	/**
	 * @return The html that for the + or - symbol that expands or collapses the incident.
	 */
	function expandOption()
	{
		return "<button class='incidentExpandButton' " +
				"id='expandOption" + this.number + "' onclick='incidents.get(" + 
				this.number + ").expandAction()'>" + this.expandOptionSymbol() + 
				"</button>";
	}
	
	/**
	 * Returns the symbol for the expand option.
	 * @return Either "+" or "-" depending on whether this incidents is collapsed or 
	 *         expanded.
	 */
	function expandOptionSymbol()
	{
		return this.expanded == true ? "&ndash;" : "+";
	}
	
	/**
	 * Performs the action of clicking on the + or - symbol that expands or collapses 
	 * the incident.
	 */
	function expandAction()
	{
		if (this.expanded)
		{
			incidents.doc.getElementById("summary" + this.number).innerHTML = "";
			incidents.doc.getElementById("expandOption" + this.number).innerHTML = "+";
		}
		else
		{
			incidents.doc.getElementById("summary" + this.number).innerHTML = 
				this.summary;
			incidents.doc.getElementById("expandOption" + this.number).innerHTML = 
				"&ndash;";
		}
		
		this.expanded = !this.expanded;
		
	}
}