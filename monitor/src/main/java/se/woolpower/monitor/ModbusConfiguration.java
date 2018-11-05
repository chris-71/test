package se.woolpower.monitor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.digitalpetri.modbus.master.ModbusTcpMaster;
import com.digitalpetri.modbus.master.ModbusTcpMasterConfig;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Configuration
public class ModbusConfiguration {

	@Value("${modbus.master.slave-address}")
	private String slaveAddress;

	@Value("${modbus.master.slave-port}")
	private int slavePort;

	@Bean
	public ModbusTcpMasterConfig modbusConfig() {
		return new ModbusTcpMasterConfig.Builder(this.slaveAddress).setPort(this.slavePort).build();
	}

	@Bean
	public ModbusTcpMaster modbusMaster() {
		return new ModbusTcpMaster(this.modbusConfig());
	}
}
