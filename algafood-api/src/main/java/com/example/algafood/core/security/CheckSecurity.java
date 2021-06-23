package com.example.algafood.core.security;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;

public @interface CheckSecurity {

	static final String SCOPE_READ = "hasAuthority('SCOPE_READ')";
	static final String SCOPE_WRITE = "hasAuthority('SCOPE_WRITE')";
	static final String IS_AUTHENTICATED = "isAuthenticated()";
	
	public @interface Cozinhas {
		
		@PreAuthorize(SCOPE_READ + " and " + IS_AUTHENTICATED)
		@Retention(RUNTIME)
		@Target(METHOD)
		public @interface PodeConsultar { }

		@PreAuthorize(SCOPE_WRITE + " and hasAuthority('EDITAR_COZINHAS')")
		@Retention(RUNTIME)
		@Target(METHOD)
		public @interface PodeEditar { }

	}

	public @interface Restaurantes {
		
		static final String EDITAR = "hasAuthority('EDITAR_RESTAURANTES')";
		
		@PreAuthorize(SCOPE_READ + " and " + IS_AUTHENTICATED)
		@Retention(RUNTIME)
		@Target(METHOD)
		public @interface PodeConsultar { }

		@PreAuthorize(SCOPE_WRITE + " and " + EDITAR)
		@Retention(RUNTIME)
		@Target(METHOD)
		public @interface PodeGerenciarCadastro { }

		@PreAuthorize(SCOPE_WRITE + " and "
				+ "("
					+ EDITAR + " or "
					+ "@algaSecurity.gerenciaRestaurante(#id)"
				+ ")"
				)
		@Retention(RUNTIME)
		@Target(METHOD)
		public @interface PodeGerenciarFuncionamento { }

	}
	
	public @interface Pedidos {
		
		@PreAuthorize(SCOPE_READ + " and " + IS_AUTHENTICATED)
		@PostAuthorize("hasAuthority('CONSULTAR_PEDIDOS') or "
				+ "@algaSecurity.usuarioAutenticadoIgual(returnObject.cliente.id) or "
				+ "@algaSecurity.gerenciaRestaurante(returnObject.restaurante.id)")
		@Retention(RUNTIME)
		@Target(METHOD)
		public @interface PodeBuscar { }

		@PreAuthorize(SCOPE_READ + "and "
				+ "("
					+ "hasAuthority('CONSULTAR_PEDIDOS') or " 
					+ "@algaSecurity.usuarioAutenticadoIgual(#filtro.clienteId) or"
					+ "@algaSecurity.gerenciaRestaurante(#filtro.restauranteId)"
				+ ")")
		@Retention(RUNTIME)
		@Target(METHOD)
		public @interface PodePesquisar { }
		
		@PreAuthorize(SCOPE_WRITE + " and " + IS_AUTHENTICATED)
		@Retention(RUNTIME)
		@Target(METHOD)
		public @interface PodeCriar { }

		@PreAuthorize(SCOPE_WRITE + " and (hasAuthority('GERENCIAR_PEDIDOS') or "
				+ "@algaSecurity.gerenciaRestauranteDoPedido(#codigoPedido))")
		@Retention(RUNTIME)
		@Target(METHOD)
		public @interface PodeGerenciarPedidos { }
		
	}

	public @interface FormasPagamento {

		static final String EDITAR = "hasAuthority('EDITAR_FORMAS_PAGAMENTO')";
		
	    @PreAuthorize(SCOPE_WRITE + " and " + EDITAR)
	    @Retention(RUNTIME)
	    @Target(METHOD)
	    public @interface PodeEditar { }

		@PreAuthorize(SCOPE_READ + " and " + IS_AUTHENTICATED)
	    @Retention(RUNTIME)
	    @Target(METHOD)
	    public @interface PodeConsultar { }
	    
	}

	public @interface Cidades {

		static final String EDITAR = "hasAuthority('EDITAR_CIDADES')";
		
	    @PreAuthorize(SCOPE_WRITE + " and " + EDITAR)
	    @Retention(RUNTIME)
	    @Target(METHOD)
	    public @interface PodeEditar { }

		@PreAuthorize(SCOPE_READ + " and " + IS_AUTHENTICATED)
	    @Retention(RUNTIME)
	    @Target(METHOD)
	    public @interface PodeConsultar { }
	    
	}

	public @interface Estados {

		static final String EDITAR = "hasAuthority('EDITAR_ESTADOS')";

	    @PreAuthorize(SCOPE_WRITE + " and " + EDITAR)
	    @Retention(RUNTIME)
	    @Target(METHOD)
	    public @interface PodeEditar { }

		@PreAuthorize(SCOPE_READ + " and " + IS_AUTHENTICATED)
	    @Retention(RUNTIME)
	    @Target(METHOD)
	    public @interface PodeConsultar { }
	    
	}

	public @interface UsuariosGruposPermissoes {

		static final String EDITAR = "hasAuthority('EDITAR_USUARIOS_GRUPOS_PERMISSOES')";
		static final String CONSULTAR = "hasAuthority('CONSULTAR_USUARIOS_GRUPOS_PERMISSOES')";

	    @PreAuthorize(SCOPE_WRITE + " and "
	            + "@algaSecurity.usuarioAutenticadoIgual(#usuarioId)")
	    @Retention(RUNTIME)
	    @Target(METHOD)
	    public @interface PodeAlterarPropriaSenha { }
	    
	    @PreAuthorize(SCOPE_WRITE + " and "
	    		+ "("
	    			+ EDITAR + " or "
	    			+ "@algaSecurity.usuarioAutenticadoIgual(#usuarioId)"
    			+ ")")
	    @Retention(RUNTIME)
	    @Target(METHOD)
	    public @interface PodeAlterarUsuario { }

	    @PreAuthorize(SCOPE_WRITE + " and " + EDITAR)
	    @Retention(RUNTIME)
	    @Target(METHOD)
	    public @interface PodeEditar { }
	    

	    @PreAuthorize(SCOPE_READ + " and " + CONSULTAR)
	    @Retention(RUNTIME)
	    @Target(METHOD)
	    public @interface PodeConsultar { }
	    
	}

	public @interface Estatisticas {

	    @PreAuthorize(SCOPE_READ + " and "
	            + "hasAuthority('GERAR_RELATORIOS')")
	    @Retention(RUNTIME)
	    @Target(METHOD)
	    public @interface PodeConsultar { }
	    
	}

}
