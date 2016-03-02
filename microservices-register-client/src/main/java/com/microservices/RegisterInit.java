package com.microservices;

import com.microservices.model.App;
import com.microservices.model.Register;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.springframework.scheduling.annotation.Scheduled;

import javax.ws.rs.client.*;
import javax.ws.rs.core.Response;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RegisterInit extends Thread {

	private static final Logger log = Logger.getLogger(RegisterInit.class
			.getName());
	private App currentApp;
	private Register register;
	private boolean registered = false;

	public RegisterInit(App currentApp,Register register) {
		super();
		this.currentApp = currentApp;
		this.register = register;
	}

    @Scheduled(fixedRate = 1000)
    public void reportCurrentTime() {
        System.out.println("The time is now " );
    }

	public void run() {
		int currentTry = 0;
		while (!registered && currentTry < 100) {
			currentTry++;


			Client client = ClientBuilder.newBuilder().register(JacksonFeature.class).build();
			WebTarget target = client.target("http://"+register.getHostName()+":"
					+register.getPort());
			target = target.path("/apps/"+currentApp.getApp());

			Invocation.Builder builder = target.request();


			try {
				Response response = builder.post(Entity.json(currentApp));
			}catch (Exception e){
				try {
					Thread.sleep(2000);
				} catch (InterruptedException ex) {
					log.log(Level.SEVERE, "error " + currentApp.getApp());
				}
			}
		}

		if (!registered) {
			throw new RuntimeException("From "+currentApp.getApp()+": Unable to find parent API "
					+ currentApp.getApp());
		}
	}
}
