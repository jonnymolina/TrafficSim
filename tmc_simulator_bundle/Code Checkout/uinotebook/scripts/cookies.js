/**
 * Creates a new cookie if the given cookie name does not exist.
 * Sets a the cookie with the given name if it already exists.
 * The cookie that is created or set expires at the end of the session.
 * @param name {String} The name of the cookie.
 * @param value {Any} The value of the cookie.
 */
function setCookie(name, value)
{
	createCookie(name, value);
}

/**
 * Creates a new cookie if the given cookie name does not exist.
 * Sets a the cookie with the given name if it already exists.
 * If days is not given, the cookie expires when the session ends.
 * @param name {String} The name of the cookie.
 * @param value {Any} The value of the cookie.
 * @param days {Integer} (Optional) The number of days before the cookie expires.
 */
function createCookie(name, value, days) 
{
	if (days) 
	{
		var date = new Date();
		date.setTime(date.getTime()+(days*24*60*60*1000));
		var expires = "; expires="+date.toGMTString();
	}
	else 
	{
		var expires = "";
	}
	
	document.cookie = name+"="+value+expires+"; path=/";
	

}

/**
 * Reads the value of the cookie with the given name.
 * @param name The name of the cookie to be read.
 * @return The value of the cookie with the given name.
 */
function readCookie(name) 
{
	var nameEQ = name + "=";
	var ca = document.cookie.split(';');
	
	for(var i = 0; i < ca.length; i++) 
	{
		var c = ca[i];
		while (c.charAt(0) == ' ')
		{
			c = c.substring(1,c.length);
		}
		if (c.indexOf(nameEQ) == 0)
		{
			return c.substring(nameEQ.length,c.length);	
		}
	}
	
	return null;
}

/**
 * Deletes the cookie with the given name.
 * @param name The name of the cookie to delete.
 */
function eraseCookie(name) 
{
	createCookie(name, "", -1);
}