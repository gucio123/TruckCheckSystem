package rekrutacja.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class KolejkaTirApplication {

	public static void main(String[] args) {
		SpringApplication.run(KolejkaTirApplication.class, args);
	}

}
