package com.example.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.service.MonitorService;

@RestController
public class MonitorController {

	@Autowired
	private MonitorService monitorService;

	@GetMapping("/testAddRecord")
	@ResponseBody
	public ResponseEntity<Integer> addRecord(@RequestParam(name = "machineName", required = true) String name,
			@RequestParam(name = "unitCount", required = true) Integer count) {

		this.monitorService.addRecord(name, count);

		Integer totalCount;
		try {
			// From beginning of time
			totalCount = this.monitorService.getNumberOfUnitsProducedBetween(name, null, null);
		} catch (Exception e) {
			return new ResponseEntity<>(HttpStatus.SERVICE_UNAVAILABLE);
		}

		return new ResponseEntity<>(totalCount, HttpStatus.OK);

	}
}
