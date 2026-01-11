package com.membership.users;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;

import com.membership.users.infrastructure.security.RSAKeyUtil;

@SpringBootApplication
public class MembershipApplication {

	private final RSAKeyUtil rsaKeyUtil;

	public MembershipApplication(RSAKeyUtil rsaKeyUtil) {
		this.rsaKeyUtil = rsaKeyUtil;
	}

	public static void main(String[] args) {
		SpringApplication.run(MembershipApplication.class, args);
	}

	@EventListener(ApplicationReadyEvent.class)
	public void generateRSAKeys() {
		rsaKeyUtil.generateKeysIfNotExist();
	}

}
