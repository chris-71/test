package se.woolpower.monitor.event;

import java.util.concurrent.ConcurrentLinkedQueue;

public abstract class AbstractObserver {
	protected StateEventHandler subject;
	protected ConcurrentLinkedQueue<State> changeQueue = new ConcurrentLinkedQueue<>();

	public abstract void update();

	public abstract int getQueueSize();

	public abstract State popQueue();
}
