package se.woolpower.monitor.service;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.annotation.PreDestroy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.digitalpetri.modbus.codec.Modbus;
import com.digitalpetri.modbus.master.ModbusTcpMaster;
import com.digitalpetri.modbus.requests.ReadCoilsRequest;
import com.digitalpetri.modbus.responses.ReadCoilsResponse;

import lombok.Data;
import se.woolpower.monitor.event.StateEventHandler;

@Data
@Service
public class ModBusServiceImpl implements ModBusService {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

	private final ModbusTcpMaster modbusMaster;

	private volatile boolean run = false;

	@Value("${modbus.master.register-address}")
	private int registerAddress;

	@Value("${modbus.master.address-quantity}")
	private int addressQuantity;

	@Value("${modbus.master.slave-unit-id}")
	private int slaveUnitId;

	StateEventHandler eventHandler;

	@Autowired
	public ModBusServiceImpl(ModbusTcpMaster modbusMaster) {
		this.modbusMaster = modbusMaster;
		this.run = true;
	}

	@Override
	public void start(StateEventHandler eventHandler) {

		this.eventHandler = eventHandler;

		new Thread(() -> {
			while (this.run) {
				try {
					Thread.sleep(5000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}).start();

		sendAndReceive();
	}

	private void sendAndReceive() {
		if (!this.run) {
			return;
		}

		CompletableFuture<ReadCoilsResponse> future = this.modbusMaster
				.sendRequest(new ReadCoilsRequest(this.registerAddress, this.addressQuantity), this.slaveUnitId);
		future.whenCompleteAsync((response, ex) -> {
			if (response != null) {
				this.eventHandler.triggerEvent(response.getCoilStatus());
			} else {
				this.logger.error("Completed exceptionally, message={}", ex.getMessage(), ex);
			}
			// response.release();
			this.scheduler.schedule(() -> sendAndReceive(), 500, TimeUnit.MILLISECONDS);
		}, Modbus.sharedExecutor());
	}

	@PreDestroy
	@Override
	public void stop() {
		this.run = false;
		this.modbusMaster.disconnect();
	}

}
