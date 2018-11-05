package se.woolpower.monitor.service;

import java.time.LocalDate;

import org.springframework.scheduling.annotation.Async;

import se.woolpower.monitor.exception.DuplicateEntityException;
import se.woolpower.monitor.exception.EntityNotFoundException;
import se.woolpower.monitor.types.StatisticsResponse;

/**
 * @author Christer Jonsson
 *
 */

public interface MonitorService {

	/**
	 * Add a machine that should be monitored
	 *
	 * @param machineName           name of the machine
	 * @param url                   address to the machine
	 * @param dailyProductionTarget the number of units that this machine should
	 *                              produce each day
	 *
	 * @throws DuplicateEntityException
	 */
	void addMachine(String machineName, Integer address, Integer dailyProductionTarget) throws DuplicateEntityException;

	/**
	 * @param oldName the current name of the machine
	 * @param newName the new name
	 */
	void renameMachine(String oldName, String newName) throws DuplicateEntityException, EntityNotFoundException;

	/**
	 * Fetch process change queue and store result in db
	 */
	@Async
	void processQueue();

	/**
	 * Fetches data from db and put together statistics for all machines
	 *
	 * @return collection of statistics from each machine
	 */
	StatisticsResponse getResult(LocalDate localDate);

}
