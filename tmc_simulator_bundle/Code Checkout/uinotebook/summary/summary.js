var incidents;

/**
 * @return The html for the list of incidents.
 */
function incidentsHTML()
{
	
	var html = "";
	
	// FOR each incident
	for (var i = 0; i < incidents.length; i++)
	{
		// add incident html
		html += incidents[i].html();
	}
	
	return html;
}

function loadSummary(theIncidents)
{
	incidents = theIncidents;
	// display the list of incidents
	document.getElementById("view").innerHTML = incidentsHTML();
	window.scrollTo(0, readCookie("summaryScrollY"));
}
