package rekrutacja.backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;
import rekrutacja.backend.model.Truck;
import rekrutacja.backend.service.TruckService;

import java.util.Map;

@RestController
@RequestMapping("/api/trucks")
@CrossOrigin(origins = "http://localhost:4200")
public class TruckController {
    @Autowired
    private TruckService truckService;

    @PostMapping("/arrive")
    public ResponseEntity<Truck> arrive() {
        return ResponseEntity.ok(truckService.arrive());
    }

    @GetMapping("/status")
    public ResponseEntity<Map<String, Object>> status() {
        return ResponseEntity.ok(truckService.status());
    }

    @PostMapping("/step")
    public ResponseEntity<Void> step() {
        truckService.step();
        return ResponseEntity.ok().build();
    }

    @GetMapping("/waitingTime/{id}")
    public ResponseEntity<Integer> waitingTime(@PathVariable Long id) {
        return ResponseEntity.ok(truckService.waitingTime(id));
    }

    @PostMapping("/startChecking/{id}")
    public ResponseEntity<Void> startChecking(@PathVariable Long id) {
        truckService.startChecking(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/estimatedWaitTime/{id}")
    public ResponseEntity<Integer> getEstimatedWaitTime(@PathVariable String id) {
        int estimatedWaitTime = truckService.calculateEstimatedWaitTime(Long.valueOf(id));
        return ResponseEntity.ok(estimatedWaitTime);
    }
    @ExceptionHandler(RuntimeException.class)
    public final ResponseEntity<String> handleTruckNotFoundException(RuntimeException ex, WebRequest request) {
        return ResponseEntity.badRequest().body(ex.getMessage());
    }
}
