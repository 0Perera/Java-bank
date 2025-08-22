package br.com.dio.repository;

import br.com.dio.model.AccountWallet;
import br.com.dio.model.InvestmentWallet;

import java.math.BigDecimal;

public final class CommonsRepository {
    private CommonsRepository() {}

    public static void checkBalanceForTransaction(AccountWallet account, BigDecimal amount) {
        if (amount == null || amount.signum() <= 0) throw new IllegalArgumentException("Valor invalido");
        if (account.getBalance().compareTo(amount) < 0) throw new IllegalArgumentException("Saldo insuficiente na conta");
    }

    public static void checkBalanceForTransaction(InvestmentWallet wallet, BigDecimal amount) {
        if (amount == null || amount.signum() <= 0) throw new IllegalArgumentException("Valor invalido");
        if (wallet.getBalance().compareTo(amount) < 0) throw new IllegalArgumentException("Saldo insuficiente na carteira");
    }
}