package br.com.dio.repository;

import br.com.dio.exception.AccountWithInvestmentException;
import br.com.dio.exception.InvestmentNotFoundException;
import br.com.dio.exception.WalletNotFoundException;
import br.com.dio.model.AccountWallet;
import br.com.dio.model.Investment;
import br.com.dio.model.InvestmentWallet;

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

    public Investment create(final long tax, final long initialBalance){
        this.nextId ++;
        var investment = new Investment(this.nextId, tax, initialBalance);
        investments.add(investment);
        return investment;
    }

    public InvestmentWallet initInvestment(final AccountWallet account, final long id){
        if (!wallets.isEmpty()){
            var accountInUse = wallets.stream().map(InvestmentWallet::getAccount).toList();
            if (accountInUse.contains(account)) {
                throw new AccountWithInvestmentException("O conta '"+ account + "' já possui um investimento");
            }
        }
        var investment = findById(id);
        checkBalanceForTransaction(account, investment.initialBalance());
        var wallet = new InvestmentWallet(investment, account, investment.initialBalance());
        wallets.add(wallet);
        return wallet;
    }

    public InvestmentWallet initInvestment(final String pix, final long initialBalance, final long tax){
        var account = accountRepository.findByPix(pix);
        var investment = create(tax, initialBalance);
        return initInvestment(account, investment.id());
    }

    public InvestmentWallet createWallet(final String pix){
        var account = accountRepository.findByPix(pix);
        var investment = create(0, 0);
        return initInvestment(account, investment.id());
    }

    public InvestmentWallet deposit(final String pix, final long balance){
        var wallet = findWalletByAccountPix(pix);
        checkBalanceForTransaction(wallet.getAccount(), balance);
        wallet.addMoney(wallet.getAccount().reduceMoney(balance), wallet.getService(), "Investimento");
        return wallet;
    }

    public InvestmentWallet withdraw(final String pix, final long balance){
        var wallet = findWalletByAccountPix(pix);
        checkBalanceForTransaction(wallet, balance);
        wallet.getAccount().addMoney(wallet.reduceMoney(balance), wallet.getService(), "Saque de investimentos");
        if (wallet.getBalance() == 0){
            wallets.remove(wallet);
        }
        return wallet;
    }

    public void updateAmount(){
        wallets.forEach(w -> w.updateAmount(w.getInvestment().tax()));
    }

    public InvestmentWallet increase(final String pix){
        var wallet = findWalletByAccountPix(pix);
        wallet.updateAmount(wallet.getInvestment().tax());
        return wallet;
    }

    public Investment findById(long id){
        return investments.stream()
                .filter(a -> a.id() == id)
                .findFirst()
                .orElseThrow(() -> new InvestmentNotFoundException("Investimento não encontrado"));
    }

    public InvestmentWallet findWalletByAccountPix(String pix){
        return wallets.stream()
                .filter(w -> w.getAccount().getPix().contains(pix))
                .findFirst()
                .orElseThrow(() -> new WalletNotFoundException("Carteira de investimentos não encontrada para pix: " + pix));
    }

    public List<InvestmentWallet> listWallets(){
        return this.wallets;
    }

    public List<Investment> list(){
        return this.investments;
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
