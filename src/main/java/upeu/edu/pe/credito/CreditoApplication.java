package upeu.edu.pe.credito;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class CreditoApplication {

	public static void main(String[] args) {
		SpringApplication.run(CreditoApplication.class, args);
	}

}
