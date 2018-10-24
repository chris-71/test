package com.example.demo.types;

import java.util.ArrayList;
import java.util.Collection;

import lombok.Data;

/*
 * Generates response for presenting a weekly overview in GUI
 */

@Data
public class StatisticsResponse {

	private Integer weekNbr;

	private Collection<MachineStats> statistics;

	public StatisticsResponse(Integer weekNbr) {
		this.weekNbr = weekNbr;
		this.statistics = new ArrayList<>();
	}

}
