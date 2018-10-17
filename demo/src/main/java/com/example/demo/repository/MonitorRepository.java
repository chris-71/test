package com.example.demo.repository;

import org.springframework.data.repository.CrudRepository;

import com.example.demo.model.ProductionUnit;

public interface MonitorRepository extends CrudRepository<ProductionUnit, Long> {

	ProductionUnit findByName(String name);

}
