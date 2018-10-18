package com.example.demo.model;

import java.net.URL;
import java.util.Collection;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "knitting_machine")
public class KnittingMachine {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@Column(unique = true)
	private String name;

	@Column
	private URL url;

	@Column
	private Integer dailyProductionTarget;

	@OneToMany(mappedBy = "knittingMachine", cascade = CascadeType.ALL)
	private Collection<LogRecord> records;

	protected KnittingMachine() {
	}

	public KnittingMachine(String name, URL url, Integer dailyProductionTarget) {
		this.name = name;
		this.url = url;
		this.dailyProductionTarget = dailyProductionTarget;
	}

	public void addRecord(LogRecord record) {
		this.records.add(record);
	}
}
