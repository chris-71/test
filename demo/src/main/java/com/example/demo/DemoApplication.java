package com.example.demo;

import java.net.URL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.jpa.convert.threeten.Jsr310JpaConverters;

import com.example.demo.model.KnittingMachine;
import com.example.demo.repository.KnittingMachineRepository;

//for jsr310 java 8 java.time.*
@EntityScan(basePackageClasses = { DemoApplication.class, Jsr310JpaConverters.class })
@SpringBootApplication
public class DemoApplication {

	private static final Logger log = LoggerFactory.getLogger(DemoApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}

	@Bean
	public CommandLineRunner demo(KnittingMachineRepository repository) {
		return args -> {
			repository.save(new KnittingMachine("first", new URL("http://127.0.0.1"), 1000));
			repository.save(new KnittingMachine("second", new URL("http://127.0.0.1"), 1000));
			repository.save(new KnittingMachine("third", new URL("http://127.0.0.1"), 1000));
			repository.save(new KnittingMachine("fourth", new URL("http://127.0.0.1"), 1000));

			log.info("Entries found with findAll():");
			log.info("-----------------------------");
			for (KnittingMachine m : repository.findAll()) {
				log.info(m.toString());
			}

			log.info("Try add unit with non-unique name");
			try {
				repository.save(new KnittingMachine("fourth", new URL("http://127.0.0.1"), 1000));
			} catch (DataIntegrityViolationException ex) {
				log.info(ex.getMessage());
			}
		};
	}
}
