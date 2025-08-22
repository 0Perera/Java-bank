package br.com.dio.repository;

import br.com.dio.model.AccountWallet;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.NavigableMap;
import java.util.TreeMap;
import java.util.stream.Collectors;

public class AccountRepository {
    private final List<AccountWallet> accounts = new ArrayList<>();

    public AccountWallet create(List<String> pix, BigDecimal initialDeposit) {
        var acc = new AccountWallet(pix, initialDeposit.setScale(2, RoundingMode.DOWN));
        accounts.add(acc);
        return acc;
    }

    public AccountWallet create(List<String> pix, long cents) {
        return create(pix, BigDecimal.valueOf(cents).movePointLeft(2));
    }

    public AccountWallet findByPix(String pix) {
        return accounts.stream()
                .filter(a -> a.getPix().contains(pix))
                .findFirst().orElseThrow(() -> new RuntimeException("Conta nao encontrada: " + pix));
    }

    public void deposit(String pix, BigDecimal amount) {
        var a = findByPix(pix);
        a.deposit(amount.setScale(2, RoundingMode.DOWN));
    }

    public void deposit(String pix, long cents) {
        deposit(pix, BigDecimal.valueOf(cents).movePointLeft(2));
    }

    public void withdraw(String pix, BigDecimal amount) {
        var a = findByPix(pix);
        a.withdraw(amount.setScale(2, RoundingMode.DOWN));
    }

    public void withdraw(String pix, long cents) {
        withdraw(pix, BigDecimal.valueOf(cents).movePointLeft(2));
    }

    public void transferMoney(String sourcePix, String targetPix, BigDecimal amount) {
        var s = findByPix(sourcePix);
        var t = findByPix(targetPix);
        s.withdraw(amount);
        t.deposit(amount);
    }

    public void transferMoney(String sourcePix, String targetPix, long cents) {
        transferMoney(sourcePix, targetPix, BigDecimal.valueOf(cents).movePointLeft(2));
    }

    public NavigableMap<LocalDateTime, List<br.com.dio.model.Transaction>> getHistory(String pix) {
        var acc = findByPix(pix);
        // Já armazenado em cada AccountWallet; aqui só devolvemos ordenado
        return acc.getHistory();
    }

    public List<AccountWallet> list() {
        return accounts;
    }
}