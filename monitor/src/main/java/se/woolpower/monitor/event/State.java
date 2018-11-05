package se.woolpower.monitor.event;

import java.time.LocalDateTime;

import io.netty.buffer.ByteBuf;
import lombok.Data;

/*
 * Represent a state with bitpattern for all attached
 * sensors and a time when the pattern changed.
 */

@Data
public class State {
	ByteBuf bitPattern;
	String bitPatternAsString;
	LocalDateTime dateTime;

	// Test
	public State(String s, LocalDateTime dt) {
		this.dateTime = dt;
		this.bitPatternAsString = s;
	}

	public State(ByteBuf bitPattern, LocalDateTime dateTime) {
		this.bitPattern = bitPattern;
		this.dateTime = dateTime;
		this.bitPatternAsString = "";

		for (int i = 0; i < bitPattern.capacity(); i++) {
			this.bitPatternAsString += convertByteToStr(bitPattern.getByte(i));
		}
	}

	private String convertByteToStr(byte b) {
		return new StringBuilder(String.format("%8s", Integer.toBinaryString(b & 0xFF)).replace(' ', '0')).reverse()
				.toString();
	}

	public void truncateBitPattern(int nbrOfAddresses) {
		this.bitPatternAsString = this.bitPatternAsString.substring(0,
				Math.min(this.bitPatternAsString.length(), nbrOfAddresses));

	}

}
