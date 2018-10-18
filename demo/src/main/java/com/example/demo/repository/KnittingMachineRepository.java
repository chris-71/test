package com.example.demo.repository;

import org.springframework.data.repository.CrudRepository;

import com.example.demo.model.KnittingMachine;

public interface KnittingMachineRepository extends CrudRepository<KnittingMachine, Long> {

	KnittingMachine findByName(String machineName);
}
