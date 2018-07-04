package com.blexr.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

@Component
public class EMail {

    @Value("${mail.send.to}")
    private String sendTo;

    @Value("${mail.send.bcc}")
    private String sendBcc;

    @Value("${mail.send.new.games.title}")
    private String messageTitle;
    
    @Autowired
    public JavaMailSender emailSender;
    
    public void sendMessage(String subject, String messageBody) {
	SimpleMailMessage message = new SimpleMailMessage();
	message.setText(messageBody);
	
	if (sendTo!=null && sendTo.length()>0) {
	    String [] toArray = sendTo.split(";");
	    message.setTo(toArray);
	}
	if (sendBcc!=null && sendBcc.length()>0) {
	    String [] bccArray = sendBcc.split(";");
	    message.setBcc(bccArray);
	}
	
	// if subject is set as a parameter send that subject. If the parameter is null or empty set the default subject from properties
	if (subject!=null && subject.length()>0) {
	    message.setSubject(subject);
	}
	else {
	    message.setSubject(messageTitle);
	}
	
	emailSender.send(message);
    }
}
