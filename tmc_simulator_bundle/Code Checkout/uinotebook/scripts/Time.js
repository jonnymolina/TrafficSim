/**
 * Represents a time.
 * @param hrs {Integer} 0 <= hrs <= 59
 * @param mins {Integer} 0 <= mins <= 59
 * @param secs {Integer} 0 <= secs <= 59
 */
function Time(hrs, mins, secs)
{
	//========== public read-only members ==========//
	this.hrs = hrs;
	this.mins = mins;
	this.secs = secs;
	
	//========== public static methods ==========//
	Time.formatTime = formatTime;
	
	//========== public methods ==========//
	this.format = format;
	this.compareTo = compareTo;
	this.getSeconds = getSeconds;
	
	//========== method definitions ===========//
	/**
	 * @return The number of seconds equal to the total amount time.
	 */
	function getSeconds()
	{
		return this.secs + this.mins * 60 + this.hrs * 3600;
	}
	
	/**
	 * @param time {Time} The Time to compare this to.
	 * @return < 0 if this is less than time;
	 *           0 if this is equal to time;
	 *         > 0 if this is greater than time
	 */
	function compareTo(time)
	{
		return this.getSeconds() - time.getSeconds();
	}
	
	/**
	 * @return Returns the time in the form of HH:MM:SS.
	 */
	function format() {
		function pad0(n)
		{
			return n < 10 ? "0" + n : n;
		}
		
		return pad0(hrs) + ":" + pad0(mins) + ":" + pad0(secs);
	}
	
	/**
	 * Converts time in seconds into the form of HH:MM:SS.
	 * @param seconds {Integer}
	 * @return The given time in the form of HH:MM:SS.
	 */
	function formatTime(seconds)
	{
		function pad0(n)
		{
			return n < 10 ? "0" + n : n;
		}
		
		var remainSecs = seconds % 60;
		var minutes = (seconds - remainSecs) / 60;
		var remainMins = minutes % 60;
		var hours = (minutes - remainMins) / 60;
		return pad0(hours) + ":" + pad0(remainMins) + ":" + pad0(remainSecs);
	}
}