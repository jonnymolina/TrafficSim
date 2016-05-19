/**
 * Represents a collection of properties that belong to a single event.
 * @param properties {Array of Property} The list of Property to be grouped together.
 */
function Properties(properties)
{
	//========== private static members ==========//
	Properties.id = (typeof Properties.id == 'undefined') ? 0 : ++Properties.id;
	
	//=========== public constants ==========//
	this.properties = properties;
	this.id = Properties.id;
	this.expandOptionID = "propertiesExpandOption" + this.id;
	this.propertiesID = "properties" + this.id;
	
	//========== public read-only members ==========//
	this.expanded = true;

	//========== public methods ==========//
	this.html = html;
	this.expandAction = expandAction;
	
	//========== private methods ==========//
	this.expandOption = expandOption;
	this.expandOptionSymbol = expandOptionSymbol;
	this.refresh = refresh;
	this.propertiesHTML = propertiesHTML;
	
	//========== method definitions ==========//
	
	/**
	 * @return The html for the "+" or "-" button that expands/collapses the properties.
	 */
	function expandOption() 
	{ 
		return "<button class='propertiesExpandButton' id='" + 
				this.expandOptionID + "' onclick='events.getProperties(" 
				+ this.id + ").expandAction();'>" + this.expandOptionSymbol() +
				"</button>";
	}
	
	/**
	 * If the list of properties are expanded, then collapses the list of properties.
	 * If the list of properties are collapsed, then expands the list of properties.
	 */
	function expandAction() 
	{ 
		this.expanded = !this.expanded; this.refresh(); 
	}
	
	/**
	 * @return Returns the "+" or "-" symbol for the expand/collapse button.
	 */
	function expandOptionSymbol() 
	{ 
		return this.expanded == true ? "&ndash;" : "+"; 
	}
	
	/**
	 * Displays the correct symbol for the expand/collapse button.
	 * Displays or hides the list of properties depending on the expand/collapse state.
	 */
	function refresh() 
	{
		// IF the expandOption element exists THEN
		if (events.doc.getElementById(this.expandOptionID))
		{
			events.doc.getElementById(this.expandOptionID).innerHTML = this.expandOptionSymbol();
		}
		
		// IF the properties element exists THEN
		if (events.doc.getElementById(this.propertiesID))
		{
			events.doc.getElementById(this.propertiesID).innerHTML = this.expanded ? this.propertiesHTML() : "";
		}
	}
	
	/**
	 * @return The html for the list of properties.
	 */
	function propertiesHTML()
	{
		var html = "<table>";
		
		for (var i = 0; i < this.properties.length; i++)
		{
			html += "<tr>" +
					"<td>" + this.properties[i].html() + "</td>" +
					"</tr>";
		}
		
		html += "</table>";
		
		return html;
	};
	
	/**
	 * @return The html representation of this class.
	 */
	function html()
	{
		var text = "";
		
		if (this.properties.length > 0)
		{
			text = "<table>";
			
			text += "<tr>" +
					"<td class='propertiesExpandOption'>" + this.expandOption() + 
					" Properties</td>" +
					"</tr>";

				text += "<tr>" +
						"<td class='properties' id='" + this.propertiesID + "'>" + 
					    (this.expanded ? this.propertiesHTML() : "") + "</td>" +
						"</tr>";
			
			text +="</table>";
		}
		
		return text;
	}
	
}