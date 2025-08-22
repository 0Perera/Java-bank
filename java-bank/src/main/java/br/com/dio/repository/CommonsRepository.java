package br.com.dio.repository;

import br.com.dio.exception.NoFundsEnoughException;
import br.com.dio.model.AccountWallet;
import br.com.dio.model.InvestmentWallet;

public final class CommonsRepository {

    private CommonsRepository() {}

    public static void checkBalanceForTransaction(AccountWallet account, long amount) {
        if (amount <= 0) return;
        if (account.getBalance() < amount) {
            throw new NoFundsEnoughException("Saldo insuficiente na conta para movimentar " + amount);
        }
    }

    public static void checkBalanceForTransaction(InvestmentWallet wallet, long amount) {
        if (amount <= 0) return;
        if (wallet.getBalance() < amount) {
            throw new NoFundsEnoughException("Saldo insuficiente na carteira de investimentos para movimentar " + amount);
        }
    }
}
