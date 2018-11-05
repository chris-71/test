package se.woolpower.monitor.model;

import java.util.Collection;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "knitting_machine")
public class KnittingMachine {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
	@GenericGenerator(name = "native", strategy = "native")
	private Long id;

	@Column(unique = true)
	private String name;

	@Column
	private Integer address;

	@Column
	private Integer dailyProductionTarget;

	@OneToMany(fetch = FetchType.EAGER, mappedBy = "knittingMachine", cascade = CascadeType.ALL)
	private Collection<LogRecord> records;

	protected KnittingMachine() {
	}

	public KnittingMachine(String name, Integer address, Integer dailyProductionTarget) {
		this.name = name;
		this.address = address;
		this.dailyProductionTarget = dailyProductionTarget;
	}

	public void addRecord(LogRecord record) {
		this.records.add(record);
	}
}
