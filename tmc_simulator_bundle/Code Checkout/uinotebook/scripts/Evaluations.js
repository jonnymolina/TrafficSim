/**
 * Represents a collection of evaluations that belong to a single event.
 * @param evaluations {Array of Evaluation} The list of Evaluation to be grouped together.
 */
function Evaluations(evaluations)
{
	//========== private static members ==========//
	Evaluations.id = (typeof Evaluations.id == 'undefined') ? 0 : ++Evaluations.id;
	
	//========== public constants ==========//
	this.evaluations = evaluations;
	this.id = Evaluations.id;
	this.expandOptionID = "evalutionsExpandOption" + this.id;
	this.evaluationsID = "evaluations" + this.id;
	
	//========== public read-only members ==========//
	this.expanded = true;

	//========== public methods ==========//
	this.html = html;
	this.expandAction = expandAction;
	
	//========== private methods ==========//
	this.expandOption = expandOption;
	this.expandOptionSymbol = expandOptionSymbol;
	this.refresh = refresh;
	this.evaluationsHTML = evaluationsHTML;
	
	//========== function definitions ==========//
	/**
	 * @return The html for the "+" or "-" button that expands or collapses these
	 *         evaluations when clicked.
	 */
	function expandOption() 
	{ 
		return "<button class='evaluationsExpandButton' id='" + 
				this.expandOptionID + "' onclick='events.getEvaluations(" 
				+ this.id + ").expandAction();'>" + 
				this.expandOptionSymbol() + "</button>";
	}
	
	/**
	 * @return The "-" or "-" symbol for the expand/collapse button.
	 */
	function expandOptionSymbol() 
	{ 
		return this.expanded == true ? "&ndash;" : "+"; 
	}
	
	/**
	 * Updates the expand/collapse symbol and whether the evaluations are visible.
	 */
	function refresh() 
	{
		// IF the expandOption element exists THEN
		if (events.doc.getElementById(this.expandOptionID))
		{
			events.doc.getElementById(this.expandOptionID).innerHTML = 
				this.expandOptionSymbol();
		}
		
		// IF the evaluations element exists THEN
		if (events.doc.getElementById(this.evaluationsID))
		{
			events.doc.getElementById(this.evaluationsID).innerHTML = 
			this.expanded ? this.evaluationsHTML() : "";
		}
	}
	
	/**
	 * @return The html for the evaluations.
	 */
	function evaluationsHTML()
	{
		var html = "<table class='evaluationsInner'>";
		
		// FOR each evaluation
		for (var i = 0; i < this.evaluations.length; i++)
		{
			// add evaluation html
			html += "<tr>" +
					"<td>" + this.evaluations[i].html() + "</td>" +
					"</tr>";
		}
		
		html += "</table>";
		
		return html;
	}

	/**
	 * Expands the evaluations if they are collapsed.
	 * Collapses the evaluations if they are expanded.
	 */
	function expandAction() 
	{ 
		this.expanded = !this.expanded; this.refresh(); 
	}
	
	/**
	 * @return The html representation of these evaluations.
	 */
	function html()
	{
		var text = "";
		
		// IF there are any evaluations THEN
		if (this.evaluations.length > 0)
		{
			text = "<table class='evaluationsOuter'>";
			
			text += "<tr>" +
					"<td class='evaluationsExpandOption'>" + this.expandOption() + 
					" Evaluations</td>" +
					"</tr>";
			
				text += "<tr>" +
						"<td class='evaluations' id='" + this.evaluationsID + "'>" + 
						(this.expanded ? this.evaluationsHTML() : "") + "</td>" +
						"</tr>";
			
			text +="</table>";
		}
		
		return text;
	}
	
}