package com.example.demo;

import java.util.Collection;

import lombok.AllArgsConstructor;
import lombok.Data;

/*
 * Generates response for presenting a weekly overview in GUI
 */

@Data
@AllArgsConstructor
public class StatisticsResponse {

	private Long id;

	private String name;

	private Integer weekNbr;

	private Integer dailyProductionTarget;

	private Collection<Integer> producedDaily;

	private Integer producedWeekTotal;

}
