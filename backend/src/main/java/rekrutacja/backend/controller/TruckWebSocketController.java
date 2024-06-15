package rekrutacja.backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;
import rekrutacja.backend.model.Truck;
import rekrutacja.backend.service.TruckService;

import java.util.Map;

@Controller
public class TruckWebSocketController {

    @Autowired
    private TruckService truckService;

    @Autowired
    private SimpMessagingTemplate template;

    @MessageMapping("/status")
    @SendTo("/topic/status")
    public Map<String, Object> getStatus() {
        return truckService.status();
    }

    @Scheduled(fixedRate = 1000)
    public void sendPeriodicUpdates() {
        Map<String, Object> status = truckService.status();
        template.convertAndSend("/topic/status", status);
    }
}
