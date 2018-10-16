package com.example.demo;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class MonitorController {
	
	    @GetMapping("/uptime")
	    @ResponseBody
	    public MonitorResponse getUptime(@RequestParam(name="id", required = true) long id) {
	    	return new MonitorResponse(id);
	}
}
