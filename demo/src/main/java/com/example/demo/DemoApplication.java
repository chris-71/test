package com.example.demo;

import java.net.URL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import com.example.demo.model.ProductionUnit;
import com.example.demo.repository.MonitorRepository;

@SpringBootApplication
public class DemoApplication {

	private static final Logger log = LoggerFactory.getLogger(DemoApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}

	@Bean
	public CommandLineRunner demo(MonitorRepository repository) {
		return (args) -> {
			repository.save(new ProductionUnit(1, "first", new URL("http://127.0.0.1")));
			repository.save(new ProductionUnit(2, "second", new URL("http://127.0.0.1")));
			repository.save(new ProductionUnit(3, "third", new URL("http://127.0.0.1")));
			repository.save(new ProductionUnit(4, "fourth", new URL("http://127.0.0.1")));

			log.info("Entries found with findAll():");
			log.info("-----------------------------");
			for (ProductionUnit m : repository.findAll()) {
				log.info(m.toString());
			}

			log.info("Try add unit with non-unique name");
			repository.save(new ProductionUnit(4, "fourth", new URL("http://127.0.0.1")));
		};
	}
}
