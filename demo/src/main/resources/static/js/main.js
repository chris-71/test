
   var weekDays = ["Måndag", "Tisdag", "Onsdag", "Torsdag", "Fredag", "Lördag", "Söndag"];
    
        $(document).ready(function () {
        	$.getJSON("http://localhost:8080/api/details/",
        			function (data) {
        				var rows = 7,
        					cols = data.statistics.length;
        				
        			    var th = $('<thead/>');
        			    th.append('<tr>');
        			    th.append("<th> v." + data.weekNbr + "</th>");
        			    for (var i = 0; i < cols; i++) {
        			    	th.append("<th onclick='changeName(this)'>" + data.statistics[i].name + "</th>");
        			    }
        			    th.append('</tr></thead>');
        			    $('table').append(th);
        			    
        			    for(var j = 0;j < rows; j++){
        			    	var tr = $('<tr/>');
        			    	tr.append("<td>" + weekDays[j] + "</td>");
        			    	for (var i = 0; i < cols; i++) {
        			    		tr.append("<td>" + data.statistics[i].producedDaily[j] + " (" + data.statistics[i].dailyProductionTarget + ")</td>");	
        			    	}
        			    	$('table').append(tr);
        			    }
        			    var total = $('<tr/>');
        			    total.append('<th>Totalt:</th>');
        			    for (var i = 0; i < data.statistics.length; i++) {
        			    	total.append("<td>" + data.statistics[i].producedWeekTotal + "</td>");	
        			    }
        			    $('table').append(total);
        			});
        });
        
        function changeName(elem) {
        	$.getJSON("http://localhost:8080/api/renameMachine?oldName=" + elem.innerHTML + "&newName=second");
        }
  