package com.example.demo;

import java.time.LocalDateTime;
import java.time.temporal.ChronoField;

import lombok.Data;

@Data
public class MonitorResponse {
	
	private final long id;
	
	private String name;
	private long uptime;
	private int count;
	
	public MonitorResponse(long id) {
		this.id = id;
		this.uptime = LocalDateTime.now().getLong(ChronoField.MILLI_OF_DAY);
	}
}
