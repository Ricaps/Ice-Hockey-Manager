package cz.fi.muni.pa165.worldlistservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableTransactionManagement
public class WorldListServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(WorldListServiceApplication.class, args);
	}

}
