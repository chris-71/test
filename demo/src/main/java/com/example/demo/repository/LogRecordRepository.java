package com.example.demo.repository;

import java.time.LocalDateTime;
import java.util.Collection;

import org.springframework.data.repository.CrudRepository;

import com.example.demo.model.LogRecord;

public interface LogRecordRepository extends CrudRepository<LogRecord, Long> {

	Collection<LogRecord> findAllByKnittingMachineIdAndLoggedAtBetween(Long machineId, LocalDateTime from,
			LocalDateTime to);
}
