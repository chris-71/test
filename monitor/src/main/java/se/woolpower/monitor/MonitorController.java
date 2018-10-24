package se.woolpower.monitor;

import java.time.LocalDate;
import java.time.temporal.TemporalField;
import java.time.temporal.WeekFields;
import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import se.woolpower.monitor.exception.DuplicateEntityException;
import se.woolpower.monitor.exception.EntityNotFoundException;
import se.woolpower.monitor.service.MonitorService;
import se.woolpower.monitor.types.StatisticsResponse;

@RestController
@RequestMapping("/api")
public class MonitorController {

	Logger logger = LoggerFactory.getLogger(MonitorController.class);

	@Autowired
	private MonitorService monitorService;

	@GetMapping(value = "/stats/", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<StatisticsResponse> getWeekDetails(
			@RequestParam(name = "weekNbr", required = false) Integer weekNbr,
			@RequestParam(name = "year", required = false) Integer year) {

		// If set, create LocalDate of the requested year. Else use current year.
		LocalDate localDate = LocalDate.now();
		if (year != null) {
			localDate = localDate.withYear(year);
		}
		// If set, change week to the requested week number.
		if (weekNbr != null) {
			TemporalField woy = WeekFields.of(Locale.getDefault()).weekOfWeekBasedYear();
			int currWeekNbr = localDate.get(woy);
			int weeksToAdd = weekNbr - currWeekNbr;
			localDate = localDate.plusWeeks(weeksToAdd);
		}

		try {
			StatisticsResponse response = this.monitorService.getResult(localDate);
			return new ResponseEntity<>(response, HttpStatus.OK);
		} catch (Exception e) {
			this.logger.error(e.getMessage());
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);
		}
	}

	@GetMapping("/addMachine")
	public ResponseEntity<Void> addMachine(@RequestParam(name = "machineName", required = true) String name,
			@RequestParam(name = "url", required = true) String url,
			@RequestParam(name = "dailyProductionTarget", required = false) Integer dailyProductionTarget,
			@RequestParam(name = "counterMax", required = true) Integer counterMax) {

		try {
			this.monitorService.addMachine(name, url, dailyProductionTarget, counterMax);
		} catch (DuplicateEntityException e) {
			this.logger.error(e.getMessage());
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@GetMapping("/renameMachine")
	public ResponseEntity<Void> renameMachine(@RequestParam(name = "oldName", required = true) String oldName,
			@RequestParam(name = "newName", required = true) String newName) {

		try {
			this.monitorService.renameMachine(oldName, newName);
		} catch (DuplicateEntityException e) {
			this.logger.error(e.getMessage());
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (EntityNotFoundException ex) {
			this.logger.error(ex.getMessage());
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return new ResponseEntity<>(HttpStatus.OK);
	}

}
