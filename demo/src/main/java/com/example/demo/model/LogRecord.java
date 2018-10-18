package com.example.demo.model;

import java.time.LocalDateTime;

import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.example.demo.repository.LocalDateTimeConverter;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "log_record")
public class LogRecord {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
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
