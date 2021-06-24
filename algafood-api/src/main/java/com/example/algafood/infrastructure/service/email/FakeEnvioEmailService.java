package com.example.algafood.infrastructure.service.email;

import org.springframework.beans.factory.annotation.Autowired;

import com.example.algafood.domain.service.IEnvioEmailService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class FakeEnvioEmailService implements IEnvioEmailService {

	@Autowired
	private ProcessadorEmailTemplate processadorEmailTemplate;
	
	public void enviar(Mensagem mensagem) {
		String corpo = processadorEmailTemplate.processarTemplate(mensagem);

		log.info("[FAKE E-MAIL] Para: {}\n{}", mensagem.getDestinatarios(), corpo);
	}

}
