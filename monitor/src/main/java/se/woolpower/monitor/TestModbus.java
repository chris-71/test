package se.woolpower.monitor;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.digitalpetri.modbus.codec.Modbus;
import com.digitalpetri.modbus.master.ModbusTcpMaster;
import com.digitalpetri.modbus.master.ModbusTcpMasterConfig;
import com.digitalpetri.modbus.requests.ReadCoilsRequest;
import com.digitalpetri.modbus.responses.ReadCoilsResponse;

import io.netty.handler.codec.base64.Base64;

public class TestModbus {

	public static void main(String[] args) throws InterruptedException {
		new TestModbus(2, 2).start();
	}

	private final Logger logger = LoggerFactory.getLogger(getClass());

	private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

	private final List<ModbusTcpMaster> masters = new CopyOnWriteArrayList<>();
	private volatile boolean started = false;

	private final int nMasters;
	private final int nRequests;

	public TestModbus(int nMasters, int nRequests) {
		this.nMasters = nMasters;
		this.nRequests = nRequests;
	}

	public void start() {
		this.started = true;

		ModbusTcpMasterConfig config = new ModbusTcpMasterConfig.Builder("localhost").setPort(502).build();

		new Thread(() -> {
			while (this.started) {
				try {
					Thread.sleep(5000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

				double mean = 0.0;
				double oneMinute = 0.0;

				for (ModbusTcpMaster master : this.masters) {
					mean += master.getResponseTimer().getMeanRate();
					oneMinute += master.getResponseTimer().getOneMinuteRate();
				}

				this.logger.info("Mean rate={}, 1m rate={}", mean, oneMinute);
			}
		}).start();

		for (int i = 0; i < this.nMasters; i++) {
			ModbusTcpMaster master = new ModbusTcpMaster(config);
			this.masters.add(master);

			for (int j = 0; j < this.nRequests; j++) {
				sendAndReceive(master);
			}
		}

	}

	private void sendAndReceive(ModbusTcpMaster master) {
		if (!this.started) {
			return;
		}

		CompletableFuture<ReadCoilsResponse> future = master.sendRequest(new ReadCoilsRequest(0, 10), 1);
//		CompletableFuture<ReadHoldingRegistersResponse> future = master
//				.sendRequest(new ReadHoldingRegistersRequest(40001, 10), 0);

		future.whenCompleteAsync((response, ex) -> {
			if (response != null) {
				this.logger.info(Base64.encode(response.getCoilStatus()).toString(StandardCharsets.UTF_8));
				// ReferenceCountUtil.release(response);
			} else {
				this.logger.error("Completed exceptionally, message={}", ex.getMessage(), ex);
			}
			this.scheduler.schedule(() -> sendAndReceive(master), 1, TimeUnit.SECONDS);
		}, Modbus.sharedExecutor());
	}

	public void stop() {
		this.started = false;
		this.masters.forEach(ModbusTcpMaster::disconnect);
		this.masters.clear();
	}

}
