/**
 * Stores each Incident. You must set Incident.doc to the document that incidents will be 
 * displayed on before using this class.
 */
var incidents = new Array();

//========== public members ==========/
incidents.doc = null;

//========== public methods ==========//
incidents.get = incidents_get;
incidents.collapseAll = incidents_collapseAll;
incidents.expandAll = incidents_expandAll;
incidents.add = incidents_add;

//=========== method definitions ==========//
/**
 * Searches through each Incident to find an Incident with the given number.
 * @param number {Integer} The number of an incident.
 * @return The Incident with the given number.
 */
function incidents_get(number)
{
	var incident;
	
	// FOR each incident
	for (var i = 0; i < this.length; i++)
	{
		// IF the incident's number matches
		if (this[i].number == number)
		{
			// remember the incident
			incident = this[i];
		}
	}
	
	return incident;
}

/**
 * Collapses each Incident.
 */
function incidents_collapseAll()
{
	// FOR each incident
	for (var i = 0; i < this.length; i++)
	{
		// IF the incident is expanded THEN
		if (this[i].expanded)
		{
			// collapse incident
			this[i].expandAction();
		}
	}
}

/**
 * Expands each incident.
 */
function incidents_expandAll()
{
	// FOR each incident
	for (var i = 0; i < this.length; i++)
	{
		// IF incident is collapsed THEN
		if (!this[i].expanded)
		{
			// expand incident
			this[i].expandAction();
		}
	}
}

/**
 * Adds an Incident.
 * @param incident {Incident} The Incident to be added.
 */
function incidents_add(incident)
{
	incidents.push(incident);
}