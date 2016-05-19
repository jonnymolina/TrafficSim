incidentNames = ["Default", "Find Incident...", "181 - Tomato Truck Spill", 
                 "182 - Haytruck Crash", "183 - Car Stalled on Freeway"];

/**
 * Set up the maps.
 */
function setupMaps()
{
  showMap("Google Map");
}

/**
 * Set up Google Maps.
 */
function setupGoogleMaps()
{
  showIncident("Default");
}

/**
 * Shows an incident on Google maps
 * @param incident Incident name
 */
function showIncident(incident)
{
  for (var x = 0; x < incidentNames.length; x++)
  {
    document.getElementById(incidentNames[x]).style.display = "none";
  }
  document.getElementById(incident).style.display = "inline";
}

/**
 * Shows a map
 * @param map Map name
 */
function showMap(map)
{
  currentMap = map;
  if (map == "Google Map")
  {
	document.getElementById("googleMapContent").style.display = "inline";  
	document.getElementById("atmsMapContent").style.display = "none";  
    document.getElementById("mapSelect").innerHTML =
      "<table><tr>" +
        "<td><button disabled=true class=mapButton>Google Map</button></td><td>" +
        "<td><button onClick=\"showMap(\'ATMS Map\')\" class=mapButton>ATMS Map</button></td>" +
      "</tr></table>";
  }
  else if (map == "ATMS Map")
  {  
	document.getElementById("googleMapContent").style.display = "none";
	document.getElementById("atmsMapContent").style.display = "inline";  
    document.getElementById("mapSelect").innerHTML =
      "<table><tr>" +
        "<td><button onClick=\"showMap(\'Google Map\')\" class=mapButton>Google Map</button></td><td>" +
        "<td><button disabled=true class=mapButton>ATMS Map</button></td>" +
      "</tr></table>";
  }
}
