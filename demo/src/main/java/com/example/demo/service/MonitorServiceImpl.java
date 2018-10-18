package com.example.demo.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.model.KnittingMachine;
import com.example.demo.model.LogRecord;
import com.example.demo.repository.KnittingMachineRepository;
import com.example.demo.repository.LogRecordRepository;

import lombok.Data;

@Data
@Service
public class MonitorServiceImpl implements MonitorService {

	@Autowired
	KnittingMachineRepository knittingMachineRepository;

	@Autowired
	LogRecordRepository logRecordRepository;

	@Override
	public Integer getNumberOfUnitsProducedBetween(String machineName, LocalDateTime from, LocalDateTime to) {

		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
		KnittingMachine machine = this.knittingMachineRepository.findByName(machineName);

		LocalDateTime minDateTime = LocalDateTime.parse("2000-01-01 00:00:00", formatter);
		LocalDateTime maxDateTime = LocalDateTime.now();

		from = (from == null) ? minDateTime : from;
		to = (to == null) ? maxDateTime : to;

		return this.logRecordRepository.findAllByKnittingMachineIdAndLoggedAtBetween(machine.getId(), from, to).stream()
				.mapToInt(i -> i.getCount()).sum();
	}

	@Override
	public void addRecord(String machineName, Integer count) {
		KnittingMachine machine = this.knittingMachineRepository.findByName(machineName);
		machine.addRecord(new LogRecord(machine, count));
		this.knittingMachineRepository.save(machine);
	}

	@Override
	public void pollMachines() {
		// TODO Auto-generated method stub

	}

}
