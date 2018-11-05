package se.woolpower.monitor.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.repository.CrudRepository;

import se.woolpower.monitor.model.LogRecord;

public interface LogRecordRepository extends CrudRepository<LogRecord, Long> {

	List<LogRecord> findAllByKnittingMachineIdAndLoggedAtBetweenOrderByLoggedAtAsc(Long id, LocalDateTime from,
			LocalDateTime to);

	LogRecord findTopByKnittingMachineIdAndLoggedAtBeforeOrderByLoggedAtDesc(Long id, LocalDateTime date);

}
