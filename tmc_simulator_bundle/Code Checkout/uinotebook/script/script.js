/**
 * Holds references to the events and incidents.
 */
function Script()
{
	Script.events = null;
	Script.incidents = null;
}

/**
 * Scrolls to the last event executed based on time.
 */
function jumpToLastExecutedEvent()
{	
	var lastEvent = Script.events.getLastExecutedEvent(readCookie("time"));
	
	// IF a current event exists THEN
	if (lastEvent != null)
	{
		lastEvent.focus();
	}
	else
	{
		alert("No events have been executed yet.");
	}
}

/**
 * Finds the Event that was last executed by based on time and highlights it.
 * Removes highlighting from old events.
 * @return
 */
function highlightLatestEvent() 
{
	var currentEvent = Script.events.getLastExecutedEvent(readCookie("time"));

	// IF the old event is has not been set THEN
	if (typeof highlightLatestEvent.oldEvent == 'undefined')
	{
		highlightLatestEvent.oldEvent = new Object();
		highlightLatestEvent.oldEvent.id = Event.invalidID;
	}
	
	// IF a current event exists THEN
	if (currentEvent != null)
	{
		// IF there is a new current event THEN
		if (highlightLatestEvent.oldEvent.id != currentEvent.id)
		{
			currentEvent.highlight();
			
			// IF there was previously a current event THEN
			if (highlightLatestEvent.oldEvent.id != Event.invalidID)
			{
				highlightLatestEvent.oldEvent.unhighlight();
			}
			
			highlightLatestEvent.oldEvent = currentEvent;			
		}
	}
	
	setTimeout("highlightLatestEvent()", 1000);
}

/**
 * Loads the the script tab for the given document.
 * @pre script.html must be completely loaded
 * @param aDocument
 */
function loadScript(theEvents, theIncidents)
{
	Script.incidents = theIncidents;
	Script.events = theEvents;
	Script.events.win = document.getElementById("view").contentWindow;
	Script.events.doc = getDocumentFromFrame('view');

	var html = "";
	
	// FOR each Event
	for (var i = 0; i < Script.events.length; i++)
	{
		// add the Event's html
		html += Script.events[i].html();
	}
	
	// display events in iframe
	getDocumentFromFrame('view').body.innerHTML = html;	
	
	// resize iframe to appropriate height
	resizeIframe();
	window.onresize = resizeIframe;
	window.frames['view'].setEvents(Script.events);
	
	Script.events.win.scrollTo(0, readCookie('scriptScrollY'));
	
	highlightLatestEvent();

}

/**
 * @param id The id of the frame element.
 * @return The document of the frame element.
 */
function getDocumentFromFrame(id)
{
    var frame=document.getElementById(id);
    var doc=(frame.contentWindow || frame.contentDocument);
    
    if (doc.document)doc=doc.document;
    
    return doc;
}

/**
 * Finds the height of an element relative to the BODY tag.
 * @param elem The element to find the height of.
 * @return The y-coordinate of the given element relative to the BODY tag.
 */
function pageY(elem) 
{
    return elem.offsetParent ? (elem.offsetTop + pageY(elem.offsetParent)) : elem.offsetTop;
}

/**
 * Resizes the view for the events so that the view's height is at the bottom of its
 * container.
 */
function resizeIframe() 
{
    var height = document.documentElement.clientHeight;
    height -= pageY(document.getElementById('view'));
    height = (height < 0) ? 0 : height - 10;
    document.getElementById('view').style.height = height + 'px';
}

/**
 * Highlights the text of an input text element after a small delay.
 * This method was made because using the code "onFocus='this.focus()'" did not work.
 * @pre textField must have an 'id' attribute
 * @param textField An input text element.
 */
function highlightTextField(textField)
{
	setTimeout("document.getElementById('" + textField.id + "').select();", 100);
}

/**
 * Formats a two digit textbox. If the text box contains only one digit, then a zero
 * is appended to the front.
 * @param textField An input text element.
 */
function formatTimeTextfield(textField)
{
	// IF the text field contains 1 digits THEN
	if (textField.value.length == 1)
	{
		textField.value = "0".concat(textField.value);
	}
}

/**
 * Scrolls to the event latest executed event if the current simulation time was given
 * by text fields 'timeTextSeconds', 'timeTextMinutes', 'timeTextHours'.
 */
function jumpToTime()
{
	var seconds = parseInt(document.getElementById('timeTextSeconds').value, 10);
	var minutes = parseInt(document.getElementById('timeTextMinutes').value, 10);
	var hours = parseInt(document.getElementById('timeTextHours').value, 10);

	var lastEvent = Script.events.getLastExecutedEvent(new Time(hours, minutes, seconds).getSeconds());

	// IF an event was executed THEN
	if (lastEvent != null)
	{
		lastEvent.focus();
	}
}

/**
 * Collapses the elements based on the value of the dropdownbox with id 'domain'.
 */
function collapse()
{
	var selection = document.getElementById('domain');
	var selectedIndex = selection.selectedIndex;
	var selectedValue = selection.options[selectedIndex].value;
	
	eval("Script.events.collapseAll" + selectedValue + "();");
}

/**
 * Expands the elements based on the value of the dropdownbox with id 'domain'.
 */
function expand()
{
	var selection = document.getElementById('domain');
	var selectedIndex = selection.selectedIndex;
	var selectedValue = selection.options[selectedIndex].value;
	
	eval("Script.events.expandAll" + selectedValue + "();");
}

