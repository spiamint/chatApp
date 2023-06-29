package chat.twenty;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class TwentyApplication {

	public static void main(String[] args) {
//		System.setProperty("javax.net.debug", "ssl:handshake");
//		System.setProperty( "https.protocols", "TLSv1.3" );
		SpringApplication.run(TwentyApplication.class, args);
	}

}
