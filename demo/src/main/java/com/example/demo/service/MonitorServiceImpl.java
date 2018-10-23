package com.example.demo.service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoField;
import java.time.temporal.TemporalAdjusters;
import java.time.temporal.TemporalField;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.exception.DuplicateEntityException;
import com.example.demo.exception.EntityNotFoundException;
import com.example.demo.model.KnittingMachine;
import com.example.demo.model.LogRecord;
import com.example.demo.repository.KnittingMachineRepository;
import com.example.demo.repository.LogRecordRepository;
import com.example.demo.types.MachineStats;
import com.example.demo.types.StatisticsResponse;

import lombok.Data;

@Data
@Service
public class MonitorServiceImpl implements MonitorService {

	@Autowired
	KnittingMachineRepository knittingMachineRepository;

	@Autowired
	LogRecordRepository logRecordRepository;

	@Override
	public void addMachine(String machineName, String url, Integer dailyProductionTarget, Integer counterMax)
			throws DuplicateEntityException {
		try {
			getMachine(machineName); // Should throw EntityNotFoundException
			throw new DuplicateEntityException("Machine with name " + machineName + " already exists.");
		} catch (EntityNotFoundException e) { // Not found, safe to create
			KnittingMachine knittingMachine = new KnittingMachine(machineName, url, dailyProductionTarget, counterMax);
			this.knittingMachineRepository.save(knittingMachine);
		}
	}

	@Override
	public void renameMachine(String oldName, String newName) throws DuplicateEntityException, EntityNotFoundException {

		KnittingMachine knittingMachine;
		try {
			knittingMachine = getMachine(oldName);
		} catch (EntityNotFoundException e) {
			throw e;
		}

		try {
			getMachine(newName); // Should throw EntityNotFoundException
			throw new DuplicateEntityException("Machine with name " + newName + " already exists.");
		} catch (EntityNotFoundException e) { // Not found, safe to rename
			knittingMachine.setName(newName);
			this.knittingMachineRepository.save(knittingMachine);
		}
	}

	// TODO: Test only
	@Override
	public Integer getNumberOfUnitsProducedBetween(String name, LocalDateTime from, LocalDateTime to) {
		KnittingMachine km = null;
		try {
			km = getMachine(name);
		} catch (EntityNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return getNumberOfUnitsProducedBetween(km.getId(), new Integer(100), from, to);

	}

	private Collection<Integer> getProducedUnitsWeekly(Long machineId, Integer counterMax, LocalDateTime date) {

		DayOfWeek firstDayOfWeek = WeekFields.of(Locale.getDefault()).getFirstDayOfWeek();
		LocalDateTime startOfDay = date.with(firstDayOfWeek); // First day of week at 00:00
		Collection<Integer> weeklyOverview = new ArrayList<>();

		for (int i = 0; i <= 6; i++) {
			LocalDateTime endOfDay = startOfDay.with(ChronoField.NANO_OF_DAY, LocalTime.MAX.toNanoOfDay());
			weeklyOverview.add(getNumberOfUnitsProducedBetween(machineId, counterMax, startOfDay, endOfDay));
			startOfDay = startOfDay.plusDays(1L);
		}

		return weeklyOverview;
	}

	private Integer getNumberOfUnitsProducedBetween(Long id, Integer counterMax, LocalDateTime from, LocalDateTime to) {

		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
		LocalDateTime minDateTime = LocalDateTime.parse("2000-01-01 00:00:00", formatter);
		LocalDateTime maxDateTime = LocalDateTime.now();
		from = (from == null) ? minDateTime : from;
		to = (to == null) ? maxDateTime : to;
		Integer totalCount = 0;
		Integer lastRecordCount = 0;

		// Fetch the LogRecord preceding the first one (if any) for the from date
		// and use the count as starting point for the calculation of total.
		LogRecord preceding = this.logRecordRepository
				.findTopByKnittingMachineIdAndLoggedAtBeforeOrderByLoggedAtDesc(id, from);
		if (preceding != null) {
			lastRecordCount = preceding.getCount();
		}

		// Fetch logrecords for the specified span
		List<LogRecord> records = this.logRecordRepository
				.findAllByKnittingMachineIdAndLoggedAtBetweenOrderByLoggedAtAsc(id, from, to);

		for (int i = 0; i < records.size(); i++) {
			Integer currentRecordCount = records.get(i).getCount();
			if (i > 0) {
				lastRecordCount = records.get(i - 1).getCount();
			}

			// Need to handle counter turnover
			if (currentRecordCount > lastRecordCount) {
				totalCount += currentRecordCount - lastRecordCount;
			} else if (currentRecordCount < lastRecordCount) {
				totalCount += ((counterMax - lastRecordCount) + currentRecordCount);
			}
		}
		return totalCount;
	}

	@Override
	public void addRecord(String machineName, Integer count) {
		KnittingMachine machine;
		try {
			machine = getMachine(machineName);
		} catch (EntityNotFoundException e) {
			// TODO Auto-generated catch block
			e.getMessage();
			return;
		}
		machine.addRecord(new LogRecord(machine, count));
		this.knittingMachineRepository.save(machine);
	}

	@Override
	public StatisticsResponse getResult(LocalDate localDate) {

		TemporalField woy = WeekFields.of(Locale.getDefault()).weekOfWeekBasedYear();
		int weekNbr = localDate.get(woy);

		LocalDateTime startOfDay = localDate.atStartOfDay();
		LocalDateTime endOfDay = startOfDay.with(ChronoField.NANO_OF_DAY, LocalTime.MAX.toNanoOfDay());

		DayOfWeek firstDayOfWeek = WeekFields.of(Locale.getDefault()).getFirstDayOfWeek();
		DayOfWeek lastDayOfWeek = firstDayOfWeek.plus(6);
		LocalDateTime startOfWeek = startOfDay.with(firstDayOfWeek);
		LocalDateTime endOfWeek = endOfDay.with(TemporalAdjusters.nextOrSame(lastDayOfWeek));

		StatisticsResponse response = new StatisticsResponse(weekNbr);

		for (KnittingMachine km : this.knittingMachineRepository.findAll()) {
			Long machineId = km.getId();
			Integer counterMax = km.getCounterMax();

			Collection<Integer> producedDay = getProducedUnitsWeekly(machineId, counterMax, startOfDay);
			Integer currentWeekTotal = getNumberOfUnitsProducedBetween(machineId, counterMax, startOfWeek, endOfWeek);
			MachineStats stats = new MachineStats(machineId, km.getName(), km.getDailyProductionTarget(), producedDay,
					currentWeekTotal);
			response.getStatistics().add(stats);
//			response.add(new StatisticsResponse(machineId, km.getName(), weekNbr, km.getDailyProductionTarget(),
//					producedDay, currentWeekTotal));
		}
		return response;
	}

	private KnittingMachine getMachine(String machineName) throws EntityNotFoundException {
		return this.knittingMachineRepository.findByName(machineName).stream().findFirst()
				.orElseThrow(() -> new EntityNotFoundException("Machine not found: " + machineName));
	}

}
