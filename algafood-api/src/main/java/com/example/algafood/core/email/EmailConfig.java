package com.example.algafood.core.email;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.example.algafood.domain.service.IEnvioEmailService;
import com.example.algafood.infrastructure.service.email.FakeEnvioEmailService;
import com.example.algafood.infrastructure.service.email.SandboxEnvioEmailService;
import com.example.algafood.infrastructure.service.email.SmtpEnvioEmailService;

@Configuration
public class EmailConfig {

	@Autowired
	private EmailProperties emailProperties;

	@Bean
	public IEnvioEmailService envioEmailService() {
		// Acho melhor usar switch aqui do que if/else if
		switch (emailProperties.getImpl()) {
			case FAKE:
				return new FakeEnvioEmailService();
			case SMTP:
				return new SmtpEnvioEmailService();
			case SANDBOX:
			    return new SandboxEnvioEmailService();
			    
			default:
				return null;
		}
	}

}
