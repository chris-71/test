package se.woolpower.monitor.service;

import se.woolpower.monitor.event.StateEventHandler;

public interface ModBusService {

	/**
	 * @param eventHandler
	 */
	void start(StateEventHandler subject);

	void stop();

}
