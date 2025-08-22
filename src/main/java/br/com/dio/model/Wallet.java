package br.com.dio.model;

import java.math.BigDecimal;
import java.math.RoundingMode;

public abstract class Wallet {
    protected BigDecimal balance = BigDecimal.ZERO.setScale(2, RoundingMode.DOWN);

    public BigDecimal getBalance() {
        return balance;
    }

    public void deposit(BigDecimal amount) {
        if (amount == null || amount.signum() <= 0) return;
        balance = balance.add(amount);
    }

    public void withdraw(BigDecimal amount) {
        if (amount == null || amount.signum() <= 0) return;
        if (balance.compareTo(amount) < 0) {
            throw new IllegalArgumentException("Saldo insuficiente");
        }
        balance = balance.subtract(amount);
    }
}