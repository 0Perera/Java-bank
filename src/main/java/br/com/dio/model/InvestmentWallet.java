package br.com.dio.model;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class InvestmentWallet extends Wallet {
    private final Investment investment;
    private final AccountWallet account;

    public InvestmentWallet(Investment investment, AccountWallet account) {
        this.investment = investment;
        this.account = account;
        this.balance = investment.initialBalance().setScale(2, RoundingMode.DOWN);
    }

    public Investment getInvestment() {
        return investment;
    }

    public AccountWallet getAccount() {
        return account;
    }

    public void updateAmount() {
        var tax = investment.taxPercent();
        if (tax == null) return;
        var factor = tax.movePointLeft(2).add(BigDecimal.ONE);
        this.balance = this.balance.multiply(factor).setScale(2, RoundingMode.DOWN);
    }
}