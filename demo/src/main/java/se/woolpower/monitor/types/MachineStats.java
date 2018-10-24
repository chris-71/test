package com.example.demo.types;

import java.util.Collection;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MachineStats {

	private Long id;

	private String name;

	private Integer dailyProductionTarget;

	private Collection<Integer> producedDaily;

	private Integer producedWeekTotal;
}
