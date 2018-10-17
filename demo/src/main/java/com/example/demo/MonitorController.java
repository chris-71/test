package com.example.demo;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.model.ProductionUnit;
import com.example.demo.model.Record;
import com.example.demo.repository.MonitorRepository;

@RestController
public class MonitorController {

	@Autowired
	private MonitorRepository service;

	@GetMapping("/testAddRecord")
	@ResponseBody
	public String addRecords(@RequestParam(name = "unitName", required = true) String name) {

		ProductionUnit unit = this.service.findByName(name);
		int count = unit.getRecords().size();
		unit.addRecord(new Record(unit, LocalDateTime.now(), 200));
		this.service.save(unit);

		int newCount = this.service.findByName(name).getRecords().size();

		return "Added " + (newCount - count) + " record/s. Total count is: " + newCount;
	}
}
