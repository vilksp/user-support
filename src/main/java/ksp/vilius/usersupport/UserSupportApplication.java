package ksp.vilius.usersupport;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.File;

import static ksp.vilius.usersupport.constant.FileConstant.USER_FOLDER;

@SpringBootApplication
public class UserSupportApplication {

	public static void main(String[] args) {
		SpringApplication.run(UserSupportApplication.class, args);
		new File(USER_FOLDER).mkdirs();
	}

}
