package se.woolpower.monitor.service;

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
import java.util.HashMap;
import java.util.Locale;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import lombok.Data;
import se.woolpower.monitor.event.AbstractObserver;
import se.woolpower.monitor.event.State;
import se.woolpower.monitor.event.StateChangedObserver;
import se.woolpower.monitor.event.StateEventHandler;
import se.woolpower.monitor.exception.DuplicateEntityException;
import se.woolpower.monitor.exception.EntityNotFoundException;
import se.woolpower.monitor.model.KnittingMachine;
import se.woolpower.monitor.model.LogRecord;
import se.woolpower.monitor.repository.KnittingMachineRepository;
import se.woolpower.monitor.repository.LogRecordRepository;
import se.woolpower.monitor.types.MachineStats;
import se.woolpower.monitor.types.StatisticsResponse;

@Data
@Service
public class MonitorServiceImpl implements MonitorService {

	@Autowired
	KnittingMachineRepository knittingMachineRepository;

	@Autowired
	LogRecordRepository logRecordRepository;

	@Autowired
	ModBusService modbusService;

	Logger logger = LoggerFactory.getLogger(MonitorServiceImpl.class);
	StateEventHandler subject = new StateEventHandler();
	HashMap<Integer, Boolean> currentMachineStatus = new HashMap<>();
	AbstractObserver observer = null;

	@PostConstruct
	public void init() {
		initMachineStatus();
		this.observer = new StateChangedObserver(this.subject, this.currentMachineStatus.size());
		this.modbusService.start(this.subject);

	}

	private void initMachineStatus() {
		for (KnittingMachine km : this.knittingMachineRepository.findAll()) {
			this.currentMachineStatus.put(km.getAddress(), false);
		}
	}

	@Override
	public void addMachine(String machineName, Integer address, Integer dailyProductionTarget)
			throws DuplicateEntityException {
		try {
			getMachine(machineName); // Should throw EntityNotFoundException
			throw new DuplicateEntityException("Machine with name " + machineName + " already exists.");
		} catch (EntityNotFoundException e) { // Not found, safe to create
			KnittingMachine knittingMachine = new KnittingMachine(machineName, address, dailyProductionTarget);
			this.knittingMachineRepository.save(knittingMachine);
			this.currentMachineStatus.put(address, false);
		}
	}

	@Override
	public void renameMachine(String oldName, String newName) throws DuplicateEntityException, EntityNotFoundException {

		KnittingMachine knittingMachine;
		try {
			knittingMachine = getMachine(oldName);
		} catch (EntityNotFoundException e) {
			this.logger.error(e.getMessage());
			throw e;
		}

		try {
			getMachine(newName); // Should throw EntityNotFoundException
			throw new DuplicateEntityException("Machine with name " + newName + " already exists.");
		} catch (EntityNotFoundException e) { // Not found, safe to rename
			knittingMachine.setName(newName);
			this.knittingMachineRepository.save(knittingMachine);
			this.logger.info("Machine with id: " + knittingMachine.getId() + " changed name from '" + oldName + "' to '"
					+ newName + "'");
		}
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

			Collection<Integer> producedDay = getProducedUnitsWeekly(machineId, startOfDay);
			Integer currentWeekTotal = getNumberOfUnitsProducedBetween(machineId, startOfWeek, endOfWeek);
			MachineStats stats = new MachineStats(machineId, km.getName(), km.getDailyProductionTarget(), producedDay,
					currentWeekTotal);
			response.getStatistics().add(stats);
		}
		return response;
	}

	@Override
	@Scheduled(fixedRate = 10000)
	public void processQueue() {
		State state = this.observer.popQueue();

		while (state != null) { // Null when queue empty
			updateDb(state);
			state = this.observer.popQueue();
		}
	}

	private void updateDb(State state) {
		String pattern = state.getBitPatternAsString();

		for (int i = 0; i < pattern.length(); i++) {
			int address = i;
			Boolean status = new Boolean(pattern.charAt(i) == '1');

			if (!status.equals(this.currentMachineStatus.get(address))) { // Status has changed

				// update db
				KnittingMachine km = this.knittingMachineRepository.findTopByAddress(address);
				LogRecord rec = new LogRecord(km, 1);
				km.addRecord(rec);// new LogRecord(km, 1));
				this.knittingMachineRepository.save(km);

				/*
				 * get machine with address address increment count save object
				 */

				// update current status
				this.currentMachineStatus.replace(address, status);
				this.logger.debug("Machine with address: " + address + " changed state to: " + status + " at "
						+ state.getDateTime());

			}
		}
	}

//	private void incrementCount(HashMap<Integer, Boolean> records) {
//		Set<Integer> resultingKeyset = new HashSet<>();
//
//		for (Integer address : this.currentMachineStatus.keySet()) {
//			if (!this.currentMachineStatus.get(address).equals(records.get(address))) {
//				resultingKeyset.add(address);
//			}
//		}
//
//		for (Integer address : resultingKeyset) {
//
//			Update object in db
//			Update currentMachineStatus
//			Need to change behaviour? Now the timestamp will only be updated each time this method runs
//			System.out.println("Machine on address: " + address + " needs update");
//		}
//
//	}

	private KnittingMachine getMachine(String machineName) throws EntityNotFoundException {
		return this.knittingMachineRepository.findByName(machineName).stream().findFirst()
				.orElseThrow(() -> new EntityNotFoundException("Machine with name " + machineName + "not found."));
	}

	private KnittingMachine getMachine(Long machineId) throws EntityNotFoundException {
		return this.knittingMachineRepository.findById(machineId)
				.orElseThrow(() -> new EntityNotFoundException("Machine with id " + machineId + " not found."));
	}

	private Collection<Integer> getProducedUnitsWeekly(Long machineId, LocalDateTime date) {

		DayOfWeek firstDayOfWeek = WeekFields.of(Locale.getDefault()).getFirstDayOfWeek();
		LocalDateTime startOfDay = date.with(firstDayOfWeek); // First day of week at 00:00
		Collection<Integer> weeklyOverview = new ArrayList<>();

		for (int i = 0; i <= 6; i++) {
			LocalDateTime endOfDay = startOfDay.with(ChronoField.NANO_OF_DAY, LocalTime.MAX.toNanoOfDay());
			weeklyOverview.add(getNumberOfUnitsProducedBetween(machineId, startOfDay, endOfDay));
			startOfDay = startOfDay.plusDays(1L);
		}

		return weeklyOverview;
	}

	private Integer getNumberOfUnitsProducedBetween(Long id, LocalDateTime from, LocalDateTime to) {

		// TODO: Change this depending on what we get from modbus slave. For now we
		// assume that each
		// log record is one produced unit

		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
		LocalDateTime minDateTime = LocalDateTime.parse("2000-01-01 00:00:00", formatter);
		LocalDateTime maxDateTime = LocalDateTime.now();
		from = (from == null) ? minDateTime : from;
		to = (to == null) ? maxDateTime : to;
		Integer lastRecordCount = 0;

		Integer totalCount = this.logRecordRepository
				.findAllByKnittingMachineIdAndLoggedAtBetweenOrderByLoggedAtAsc(id, from, to).size();

		// Fetch the LogRecord preceding the first one (if any) for the from date
		// and use the count as starting point for the calculation of total.
//		LogRecord preceding = this.logRecordRepository
//				.findTopByKnittingMachineIdAndLoggedAtBeforeOrderByLoggedAtDesc(id, from);
//		if (preceding != null) {
//			lastRecordCount = preceding.getCount();
//		}

		// Fetch logrecords for the specified span
//		List<LogRecord> records = this.logRecordRepository
//				.findAllByKnittingMachineIdAndLoggedAtBetweenOrderByLoggedAtAsc(id, from, to);
//
//		for (int i = 0; i < records.size(); i++) {
//			Integer currentRecordCount = records.get(i).getCount();
//			if (i > 0) {
//				lastRecordCount = records.get(i - 1).getCount();
//			}

		// Need to handle counter turnover
//			if (currentRecordCount > lastRecordCount) {
//				totalCount += currentRecordCount - lastRecordCount;
//			} else if (currentRecordCount < lastRecordCount) {
//				totalCount += ((counterMax - lastRecordCount) + currentRecordCount);
//			}
//	}
		return totalCount;

	}

	/**
	 * Adds a record to the database with a number of produced units and associates
	 * the record with a machine
	 *
	 * @param machineId id of the machine
	 * @param count     number of produced units
	 */
	private void addRecord(Long machineId, Integer count) {
		KnittingMachine machine;
		try {
			machine = getMachine(machineId);
		} catch (EntityNotFoundException e) {
			this.logger.error(e.getMessage());
			return;
		}
		machine.addRecord(new LogRecord(machine, count));
		this.knittingMachineRepository.save(machine);
	}
}
