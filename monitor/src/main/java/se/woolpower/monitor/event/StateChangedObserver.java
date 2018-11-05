package se.woolpower.monitor.event;

import java.time.LocalDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lombok.Getter;

@Getter
public class StateChangedObserver extends AbstractObserver {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	private int nbrOfAddresses;

	public StateChangedObserver(StateEventHandler subject, Integer nbrOfAddresses) {
		this.nbrOfAddresses = nbrOfAddresses;
		this.subject = subject;
		this.subject.attach(this);
	}

	@Override
	public int getQueueSize() {
		return this.changeQueue.size();
	}

	@Override
	public void update() {
		this.changeQueue.add(new State(this.subject.getState(), LocalDateTime.now()));
		this.logger.debug("State changed, db will be updated. Size: " + this.changeQueue.size());
	}

	@Override
	public State popQueue() {
		State state = this.changeQueue.poll();

		if (state != null) {
			state.truncateBitPattern(this.nbrOfAddresses);
		}

		return state;
	}

}
