package se.woolpower.monitor.model;

import java.time.LocalDateTime;

import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

import lombok.Getter;
import lombok.Setter;
import se.woolpower.monitor.repository.LocalDateTimeConverter;

@Getter
@Setter
@Entity
@Table(name = "log_record")
public class LogRecord {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
	@GenericGenerator(name = "native", strategy = "native")
	private Long id;

	@ManyToOne
	@JoinColumn(name = "knitting_machine.id")
	private KnittingMachine knittingMachine;

	@Convert(converter = LocalDateTimeConverter.class)
	private LocalDateTime loggedAt;
	private Integer count;

	protected LogRecord() {
	}

	public LogRecord(KnittingMachine knittingMachine, Integer count) {
		this.knittingMachine = knittingMachine;
		this.loggedAt = LocalDateTime.now();
		this.count = count;
	}
}
