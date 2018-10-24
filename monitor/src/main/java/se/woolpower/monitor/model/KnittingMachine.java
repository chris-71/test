package se.woolpower.monitor.model;

import java.util.Collection;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
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
	private String url;

	@Column
	private Integer dailyProductionTarget;

	@Column
	private Integer counterMax;

	@OneToMany(mappedBy = "knittingMachine", cascade = CascadeType.ALL)
	private Collection<LogRecord> records;

	protected KnittingMachine() {
	}

	public KnittingMachine(String name, String url, Integer dailyProductionTarget, Integer counterMax) {
		this.name = name;
		this.url = url;
		this.dailyProductionTarget = dailyProductionTarget;
		this.counterMax = counterMax;
	}

	public void addRecord(LogRecord record) {
		this.records.add(record);
	}
}
