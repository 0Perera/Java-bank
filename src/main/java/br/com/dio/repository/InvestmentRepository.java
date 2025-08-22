package br.com.dio.repository;

import br.com.dio.model.AccountWallet;
import br.com.dio.model.Investment;
import br.com.dio.model.InvestmentWallet;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import static br.com.dio.repository.CommonsRepository.checkBalanceForTransaction;

public class InvestmentRepository {

    private long nextId = 0;
    private final List<Investment> investments = new ArrayList<>();
    private final List<InvestmentWallet> wallets = new ArrayList<>();
    private final AccountRepository accountRepository;

    public InvestmentRepository(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    public Investment create(BigDecimal taxPercent, BigDecimal initialBalance) {
        this.nextId++;
        var inv = new Investment(this.nextId,
                taxPercent.setScale(2, RoundingMode.DOWN),
                initialBalance.setScale(2, RoundingMode.DOWN));
        investments.add(inv);
        return inv;
    }

    public InvestmentWallet initInvestment(AccountWallet account, long id) {
        var inv = findById(id);
        checkBalanceForTransaction(account, inv.initialBalance());
        account.withdraw(inv.initialBalance());
        var wallet = new InvestmentWallet(inv, account);
        wallets.add(wallet);
        return wallet;
    }

    public InvestmentWallet createWallet(String pix, BigDecimal initial, BigDecimal taxPct) {
        var acc = accountRepository.findByPix(pix);
        var inv = create(taxPct, initial);
        return initInvestment(acc, inv.id());
    }

    public Investment findById(long id) {
        return investments.stream().filter(i -> i.id() == id)
                .findFirst().orElseThrow(() -> new RuntimeException("Investimento nao encontrado"));
    }

    public List<InvestmentWallet> listWalletsByPix(String pix) {
        var acc = accountRepository.findByPix(pix);
        return wallets.stream().filter(w -> w.getAccount() == acc).toList();
    }

    public InvestmentWallet findWalletByPixAndInvestmentId(String pix, long investmentId){
        return wallets.stream()
                .filter(w -> w.getAccount().getPix().contains(pix) && w.getInvestment().id() == investmentId)
                .findFirst().orElseThrow(() -> new RuntimeException("Carteira nao encontrada"));
    }

    public InvestmentWallet deposit(String pix, long investmentId, BigDecimal amount){
        var w = findWalletByPixAndInvestmentId(pix, investmentId);
        checkBalanceForTransaction(w.getAccount(), amount);
        w.getAccount().withdraw(amount);
        w.deposit(amount);
        return w;
    }

    public InvestmentWallet withdraw(String pix, long investmentId, BigDecimal amount){
        var w = findWalletByPixAndInvestmentId(pix, investmentId);
        checkBalanceForTransaction(w, amount);
        w.withdraw(amount);
        w.getAccount().deposit(amount);
        return w;
    }

    public InvestmentWallet increase(String pix, long investmentId){
        var w = findWalletByPixAndInvestmentId(pix, investmentId);
        w.updateAmount();
        return w;
    }

    public void updateAmount(){
        wallets.forEach(InvestmentWallet::updateAmount);
    }

    public List<InvestmentWallet> listWallets(){
        return wallets;
    }
}