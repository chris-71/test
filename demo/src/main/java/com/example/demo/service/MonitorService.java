package com.example.demo.service;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import com.example.demo.exception.DuplicateEntityException;
import com.example.demo.exception.EntityNotFoundException;
import com.example.demo.types.StatisticsResponse;

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
	 * @param counterMax            maximum value of the counter before it turns
	 *                              over
	 * @throws DuplicateEntityException
	 */
	void addMachine(String machineName, String url, Integer dailyProductionTarget, Integer counterMax)
			throws DuplicateEntityException;

	/**
	 * @param oldName the current name of the machine
	 * @param newName the new name
	 */
	void renameMachine(String oldName, String newName) throws DuplicateEntityException, EntityNotFoundException;

	/**
	 * only for test
	 */
	Integer getNumberOfUnitsProducedBetween(@NonNull String machineName, @Nullable LocalDateTime fromDate,
			@Nullable LocalDateTime toDate) throws EntityNotFoundException;

	/**
	 * Adds a record to the database with a number of produced units and associates
	 * the record with a machine
	 *
	 * @param machineName name of the machine
	 * @param count       number of produced units
	 */
	void addRecord(String machineName, Integer count);

	/**
	 * Fetches data from db and put together statistics for all machines
	 *
	 * @return collection of statistics from each machine
	 */
	StatisticsResponse getResult(LocalDate localDate);

}
