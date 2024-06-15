package rekrutacja.backend.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Truck {

    private Long id;
    private int weight;
    private int waitingTime;
    private TruckStatus status;

}
