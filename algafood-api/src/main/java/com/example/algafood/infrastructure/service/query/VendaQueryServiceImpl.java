package com.example.algafood.infrastructure.service.query;

import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.Predicate;

import org.springframework.stereotype.Repository;

import com.example.algafood.api.model.VendaDiaria;
import com.example.algafood.api.model.input.filter.VendaDiariaFilter;
import com.example.algafood.domain.model.Pedido;
import com.example.algafood.domain.model.StatusPedido;
import com.example.algafood.domain.service.IVendaQueryService;

@Repository
public class VendaQueryServiceImpl implements IVendaQueryService {

	@PersistenceContext
	private EntityManager manager;
	
	@Override
	public List<VendaDiaria> consultarVendasDiarias(VendaDiariaFilter vendaDiariaFilter, String timeOffSet) {
		var builder = manager.getCriteriaBuilder();
		var query = builder.createQuery(VendaDiaria.class);
		var root = query.from(Pedido.class);
		
		var functionConvertTzDataCriacao = builder.function("convert_tz", Date.class, root.get("dataCriacao"), 
				builder.literal("+00:00"), builder.literal(timeOffSet));
		var functionDateDataCriacao = builder.function("date", LocalDate.class, functionConvertTzDataCriacao);
		
		var selection = builder.construct(VendaDiaria.class,
				functionDateDataCriacao, 
				builder.count(root.get("id")),
				builder.sum(root.get("valorTotal")));

		var predicates = new ArrayList<Predicate>();
		
		if (Objects.nonNull(vendaDiariaFilter.getRestauranteId())) {
			predicates.add(builder.equal(root.get("restaurante"), vendaDiariaFilter.getRestauranteId()));
		}

		if (Objects.nonNull(vendaDiariaFilter.getDataCriacaoInicio())) {
			predicates.add(builder.greaterThanOrEqualTo(root.get("dataCriacao"), vendaDiariaFilter.getDataCriacaoInicio()));
		}

		if (Objects.nonNull(vendaDiariaFilter.getDataCriacaoFim())) {
			predicates.add(builder.lessThanOrEqualTo(root.get("dataCriacao"), vendaDiariaFilter.getDataCriacaoFim()));
		}
		
		predicates.add(root.get("status").in(StatusPedido.CONFIRMADO,StatusPedido.ENTREGUE));

		query.select(selection);
		query.where(predicates.toArray(new Predicate[0]));
		query.groupBy(functionDateDataCriacao);
		
		return manager.createQuery(query).getResultList();
	}

}
