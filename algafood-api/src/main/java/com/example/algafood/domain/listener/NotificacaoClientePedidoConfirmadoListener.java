package com.example.algafood.domain.listener;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import com.example.algafood.domain.event.PedidoConfirmadoEvent;
import com.example.algafood.domain.model.Pedido;
import com.example.algafood.domain.service.IEnvioEmailService;
import com.example.algafood.domain.service.IEnvioEmailService.Mensagem;

@Component
public class NotificacaoClientePedidoConfirmadoListener {

	@Autowired
	private IEnvioEmailService envioEmailService;
	
	@TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
	public void aoConfirmarPedido(PedidoConfirmadoEvent event) {
		Pedido pedido = event.getPedido();
		
		Mensagem mensagem = Mensagem.builder()
			.assunto(pedido.getRestaurante().getNome() + " - Pedido confirmado!")
			.corpo("pedido-confirmado.html")
			.variavel("pedido", pedido)
			.destinatario(pedido.getCliente().getEmail())
			.build();
		
		envioEmailService.enviar(mensagem);
		
	}
}
