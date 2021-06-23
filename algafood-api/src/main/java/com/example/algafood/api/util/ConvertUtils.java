package com.example.algafood.api.util;

import java.util.List;

import javax.persistence.criteria.Predicate;

public class ConvertUtils {

	public static Predicate[] converterPredicateToArrray(List<Predicate> predicates) {
		return predicates.toArray(new Predicate[0]);
	}
}
