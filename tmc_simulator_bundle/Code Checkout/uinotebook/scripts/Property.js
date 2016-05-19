/**
 * Represents a property of an event. Does not represent an evaluation.
 * @param type {String} Ex: "CHP Radio"
 * @param data Array(text) OR Array(label1, text1, label2, text2, ...)
 */
function Property(type, data)
{
	//========== public read-only members ==========//
	this.type = type;
	this.data = data;
	
	//========== public methods ==========//
	this.html = html;
	
	//========== method definitions ==========//
	/**
	 * @return The html representation of this property.
	 */
	function html()
	{
		var text = "<table class='property'>" +
	   	   		   "<tr>" +
	   	   		   "<th class='propertyType' colspace='" + 
	   	   		   (this.data.length == 1 ? 1 : 2) + "'>" + type + "</th>" +
	   	   		   "</tr>";
		
		if (this.data.length == 1)
		{
			text += "<tr>" +
				    "<td class='propertyContent'>" + this.data[0] + "</td>" +
				    "</tr>";
		}
		else
		{
			for (i=0;i<this.data.length;i+=2)
			{
				text += "<tr>" +
					   "<td class='propertyLabel'>" + this.data[i] + "</td>" +
					   "<td class='propertyValue'>" + this.data[i+1] + "</td>" +
					   "</tr>";
			}
		}
		
		text += "</table>";
		
		return text;
	}
}