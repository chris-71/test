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
@Table(name = "production_unit")
public class ProductionUnit {

	private long id;
	@Column
	private String name;
	@Column
	private URL url;

	@OneToMany()
	private Collection<Record> records;

	protected ProductionUnit() {
	}

	public ProductionUnit(long id, String name, URL url) {
		this.id = id;
		this.name = name;
		this.url = url;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public long getId() {
		return this.id;
	}

	@OneToMany(mappedBy = "productionUnit", cascade = CascadeType.ALL)
	public Collection<Record> getRecords() {
		return this.records;
	}

	public void addRecord(Record record) {
		this.records.add(record);
	}

	@Override
	public String toString() {
		return "Name: " + this.name + " Id: " + this.id;
	}
}
