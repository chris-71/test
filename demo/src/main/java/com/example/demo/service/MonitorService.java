package com.example.demo.service;

import java.time.LocalDateTime;

import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

public interface MonitorService {

	Integer getNumberOfUnitsProducedBetween(@NonNull String machineName, @Nullable LocalDateTime from,
			@Nullable LocalDateTime to);

	void addRecord(String machineName, Integer count);

	void pollMachines();

}
