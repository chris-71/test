package se.woolpower.monitor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class MonitorApplication {

	private static final Logger log = LoggerFactory.getLogger(MonitorApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(MonitorApplication.class, args);
	}
}
