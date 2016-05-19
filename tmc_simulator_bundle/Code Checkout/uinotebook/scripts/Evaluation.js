/**
 * Represents an evaluation (e.g., a "CMS evaluation").
 * @param type {String} Ex: "CMS" or "ATMS" ...
 * @param data Array(label1, text1, label2, text2, ...)
 */
function Evaluation(type, data)
{
	//========== private static members ==========//
	Evaluation.id = (typeof Evaluation.id == 'undefined') ? 0 : ++Evaluation.id;
	
	//========== public constants ==========//
	this.id = Evaluation.id;
	this.ratingGroupName = "evaluationGroup" + this.id;	
	this.textID = "evaluationText" + this.id;
	this.type = type;
	this.data = data;
	
	//========== public read-only members ==========//
	this.text = "";
	this.rating = -1;
	
	//========== public methods ==========//
	this.html = html;
	this.recordText = recordText; 
	this.recordRating = recordRating;
	
	//========== private methods ==========//
	this.smallScaleForm = smallScaleForm;
	this.evaluationForm = evaluationForm;
	
	//========== function definitions ==========//
	/**
	 * @return The html representation of this evaluation.
	 */
	function html()
	{
		var text = "<table class='evaluation'>" +
	   	   		   "<tr>" +
	   	   		   "<th class='evaluationType' colspan='2'>" + type + " Evalution</th>" +
	   	   		   "</tr>" +
	   	   		   "<tr>" +
	   	   		   "<td><table class='evaluationInner2'>";
		
		for (i = 0; i < this.data.length; i += 2)
		{
			text += "<tr>" +
				   "<td class='evaluationLabel'>" + this.data[i] + "</td>" +
				   "<td class='evaluationCriteria'>" + this.data[i+1] + "</td>" +
				   "</tr>";
		}
		
		text += "<tr><td colspan='2' class='evaluationResponse'>" +
			"<input onchange='events.getEvaluation(" + this.id + ").recordText();'" +
			" id='" + this.textID + "' type='text' value='" + this.text + "'" +
			" class='evaluationResponseText' />" 
			+ "</td>";
		
		text += "</table></td>" +
   		   		"<td class='evaluationScale'>" + this.evaluationForm() + "</td>" +
	   		    "</tr>" +
   		   		"</table>";
		
		return text;
	}
	
	/**
	 * Stores the rating given by the evaluation scale radio button form.
	 */
	function recordRating() 
	{ 
		// list of radio buttons
		var radioButtons;
		
		// get list of radio buttons
		radioButtons = events.doc.getElementsByName(this.ratingGroupName);
		
		// FOR each radio button
		for (var i = 0; i < radioButtons.length; i++)
		{
			// IF the radio button is checked THEN
			if (radioButtons[i].checked)
			{
				// save rating
				this.rating = radioButtons[i].value;
			}
		}
	}

	/**
	 * Stores the text in the written response text box.
	 */
	function recordText() 
	{ 
		this.text = events.doc.getElementById(this.textID).value; 
	}
	
	/**
	 * @return The html for appropriate evaluation form for grading.
	 */
	function evaluationForm()
	{
		return this.smallScaleForm();
	}
	
	/**
	 * @return The html for a 1-5 grading scale.
	 */
	function smallScaleForm()
	{
		return "<form>" +
			   "<table align='right' class='evaluationScale'>" +
			   "<tr>" +
			   "<td class='eventRadioButtonSmallScale'>Best</td>" +
			   "<td class='eventRadioButtonSmallScale'>Good</td>" +
			   "<td class='eventRadioButtonSmallScale'>Average</td>" +
			   "<td class='eventRadioButtonSmallScale'>Poor</td>" +
			   "<td class='eventRadioButtonSmallScale'>Worst</td>" +
			   "</tr>" +
			   "<tr>" +
			   "<td align='center'><input type='radio' " + 
			   		(this.rating == 1 ? "checked='true'" : "") + 
			   		" onchange='events.getEvaluation(" + this.id + 
			   		").recordRating()' name='" + this.ratingGroupName + 
			   		"' value='1'></td>" +
			   "<td align='center'><input type='radio' " + 
			   		(this.rating == 2 ? "checked='true'" : "") + 
			   		" onchange='events.getEvaluation(" + this.id + 
			   		").recordRating()' name='" + this.ratingGroupName + 
			   		"' value='2'></td>" +
			   "<td align='center'><input type='radio' " + 
			   		(this.rating == 3 ? "checked='true'" : "") + 
			   		" onchange='events.getEvaluation(" + this.id + 
			   		").recordRating()' name='" + this.ratingGroupName + 
			   		"' value='3'></td>" +
			   "<td align='center'><input type='radio' " + 
			   		(this.rating == 4 ? "checked='true'" : "") + 
			   		" onchange='events.getEvaluation(" + this.id + 
			   		").recordRating()' name='" + this.ratingGroupName + 
			   		"' value='4'></td>" +
			   "<td align='center'><input type='radio' " + 
			   		(this.rating == 5 ? "checked='true'" : "") + 
			   		" onchange='events.getEvaluation(" + this.id + 
			   		").recordRating()' name='" + this.ratingGroupName + 
			   		"' value='5'></td>" +
			   "</tr>" +
			   "</table>"  +
			   "</form>";
	}
}