package com.learnSpring.email;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * This class uses OAuth authentication to send an email using JavaMailSender
 *
 */

public class EPostMan {

	// Variables used for OAuth authentication
	private static String TOKEN_URL = "https://www.googleapis.com/oauth2/v4/token";
	private String oauthClientId = "134742172253-p683tlg44vi400a3ns3qa8oj0pqte6ba.apps.googleusercontent.com";
	private String oauthSecret = "pabDUYcWi1Q6j92GaomJRBWx";
	private String refreshToken = "1/W6i6Wuk-vcyEo6jlWMBzRpP25ZKe3HDYOr_czyiGXko";
	private String accessToken;

	private JavaMailSender mailSender;

	public void setMailSender(JavaMailSender mailSender) {
		this.mailSender = mailSender;
	}

	/**
	 * This method sends an email message
	 * 
	 * @param from
	 *            - Email address from which the email is being sent
	 * @param to
	 *            - Email address to which the email is being sent
	 * @param subject
	 *            - Subject of the email
	 * @param body
	 *            - Body of the email
	 */
	public void sendMail(String from, String to, String subject, String body) {

		// File to be sent as attachment
		FileSystemResource file = new FileSystemResource("src/main/resources/sample-file.txt");

		// Create and populate a MimeMessage
		MimeMessage mail = mailSender.createMimeMessage();
		MimeMessageHelper helper;
		try {
			helper = new MimeMessageHelper(mail, true);
			helper.setFrom(from);
			helper.setTo(to);
			helper.setSubject(subject);
			helper.setText(body);
			helper.addAttachment(file.getFilename(), file);
		} catch (MessagingException e) {
			e.printStackTrace();
		}
		// Set access token as the password for the mail account
		accessToken = generateAccessToken();
		((JavaMailSenderImpl) this.mailSender).setPassword(accessToken);
		// Send the MIME message (email)
		mailSender.send(mail);
	}

	/**
	 * This method generates an OAuth access token. To obtain a access token, a
	 * HTTPS POST request has to be sent to
	 * https://www.googleapis.com/oauth2/v4/token. This request must contain the
	 * client_id, client_secret, refresh_token and grant_type and it should be
	 * application/x-www-form-urlencoded
	 * 
	 * @return Returns the access token
	 */
	public String generateAccessToken() {

		try {
			// Convert the string into application/x-www-form-urlencoded
			// format using URLEncoder
			String request = "client_id=" + URLEncoder.encode(oauthClientId, "UTF-8") + "&client_secret="
					+ URLEncoder.encode(oauthSecret, "UTF-8") + "&refresh_token="
					+ URLEncoder.encode(refreshToken, "UTF-8") + "&grant_type=refresh_token";
			// Open a new URL connection
			HttpURLConnection connection = (HttpURLConnection) new URL(TOKEN_URL).openConnection();
			// Set the parameter to use the URL connection for output
			connection.setDoOutput(true);
			// Set the method for the URL request as POST
			connection.setRequestMethod("POST");
			// Get an output stream that writes to this connection
			PrintWriter writer = new PrintWriter(connection.getOutputStream());
			// Write the request data to the server
			writer.print(request);
			// Flush and close the output stream
			writer.flush();
			writer.close();
			// If connection is already not established, open a communication
			// link to the resource referenced by
			// connection.connect();

			try {
				HashMap<String, Object> resultMap;
				/*
				 * getInputStream() opens a inputs stream that reads from the
				 * open connection. The response from the server will contain
				 * the access token, but is formatted as JSON. Sample response
				 * from server {access_token=xxx, token_type=Bearer,
				 * expires_in=3600} Jackson is used here to parse the response
				 */
				resultMap = new ObjectMapper().readValue(connection.getInputStream(),
						new TypeReference<HashMap<String, Object>>() {
						});
				accessToken = (String) resultMap.get("access_token");
			} catch (IOException e) {
				e.printStackTrace();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return accessToken;
	}
}
