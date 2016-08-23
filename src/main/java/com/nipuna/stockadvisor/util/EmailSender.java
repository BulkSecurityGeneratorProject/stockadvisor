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
			email.setSubject(sub);
			email.setMsg(body + "\n\n\n\n" + "Sent from " + hostName);
			email.addTo("vijayender.bandaru@gmail.com");
			email.send();

		} catch (Exception e) {
			System.err.println("error sending email");
			e.printStackTrace();
		}

	}

	public static void sendEmail2(String sub, String body) {
		try {

			String host = System.getenv("SPRING_MAIL_HOST");
			String port = System.getenv("SPRING_MAIL_PORT");
			String userName = System.getenv("SPRING_MAIL_USERNAME");
			String password = System.getenv("SPRING_MAIL_PASSWORD");
			String sentFrom = System.getenv("STOCKADVISOR_MAIL_SENTFROM");
			String sendTo = System.getenv("STOCKADVISOR_MAIL_SENDTO");
			String debug = System.getenv("STOCKADVISOR_MAIL_DEBUG");

			Email email = new SimpleEmail();
			email.setHostName(host);
			email.setSmtpPort(587);
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
