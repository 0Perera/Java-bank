package br.com.dio.repository;

import br.com.dio.exception.AccountNotFoundException;
import br.com.dio.exception.PixInUseException;
import br.com.dio.model.AccountWallet;
import br.com.dio.model.MoneyAudit;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static br.com.dio.repository.CommonsRepository.checkBalanceForTransaction;
import static java.time.temporal.ChronoUnit.SECONDS;

public class AccountRepository {

    private final List<AccountWallet> accounts = new ArrayList<>();

    public AccountWallet create(final List<String> pix, final long initialBalance) {
        if (!accounts.isEmpty()) {
            var pixInUse = accounts.stream().flatMap(a -> a.getPix().stream()).toList();
            for (var p : pix) {
                if (pixInUse.contains(p)) {
                    throw new PixInUseException("O pix '"+ p + "' já está em uso");
                }
            }
        }
        var newAccount = new AccountWallet(initialBalance, pix);
        accounts.add(newAccount);
        return newAccount;
    }

    public void deposit(final String pix, final long balanceAmount) {
        var target = findByPix(pix);
        target.addMoney(balanceAmount, "Depósito");
    }

    public long withdraw(final String pix, final long amount) {
        var source = findByPix(pix);
        checkBalanceForTransaction(source, amount);
        source.reduceMoney(amount);
        return amount;
    }

    public void transferMoney(final String sourcePix, final String targetPix, final long amount) {
        var source = findByPix(sourcePix);
        checkBalanceForTransaction(source, amount);
        var target = findByPix(targetPix);
        var message = "Pix enviado de '" + sourcePix + "' para '" + targetPix + "'";
        target.addMoney(source.reduceMoney(amount), source.getService(), message);
    }

    public AccountWallet findByPix(final String pix) {
        return accounts.stream().filter(a -> a.getPix().contains(pix))
                .findFirst()
                .orElseThrow(() -> new AccountNotFoundException("A conta com a chave pix '"+pix+"' não foi encontrada."));
    }

    public List<AccountWallet> list(){
        return this.accounts;
    }

    public Map<OffsetDateTime, List<MoneyAudit>> getHistory(final String pix) {
        var wallet = findByPix(pix);
        var audit = wallet.getFinancialTransactions();
        var group = audit.stream()
                .collect(Collectors.groupingBy(t -> t.createdAt().truncatedTo(SECONDS)));
        return group;
    }

}
