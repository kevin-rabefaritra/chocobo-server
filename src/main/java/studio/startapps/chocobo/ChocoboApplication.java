package studio.startapps.chocobo;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import studio.startapps.chocobo.file.FileStorageService;

@SpringBootApplication
@EnableScheduling
public class ChocoboApplication implements CommandLineRunner {

	public static void main(String[] args) {
		SpringApplication.run(ChocoboApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		FileStorageService.initDirs();
	}
}
