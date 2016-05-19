/**
 * Represents an event.
 * @param time {Time}
 * @param incident {Incident}
 * @param properties {Properties}
 * @param properties {Evaluations}
 */
function Event(time, incident, properties, evaluations)
{
	//========== private static members ==========//
	Event.invalidID = -1;
	
	//========== private static members ==========//
	Event.id = (typeof Event.id == 'undefined') ? 0 : ++Event.id;
	
	//========== public constants ==========//
	this.id = Event.id;
	this.expandOptionID = "eventExpandOption" + this.id;
	this.dataID = "eventData" + this.id;
	this.eventHeaderID = "eventHeader" + this.id;
	
	//========== public read-only members ==========//
	this.expanded = true;
	this.highlighted = false;
	this.time = time;
	this.incident = incident;
	this.evaluations = evaluations;
	this.properties = properties;
	
	//========== public methods ==========//
	this.html = html;
	this.expandAction = expandAction;
	this.highlight = highlight;
	this.unhighlight = unhighlight;
	this.focus = focus;
	
	//========== private methods ==========//
	this.expandOptionSymbol = expandOptionSymbol;
	this.headerHTML = headerHTML;
	this.expandOptionHTML = expandOptionHTML;
	this.refresh = refresh;
	this.dataHTML = dataHTML;
	this.expandAction = expandAction;
	this.html = html;
	
	//========== function definitions ===========//
	/**
	 * Returns the symbol for the expand/collapse option.
	 * @return "+" or "-" depending on whether this event is collapsed or expanded.
	 */
	function expandOptionSymbol() 
	{ 
		return this.expanded ? "&ndash;" : "+"; 
	}
	 
	/**
	 * @return The rectangle containing the expand/collapse symbol, 
	 *         incident time, incident number and
	 *         incident title that this event belongs to.
	 */
	function headerHTML()
	{
		return "<table class='eventHeader'>" +
				"<tr class='eventHeader'>" +
				"<td class='eventExpandOption'>" + this.expandOptionHTML() + "</td>" +
				"<td class='eventTime'>Time: " + this.time.format() + "</td>" +
				"<td class='eventNumber'>" + this.incident.number + "</td>" +
				"<td class='eventTitle'>" + this.incident.title + "</td>" + 
				"</tr>" +
				"</table>";
	}
	
	/**
	 * @return The html of the expand/collapse symbol.
	 */
	function expandOptionHTML()
	{
		return "<button class='eventExpandButton' id='" + this.expandOptionID + 
				"' onclick='events.get(" + 
				this.id + ").expandAction();'>" + this.expandOptionSymbol() 
				+ "</button>";
	}

	/**
	 * Updates the expand option symbol and the visibility of the event data depending on 
	 * whether this event is collapsed or expanded.
	 */
	function refresh()
	{
		events.doc.getElementById(this.dataID).innerHTML = 
			this.expanded ? this.dataHTML() : "";
			
		// IF the event is highlighted THEN
		if (this.highlighted)
		{
			this.highlight();
		}
		else
		{
			this.unhighlight();
		}
			
		events.doc.getElementById(this.expandOptionID).innerHTML = 
			this.expandOptionSymbol();
	}
	
	/**
	 * @return The html for the properties and the evaluations.
	 */
	function dataHTML()
	{
		return properties.html() + evaluations.html();
	}

	/**
	 * Expands or collapses this event depending on whether this event is 
	 * already expanded or collapsed.
	 */
	function expandAction() 
	{ 
		this.expanded = !this.expanded; 
		this.refresh(); 
	}

	/**
	 * @return The html representation of this event.
	 */
	function html()
	{
		return "<table class='event'>" +
			   "<tr>" +
			   "<td class='eventHeader' id='" + this.eventHeaderID + "'>" + 
			      this.headerHTML() + "</td>" +
			   "</tr>" +
			   "<tr>" + 
			   "<td class='eventData' id='" + this.dataID + "'>" + 
			   (this.expanded ? this.dataHTML() : "") + 
			   "</td>" +
			   "</tr>" +
			   "</table>";
	}
	
	/**
	 * Scrolls the window to this Event.
	 * @param window The window to scroll.
	 */
	function focus()
	{
		/* This method was discarded because it moved the scroll bar of the parent of
		 * the given window in addition to the scroll bar of the given window..
		var positionOfPound = window.location.indexOf("#");
		var rootLocation = window.location.substring(0, positionOfPound);
		window.location = rootLocation + "#" + eventAnchorName;
		*/
		events.win.scrollTo(0, absoluteTop(events.doc.getElementById(this.eventHeaderID)));
	}
	
	/**
	 * Highlights this Event.
	 */
	function highlight()
	{
		this.highlighted = true;
		events.doc.getElementById(this.eventHeaderID).style.borderColor = "blue";
		events.doc.getElementById(this.eventHeaderID).style.backgroundColor = "yellow";
		
		// IF this event is expanded THEN
		if (this.expanded) 
		{
			events.doc.getElementById(this.dataID).style.border = "2px solid blue";
		}
		else
		{
			events.doc.getElementById(this.dataID).style.border = "none";
		}
		
	}
	
	/**
	 * Unhighlights this event.
	 * @return
	 */
	function unhighlight()
	{
		this.highlighted = false;
		events.doc.getElementById(this.eventHeaderID).style.backgroundColor = "white";
		events.doc.getElementById(this.eventHeaderID).style.borderColor = "black";
		events.doc.getElementById(this.dataID).style.border = "0px solid red";
	}
	
	/**
	 * Returns the number of pixels from the top of the given element to the top
	 * of the body element.
	 * @param elem The element whose y-coordinate will be found.
	 * @return The y-coordinate of the given element.
	 */
	function absoluteTop(elem)
	{
		var offset = elem.offsetTop;
		var parent = elem.offsetParent;
		
		if (parent.tagName == "BODY")
		{
			return offset;
		}
		else
		{
			return offset + absoluteTop(parent);
		}
	}
}