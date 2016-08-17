package com.nipuna.stockadvisor.util;

import java.net.InetAddress;

import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.Email;
import org.apache.commons.mail.SimpleEmail;

public class EmailSender {

	public static void sendEmail(String sub, String body) {
		try {
			Email email = new SimpleEmail();
			email.setHostName("smtp.gmail.com");
			email.setSmtpPort(587);
			email.setAuthenticator(new DefaultAuthenticator("vijay.nipuna", "paxfurnrytbbzcpr"));
			email.setDebug(true);
			email.setStartTLSEnabled(true);

			String hostName = InetAddress.getLocalHost().getHostName();

			email.setFrom("vijay.nipuna@gmail.com", "Stock Advisor v1.0");
			email.setSubject(sub + " from " + hostName);
			email.setMsg(body);
			email.addTo("vijayender.bandaru@gmail.com");
			email.send();

		} catch (Exception e) {
			System.err.println("error sending email");
			e.printStackTrace();
		}

	}
}
