package se.woolpower.monitor.event;

import java.util.ArrayList;
import java.util.List;

import io.netty.buffer.ByteBuf;

public class StateEventHandler {

	private List<AbstractObserver> observers = new ArrayList<>();

	private ByteBuf bitPattern;

	public ByteBuf getState() {
		return this.bitPattern;
	}

	// Only alter state if it actually changed
	public void triggerEvent(ByteBuf bitPattern) {
		if (bitPattern != null && !bitPattern.equals(this.bitPattern)) {
			this.bitPattern = bitPattern;
			notifyAllObservers();
		}
	}

	public void attach(AbstractObserver observer) {
		this.observers.add(observer);
	}

	public void notifyAllObservers() {
		for (AbstractObserver observer : this.observers) {
			observer.update();
		}
	}
}
