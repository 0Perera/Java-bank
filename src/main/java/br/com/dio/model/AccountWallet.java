package br.com.dio.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.*;

public class AccountWallet extends Wallet {
    private final List<String> pix;
    private final NavigableMap<LocalDateTime, List<Transaction>> history = new TreeMap<>();

    public AccountWallet(List<String> pix, BigDecimal initial) {
        this.pix = pix == null ? List.of() : List.copyOf(pix);
        this.balance = (initial == null ? BigDecimal.ZERO : initial).setScale(2, RoundingMode.DOWN);
        addHistory("Conta criada com saldo inicial");
    }

    public List<String> getPix() {
        return pix;
    }

    public NavigableMap<LocalDateTime, List<Transaction>> getHistory() {
        return history;
    }

    public void addHistory(String desc) {
        var now = LocalDateTime.now();
        history.computeIfAbsent(now, k -> new ArrayList<>())
               .add(new Transaction(UUID.randomUUID().toString(), desc));
    }

    @Override
    public void deposit(BigDecimal amount) {
        super.deposit(amount);
        addHistory("Deposito de " + amount);
    }

    @Override
    public void withdraw(BigDecimal amount) {
        super.withdraw(amount);
        addHistory("Saque de " + amount);
    }
}