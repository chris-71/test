package se.woolpower.monitor.repository;

import java.util.Collection;

import org.springframework.data.repository.CrudRepository;

import se.woolpower.monitor.model.KnittingMachine;

public interface KnittingMachineRepository extends CrudRepository<KnittingMachine, Long> {

	Collection<KnittingMachine> findByName(String machineName);
}
