package br.com.dio.model;

import java.math.BigDecimal;

public record Investment(long id, BigDecimal taxPercent, BigDecimal initialBalance) {}