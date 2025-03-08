package br.com.somestudy.services;

import org.springframework.beans.factory.annotation.Autowired;

import br.com.somestudy.config.EmailConfig;
import br.com.somestudy.data.dto.request.EmailRequestDTO;
import br.com.somestudy.mail.EmailSender;

public class EmailService {
	@Autowired
	private EmailSender emailSender;

	@Autowired
	private EmailConfig emailConfigs;
	
	public void sendSimpleEmail(EmailRequestDTO emailRequest) {
		emailSender
		.to(emailRequest.getTo())
		.withSubject(emailRequest.getSubject())
		.withMessage(emailRequest.getSubject())
		.send(emailConfigs);
	}
}
