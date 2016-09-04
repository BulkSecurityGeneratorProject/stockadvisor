package com.nipuna.stockadvisor.util;

import java.net.InetAddress;

import javax.inject.Inject;

import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.Email;
import org.apache.commons.mail.SimpleEmail;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
public class EmailSender {

	@Inject
	private Environment env;

	public void sendEmail(String sub, String body) {
		try {

			String host = env.getProperty("SPRING_MAIL_HOST");
			String port = env.getProperty("SPRING_MAIL_PORT");
			String userName = env.getProperty("SPRING_MAIL_USERNAME");
			String password = env.getProperty("SPRING_MAIL_PASSWORD");
			String sentFrom = env.getProperty("STOCKADVISOR_MAIL_SENTFROM");
			String sendTo = env.getProperty("STOCKADVISOR_MAIL_SENDTO");
			String debug = env.getProperty("STOCKADVISOR_MAIL_DEBUG");

			Email email = new SimpleEmail();
			email.setHostName(host);
			email.setSmtpPort(Integer.valueOf(port));
			email.setAuthenticator(new DefaultAuthenticator(userName, password));
			email.setDebug(Boolean.valueOf(debug));
			email.setStartTLSEnabled(true);

			String hostName = InetAddress.getLocalHost().getHostName();

			email.setFrom(sentFrom, "Stock Advisor v1.0");
			email.setSubject(sub);
			email.setMsg(body + "\n\n\n\n" + "Sent from " + hostName);
			email.addTo(sendTo);
			email.send();

		} catch (Exception e) {
			System.err.println("error sending email");
			e.printStackTrace();
		}

	}
}
