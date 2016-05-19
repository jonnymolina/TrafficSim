/**
 * Stores the each Event. Maintains each Event sorted ascending by time. 
 * 
 * You must set
 *   1) events.doc to the document that events will be displayed
 *   2) events.win to the window on which the document resides that will display the events
 * before the 'html' method of Event, Evaluations, Evaluation, Properties, or Property 
 * has been called.
 */
var events = new Array();

//========== public members ==========//
events.doc = null;
events.win = null;

//========== public methods ==========//
events.get = events_get;
events.getProperties = events_getProperties;
events.getEvaluations = events_getEvaluations;
events.getEvaluation = events_getEvaluation;
events.collapseAllEvents = events_collapseAllEvents;
events.collapseAllProperties = events_collapseAllProperties;
events.collapseAllEvaluations = events_collapseAllEvaluations;
events.expandAllEvents = events_expandAllEvents;
events.expandAllProperties = events_expandAllProperties;
events.expandAllEvaluations = events_expandAllEvaluations;
events.expandAll = events_expandAll;
events.collapseAll = events_collapseAll;
events.add = events_add;
events.getAllProperties = events_getAllProperties;
events.getAllEvaluations = events_getAllEvaluations;
events.getLastExecutedEvent = events_getLastExecutedEvent;

//========== method definitions ==========//

/**
 * Searches through each Event to find an Event with the given id.
 * @param id {Integer} The id of an Event.
 * @return The Event with the given id.
 */
function events_get(id)
{
    var event;

    // FOR each Event 
    for (var i = 0; i < this.length; i++)
    {
    	// IF the Event's id matches THEN
        if (this[i].id == id)
        {
        	// remember the Event
            event = this[i];
        }
    }
    
    return event;
}

/**
 * Searches through the Properties of each event to find the Property with the given id.
 * @param id {Integer} The id of a Property.
 * @return The Property with the given id.
 */
function events_getProperties(id)
{
    var property;
    
    // FOR each Event
    for (var i = 0; i < this.length; i++)
    {
    	// IF the Event's id matches THEN
        if (this[i].properties.id == id)
        {
        	// remember the Property
            property = this[i].properties;
        }
    }
    
    return property;
}

/**
 * Searches through each Evaluations of each event to find the Evaluations with the given id.
 * @param id {Integer} The id of an Evaluations.
 * @return The Evaluations with the given id.
 */
function events_getEvaluations(id)
{
    var evaluations;

    // FOR each Event
    for (var i = 0; i < this.length; i++)
    {
    	// IF this Evalutions's id matches THEN
        if (this[i].evaluations.id == id)
        {
        	// remember the Evaluations
            evaluations = this[i].evaluations;  
        }
    }
    
    return evaluations;
}

/**
 * Searches through each Evaluation of each Evaluations of each Event to find the
 * Evaluation of with the given id.
 * @param id {Integer} The id of an Evaluation.
 * @return The Evaluation of with the given id.
 */
function events_getEvaluation(id)
{
    var evaluation;

    // FOR each Event
    for (var i = 0; i < this.length; i++)
    {
    	// FOR each Evaluation
        for (var j = 0; j < this[i].evaluations.evaluations.length; j++)
        {
        	// IF the Evaluation's id matches THEN
            if (this[i].evaluations.evaluations[j].id == id)
            {
            	// remember the Evaluation
                evaluation = this[i].evaluations.evaluations[j];
            }
        }
    }
    
    return evaluation;  
}

/**
 * Collapses each Event.
 */
function events_collapseAllEvents()
{
	// FOR each Event
	for (var i = 0; i < this.length; i++)
	{
		// IF the Event is expanded THEN
		if (this[i].expanded)
		{
			// collapse Event
			this[i].expandAction();
		}
	}
}

/**
 * Expands each event.
 */
function events_expandAllEvents()
{
	// FOR each Event
	for (var i = 0; i < this.length; i++)
	{
		// IF the Event is collapsed
		if (!this[i].expanded)
		{
			// expand Event
			this[i].expandAction();
		}
	}
}

function events_collapseAllProperties()
{
	var list = this.getAllProperties();
	
	// FOR each Properties
	for (var i = 0; i < list.length; i++)
	{
		// IF the Properties is expanded THEN
		if (list[i].expanded)
		{
			// collapse the Properties
			list[i].expandAction();
		}
	}
}

function events_collapseAllEvaluations()
{
	var list = this.getAllEvaluations();
	
	// FOR each Evaluations
	for (var i = 0; i < list.length; i++)
	{
		// IF the Evaluations is expanded THEN
		if (list[i].expanded)
		{
			// collapse the Evaluations
			list[i].expandAction();
		}
	}	
}

function events_expandAllProperties()
{
	var list = this.getAllProperties();
	
	// FOR each Properties
	for (var i = 0; i < list.length; i++)
	{
		// IF the Properties is collapsed THEN
		if (!list[i].expanded)
		{
			// expand the Properties
			list[i].expandAction();
		}
	}
}

function events_expandAllEvaluations()
{
	var list = this.getAllEvaluations();
	
	// FOR each Evaluations
	for (var i = 0; i < list.length; i++)
	{
		// IF the Evaluations is collapsed THEN
		if (!list[i].expanded)
		{
			// expand the Evaluations
			list[i].expandAction();
		}
	}	
}

/**
 * Adds an Event.
 * @param event {Event} The Event to add.
 */
function events_add(event)
{
	// add event
	events.push(event);
	
	// sort events in ascending order by time
	events.sort(function (e1, e2) {
		return e1.time.getSeconds() - e2.time.getSeconds();
	});
}

/**
 * Finds all of the Properties in each Event.
 * @return The list of the Properties in each Event.
 */
function events_getAllProperties()
{
	var list = new Array();
	
	// FOR each event
	for (var i = 0; i < this.length; i++)
	{
		// add properties to list
		list.push(this[i].properties);
	}
	
	return list;
}

/**
 * Finds all of the Evaluations in each Event.
 * @return The list of the Evaluations in each Event.
 */
function events_getAllEvaluations()
{
	var list = new Array();
	
	// FOR each event
	for (var i = 0; i < this.length; i++)
	{
		// add evaluations to list
		list.push(this[i].evaluations);
	}
	
	return list;
}

/**
 * Collapses each Event, Properties, and Evaluations.
 */
function events_collapseAll()
{
	events.collapseAllProperties(); 
	events.collapseAllEvaluations(); 
	events.collapseAllEvents();	
}

/**
 * Expands each Event, Properties, and Evaluations.
 * @return
 */
function events_expandAll()
{
	events.expandAllProperties(); 
	events.expandAllEvaluations(); 
	events.expandAllEvents();	
}

/**
 * Finds the Event that was last executed by based on given time.
 * @param time The time in seconds.
 * @return The last execute event if at least one event has executed; otherwise, null.
 */
function events_getLastExecutedEvent(time)
{
	var event = null;
	
	// IF there is only one event and it has been executed THEN
	if (this.length == 1 && this[0].time.getSeconds() <= time)
	{
		event = this[0];
	}
	else
	{
		//NOTE: remember that the events are maintained in ascending order by time
		
		// FOR each Event
		for (var i = 0; i < this.length - 1; i++)
		{
			// IF the Event has executed and no subsequent event has executed THEN
			if (this[i].time.getSeconds() <= time &&
			    this[i + 1].time.getSeconds() > time)
			{
				event = this[i];
			}
		}
		
		// IF the last event has executed THEN
		if (this[this.length - 1].time.getSeconds() <= time)
		{
			event = this[this.length - 1];
		}	
	}
	
	return event;
}

