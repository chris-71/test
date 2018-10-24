
   var weekDays = ["Måndag", "Tisdag", "Onsdag", "Torsdag", "Fredag", "Lördag", "Söndag"];
   
   function showTable(callback) {
	   $.getJSON('http://localhost:8080/api/stats/', function (data) {
	     callback(data);
	   });
	 }
   
   showTable(function (data) {
	   var rows = 7,
	   cols = data.statistics.length;
	   
	   var th = $('<thead/>');
	   th.append('<tr>');
	   th.append('<th> v.' + data.weekNbr + '</th>');
	   
	   for (var i = 0; i < cols; i++) {
		   th.append("<th contenteditable='true' onfocusout='changeName(this," + '"' + data.statistics[i].name + '"' + ")'>" + data.statistics[i].name + '</th>');
	   }
	   
	   th.append('</tr></thead>');
	   $('table').append(th);
			    
	   for(var j = 0;j < rows; j++){
		   var tr = $('<tr/>');
		   tr.append("<td>" + weekDays[j] + "</td>");
		   
		   for (var k = 0; k < cols; k++) {
			   tr.append("<td>" + data.statistics[k].producedDaily[j] + " (" + data.statistics[k].dailyProductionTarget + ")</td>");
		   }
		   
		   $('table').append(tr);
	   }
	   
	   var total = $('<tr/>');
	   total.append('<th>Totalt:</th>');
		
	   for (var l = 0; l < data.statistics.length; l++) {
		   total.append("<td>" + data.statistics[l].producedWeekTotal + "</td>");	
	   }
	   $('table').append(total);
	   
	 });
   
           
    function changeName(elem, oldName) {
    	$.getJSON("http://localhost:8080/api/renameMachine?oldName=" + oldName + "&newName=" + elem.innerText);
    }
    
    $(document).ready(showTable);
  