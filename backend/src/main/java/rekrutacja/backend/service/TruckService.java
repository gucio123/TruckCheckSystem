package rekrutacja.backend.service;

import jakarta.annotation.PostConstruct;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import rekrutacja.backend.model.Truck;
import rekrutacja.backend.model.TruckStatus;

import java.time.Duration;
import java.time.Instant;
import java.util.*;

@Service
public class TruckService {
    private final Set<Truck> trucks = new HashSet<>();
    private final Queue<Truck> queue1 = new LinkedList<>();
    private final Queue<Truck> queue2 = new LinkedList<>();
    private Long nextId = 1L;
    private Instant startTime;
    private Instant endTime;
    private final Random random = new Random();



    @PostConstruct
    public void init() {
        for (int i = 0; i < 15; i++) {
            int weight = 5 + i % 16;
            Truck truck = new Truck(nextId++, weight, weight, TruckStatus.ARRIVED);
            trucks.add(truck);
        }
        int count = 0;
        for (Truck truck : trucks) {
            if (count < 6) {
                queue1.add(truck);
                truck.setStatus(TruckStatus.IN_QUEUE);
            } else if (count < 12) {
                queue2.add(truck);
                truck.setStatus(TruckStatus.IN_QUEUE);
            }
            count++;
        }
        startTime = Instant.now();
    }

    public synchronized Truck arrive() {
        int weight = random.nextInt(16) + 5;
        Truck truck = new Truck(nextId++, weight, weight, TruckStatus.ARRIVED);
        trucks.add(truck);
        assignToQueue(truck);
        return truck;
    }

    synchronized void assignToQueue(Truck truck) {
        if (queue1.size() < 6) {
            queue1.add(truck);
            truck.setStatus(TruckStatus.IN_QUEUE);
        } else if (queue2.size() < 6) {
            queue2.add(truck);
            truck.setStatus(TruckStatus.IN_QUEUE);
        }
        optimizeQueues();
    }

    private synchronized void optimizeQueues() {
        List<Truck> list1 = new ArrayList<>(queue1);
        List<Truck> list2 = new ArrayList<>(queue2);


        balanceQueues(list1, list2);

        for (int i = 2; i < Math.min(list1.size(), list2.size()); i++) {
            if (i < 6) {
                int sumWaitingTimeQueue1 = 0;
                int sumWaitingTimeQueue2 = 0;

                for (int j = 0; j < i; j++) {
                    sumWaitingTimeQueue1 += list1.get(j).getWaitingTime();
                    sumWaitingTimeQueue2 += list2.get(j).getWaitingTime();
                }

                Truck truck1 = list1.get(i);
                Truck truck2 = list2.get(i);

                if (sumWaitingTimeQueue1 > sumWaitingTimeQueue2 && truck1.getWaitingTime() > truck2.getWaitingTime()) {
                    list1.set(i, truck2);
                    list2.set(i, truck1);
                } else if (sumWaitingTimeQueue1 < sumWaitingTimeQueue2 && truck1.getWaitingTime() < truck2.getWaitingTime()) {
                    list1.set(i, truck2);
                    list2.set(i, truck1);
                }
            }
        }

        queue1.clear();
        queue2.clear();
        queue1.addAll(list1);
        queue2.addAll(list2);

    }

    private void balanceQueues(List<Truck> list1, List<Truck> list2) {
        while (Math.abs(list1.size() - list2.size()) > 2) {
            if (list1.size() > list2.size()) {
                Truck truck = list1.remove(list1.size() - 1);
                list2.add(truck);
            } else {
                Truck truck = list2.remove(list2.size() - 1);
                list1.add(truck);
            }
        }
    }

    public synchronized Map<String, Object> status() {
        Map<String, Object> status = new HashMap<>();
        status.put("queue1", new ArrayList<>(queue1));
        status.put("queue2", new ArrayList<>(queue2));
        status.put("trucks", new ArrayList<>(trucks));
        return status;
    }

    @Scheduled(fixedRate = 1000)
    public synchronized void step() {
        processQueue(queue1);
        processQueue(queue2);
        fillQueues();

        if (queue1.isEmpty() && queue2.isEmpty()) {
            if (endTime == null) {
                endTime = Instant.now();
                Duration duration = Duration.between(startTime, endTime);
            }
        }
    }

    private void fillQueues() {
        while (queue1.size() < 6) {
            Truck nextTruck = trucks.stream()
                    .filter(t -> t.getStatus() == TruckStatus.ARRIVED)
                    .min(Comparator.comparing(Truck::getId))
                    .orElse(null);
            if (nextTruck != null) {
                nextTruck.setStatus(TruckStatus.IN_QUEUE);
                queue1.add(nextTruck);
            } else {
                break;
            }
        }
        while (queue2.size() < 6) {
            Truck nextTruck = trucks.stream()
                    .filter(t -> t.getStatus() == TruckStatus.ARRIVED)
                    .min(Comparator.comparing(Truck::getId))
                    .orElse(null);
            if (nextTruck != null) {
                nextTruck.setStatus(TruckStatus.IN_QUEUE);
                queue2.add(nextTruck);
            } else {
                break;
            }
        }
        optimizeQueues();
    }

    private void processQueue(Queue<Truck> queue) {
        if (!queue.isEmpty()) {
            Truck truck = queue.peek();
            if (truck != null && truck.getStatus() == TruckStatus.CHECKING) {
                truck.setWaitingTime(truck.getWaitingTime() - 1);
                if (truck.getWaitingTime() <= 0) {
                    truck.setStatus(TruckStatus.CHECKED);
                    queue.poll();
                }
            } else if (truck != null && truck.getStatus() == TruckStatus.IN_QUEUE) {
                startChecking(truck.getId());
            }
        }
    }

    public synchronized int waitingTime(Long id) {
        return trucks.stream()
                .filter(truck -> truck.getId().equals(id))
                .map(Truck::getWaitingTime)
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Truck not found"));
    }

    public synchronized void startChecking(Long id) {
        Truck truck = trucks.stream()
                .filter(t -> t.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Truck not found"));
        if (truck.getStatus() == TruckStatus.IN_QUEUE) {
            truck.setStatus(TruckStatus.CHECKING);
            truck.setWaitingTime(truck.getWeight());
        }
    }

    public synchronized int calculateEstimatedWaitTime(Long id) {
        Optional<Truck> truckOpt = trucks.stream().filter(truck -> truck.getId().equals(id)).findFirst();
        if (!truckOpt.isPresent()) {
            throw new RuntimeException("Truck not found");
        }

        Truck truck = truckOpt.get();
        int estimatedTime = 0;

        if (truck.getStatus() == TruckStatus.IN_QUEUE || truck.getStatus() == TruckStatus.CHECKING) {
            Queue<Truck> queue = queue1.contains(truck) ? queue1 : queue2;
            for (Truck t : queue) {
                estimatedTime += t.getWaitingTime();
                if (t.equals(truck)) break;
            }
        } else if (truck.getStatus() == TruckStatus.ARRIVED) {
            List<Truck> allTrucks = new ArrayList<>(trucks);
            allTrucks.sort(Comparator.comparing(Truck::getId));

            List<Truck> virtualQueue1 = new LinkedList<>(queue1);
            List<Truck> virtualQueue2 = new LinkedList<>(queue2);

            for (Truck t : allTrucks) {
                if (t.getStatus() == TruckStatus.ARRIVED) {
                    if (virtualQueue1.size() < 6) {
                        virtualQueue1.add(t);
                    } else if (virtualQueue2.size() < 6) {
                        virtualQueue2.add(t);
                    } else {
                        int sumQueue1 = virtualQueue1.stream().mapToInt(Truck::getWaitingTime).sum();
                        int sumQueue2 = virtualQueue2.stream().mapToInt(Truck::getWaitingTime).sum();

                        if (sumQueue1 <= sumQueue2) {
                            virtualQueue1.add(t);
                        } else {
                            virtualQueue2.add(t);
                        }
                    }

                    if (t.equals(truck)) {
                        List<Truck> assignedQueue = virtualQueue1.contains(t) ? virtualQueue1 : virtualQueue2;
                        for (Truck qt : assignedQueue) {
                            estimatedTime += qt.getWaitingTime();
                            if (qt.equals(truck)) break;
                        }
                        break;
                    }
                }
            }
        }

        return estimatedTime;
    }
}

