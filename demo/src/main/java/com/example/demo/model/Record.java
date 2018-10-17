package com.example.demo.model;

import java.time.LocalDateTime;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.Data;

@Data
@Entity
@Table(name = "record")
public class Record {

	private long id;

	private ProductionUnit productionUnit;
	private LocalDateTime timestamp;
	private int count;

	protected Record() {
	}

	public Record(ProductionUnit productionUnit, LocalDateTime timestamp, int count) {
		this.productionUnit = productionUnit;
		this.timestamp = timestamp;
		this.count = count;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public long getId() {
		return this.id;
	}

	@ManyToOne
	@JoinColumn(name = "production_unit.id")
	public ProductionUnit getProductionUnit() {
		return this.productionUnit;
	}

}
