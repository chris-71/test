package com.example.demo.repository;

import java.util.Collection;

import org.springframework.data.repository.CrudRepository;

import com.example.demo.model.KnittingMachine;

public interface KnittingMachineRepository extends CrudRepository<KnittingMachine, Long> {

	Collection<KnittingMachine> findByName(String machineName);
}
