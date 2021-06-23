package com.example.algafood.domain.listener;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.event.TransactionalEventListener;

import com.example.algafood.domain.event.PedidoCanceladoEvent;
import com.example.algafood.domain.model.Pedido;
import com.example.algafood.domain.service.IEnvioEmailService;
import com.example.algafood.domain.service.IEnvioEmailService.Mensagem;

public class NotificacaoClientePedidoCanceladoListener {

    @Autowired
    private IEnvioEmailService envioEmail;
    
    @TransactionalEventListener
    public void aoCancelarPedido(PedidoCanceladoEvent event) {
        Pedido pedido = event.getPedido();
        
        var mensagem = Mensagem.builder()
                .assunto(pedido.getRestaurante().getNome() + " - Pedido cancelado")
                .corpo("pedido-cancelado.html")
                .variavel("pedido", pedido)
                .destinatario(pedido.getCliente().getEmail())
                .build();

        envioEmail.enviar(mensagem);
    }   
}        
