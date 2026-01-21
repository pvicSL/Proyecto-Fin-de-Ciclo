package proyecto;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;


@SpringBootApplication
@EnableScheduling
public class ProyectoEstudioTatuajeApplication {

	public static void main(String[] args) {
		SpringApplication.run(ProyectoEstudioTatuajeApplication.class, args);
	}

}
