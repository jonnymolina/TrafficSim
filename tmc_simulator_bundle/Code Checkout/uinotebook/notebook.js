/*
function loadXMLDoc()
{
  if (window.XMLHttpRequest)
  {
    xmlhttp = new XMLHttpRequest();
  }
  else
  {
    xmlhttp = new ActiveXObject("Microsoft.XMLHTTP");
  }

  xmlhttp.onreadystatechange=function()
  {
    if (xmlhttp.readyState==4 && xmlhttp.status==200)
    {
      document.getElementById("myDiv").innerHTML = xmlhttp.responseText;
    }
  }
  
  xmlhttp.open("GET", "ajax_info.txt", true);
  xmlhttp.send("");
}
*/

/**
 * Sets the selected tab to the summary tab and loads the summary page.
 */

function setupNotebook()
{
   changeTab('summaryTab');
   loadSummary();
   setCookie("time", "0");
   setCookie("scriptScrollY", 0);
   setCookie("summaryScrollY", 0);
}

/**
 * Runs the script.
 */
function run()
{
	run.initialDelay = 1000;
	
	setTimeout("run.run()", run.initialDelay);
	
	run.run = function()
	{
	    document.getElementById("simulationStatus").innerHTML = "RUNNING";                 
		setTime();
		updateStatus();
	};
}

/**
 * Displays fading text alerts in the status box (left-top corner).
 */
function updateStatus()
{
	// IF last event if it has not been set before THEN
    if (typeof updateStatus.lastEvent == 'undefined')
    {
    	updateStatus.lastEvent = null;
    }
   
    var latestEvent = events.getLastExecutedEvent(readCookie("time"));
    
    
    // IF a new event executed THEN
    if (updateStatus.lastEvent != latestEvent)
    {
		// IF the new event has evaluations THEN
    	if (latestEvent.evaluations.evaluations.length == 0)
    	{
    		document.getElementById('updateStatus').innerHTML = "New Event";
    	}
    	else
    	{
    		document.getElementById('updateStatus').innerHTML = "New Evaluation";
    	}
    	
		// fades the text
		updateStatus.fade = function fade()
		{
			// IF the fade color is not black yet THEN
    		if (fade.hex < 255)
    		{
    			fade.hex += 5;
    			document.getElementById('updateStatus').style.color = 
    				"rgb(" + fade.hex + ", " + fade.hex + ", " + fade.hex + ")";  
    			setTimeout("updateStatus.fade()", 100);	
    		}
    		else
    		{
    			document.getElementById('updateStatus').style.color = "white";
    		}

		};
        
		updateStatus.fade.hex = 0;
		updateStatus.fade();
    	updateStatus.lastEvent = latestEvent;
    }
    
    setTimeout("updateStatus();", 1000);
}

/**
 * Sets 'simulationTime' to the current simulation time in seconds.
 * This method runs itself every second to keep the time current.
 * Sets the cookie 'time' to the value of 'simulationTime'.
 * @return
 */
function setTime()
{
	// increment time by one second (or initialize it if it has not been set before)
	setTime.time = (typeof setTime.time == 'undefined') ? 0 : ++setTime.time;

    setCookie("time", "" + setTime.time);
   
	// display the latest simulation time
	document.getElementById("simulationTime").innerHTML = 
		Time.formatTime(setTime.time);
	
    setTimeout("setTime()", 1000);
}

/**
 * Selects a new tab to be viewed. 
 * @param id The id for the tab to be selected
 */
function changeTab(id)
{
    /* Set all tabs to not being active */
    document.getElementById("summaryTab").className = "notActive";
    document.getElementById("scriptTab").className = "notActive";
    document.getElementById("CADTab").className = "notActive";
    document.getElementById("mapsTab").className = "notActive";

    /* Set the selected tab to being active */
    document.getElementById(id).className = "activeTab";
}

/**
 * Loads the script tab page;
 */
function loadScript()
{
}

/**
 * Loads the summary tab page;
 */
function loadSummary()
{	   
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
 * Hides a tab page
 * @param d The div of the page to hide.
 */
function hideContent(d) 
{
    document.getElementById(d).style.display = "none";
}

/**
 * Switches the tab page.
 * @param d The div of the page to show.
 */
function showContent(d) 
{
    hideContent('summaryPageContent');
    hideContent('scriptPageContent');
    hideContent('cadPageContent');
    hideContent('mapsPageContent');    
    document.getElementById(d).style.display = "inline-block";
}