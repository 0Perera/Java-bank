import br.com.dio.model.InvestmentWallet;
import br.com.dio.model.AccountWallet;
import br.com.dio.repository.AccountRepository;
import br.com.dio.repository.InvestmentRepository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;

import static java.time.format.DateTimeFormatter.ISO_DATE_TIME;

public class Main {

    private static final Scanner input = new Scanner(System.in);
    private static final AccountRepository accountRepository = new AccountRepository();
    private static final InvestmentRepository investmentRepository = new InvestmentRepository(accountRepository);
    private static final NumberFormat BRL = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));

    public static void main(String[] args) {
        while (true) {
            printMenu();
            int opt = readIntOption(1, 14);
            switch (opt) {
                case 1 -> createAccount();
                case 2 -> createInvestmentWallet();
                case 3 -> withdrawFromAccount();
                case 4 -> depositToAccount();
                case 5 -> transferBetweenAccounts();
                case 6 -> showAccountHistory();
                case 7 -> listWalletsByPix();
                case 8 -> depositIntoInvestmentWallet();
                case 9 -> withdrawFromInvestmentWallet();
                case 10 -> increaseOneInvestment();
                case 11 -> updateAllInvestments();
                case 12 -> listAllInvestmentWallets();
                case 13 -> listAllAccounts();
                case 14 -> { System.out.println("Saindo..."); return; }
                default -> System.out.println("Opcao invalida.");
            }
        }
    }

    private static void printMenu() {
        System.out.println("\n=== MENU ===");
        System.out.println("1  - Criar conta");
        System.out.println("2  - Criar carteira (pix, aporte inicial em reais, taxa %)");
        System.out.println("3  - Sacar da conta");
        System.out.println("4  - Depositar na conta");
        System.out.println("5  - Transferir entre contas");
        System.out.println("6  - Historico da conta");
        System.out.println("7  - Listar carteiras por PIX");
        System.out.println("8  - Aportar em carteira (PIX + investmentId)");
        System.out.println("9  - Sacar da carteira (PIX + investmentId)");
        System.out.println("10 - Atualizar rendimento de 1 carteira (PIX + investmentId)");
        System.out.println("11 - Atualizar rendimento de TODAS as carteiras");
        System.out.println("12 - Listar TODAS as carteiras");
        System.out.println("13 - Listar TODAS as contas");
        System.out.println("14 - Sair");
        System.out.print("Opcao: ");
    }

    private static int readIntOption(int min, int max) {
        while (true) {
            String s = input.nextLine().trim();
            try {
                int n = Integer.parseInt(s);
                if (n < min || n > max) {
                    System.out.print("Opcao invalida. Informe um numero entre " + min + " e " + max + ": ");
                    continue;
                }
                return n;
            } catch (NumberFormatException e) {
                System.out.print("Entrada invalida. Digite um numero: ");
            }
        }
    }

    private static BigDecimal readMoney(String prompt) {
        System.out.print(prompt);
        while (true) {
            String s = input.nextLine().trim().replace(",", ".");
            try {
                return new BigDecimal(s).setScale(2, RoundingMode.DOWN);
            } catch (Exception e) {
                System.out.print("Valor invalido. Digite em reais (ex: 10.50): ");
            }
        }
    }

    private static String readString(String prompt) {
        System.out.print(prompt);
        String s = input.nextLine().trim();
        while (s.isEmpty()) {
            System.out.print("Valor vazio. Informe novamente: ");
            s = input.nextLine().trim();
        }
        return s;
    }

    private static String brl(BigDecimal v) {
        return BRL.format(v);
    }

    private static String primaryPix(List<String> pixList) {
        return pixList == null || pixList.isEmpty() ? "<sem pix>" : pixList.get(0);
    }

    private static void printAccountLine(AccountWallet a, String prefix) {
        String anyPix = primaryPix(a.getPix());
        System.out.println(prefix + " PIX=" + anyPix + " | saldo=" + brl(a.getBalance()));
    }

    private static void printWalletLine(InvestmentWallet w, String prefix) {
        String anyPix = primaryPix(w.getAccount().getPix());
        System.out.println(prefix + " PIX=" + anyPix
                + " | invId=" + w.getInvestment().id()
                + " | taxa=" + w.getInvestment().taxPercent() + "%"
                + " | saldo=" + brl(w.getBalance()));
    }

    private static void createAccount(){
        String pixJoined = readString("Informe as chaves Pix (separadas por ';'): ");
        List<String> pix = List.of(pixJoined.split(";"));
        BigDecimal amount = readMoney("Informe o valor inicial do deposito (reais): ");
        var account = accountRepository.create(pix, amount);
        printAccountLine(account, "OK CONTA CRIADA |");
    }

    private static void withdrawFromAccount(){
        String pix = readString("Informe a chave Pix da conta: ");
        BigDecimal amount = readMoney("Informe o valor do saque (reais): ");
        try{
            accountRepository.withdraw(pix, amount);
            var acc = accountRepository.findByPix(pix);
            System.out.println("OK SAQUE | PIX=" + primaryPix(acc.getPix())
                    + " | valor=" + brl(amount)
                    + " | saldo=" + brl(acc.getBalance()));
        } catch (Exception ex){
            System.out.println(ex.getMessage());
        }
    }

    private static void depositToAccount(){
        String pix = readString("Informe a chave Pix da conta: ");
        BigDecimal amount = readMoney("Informe o valor do deposito (reais): ");
        try {
            accountRepository.deposit(pix, amount);
            var acc = accountRepository.findByPix(pix);
            System.out.println("OK DEPOSITO | PIX=" + primaryPix(acc.getPix())
                    + " | valor=" + brl(amount)
                    + " | saldo=" + brl(acc.getBalance()));
        } catch (Exception ex){
            System.out.println(ex.getMessage());
        }
    }

    private static void transferBetweenAccounts(){
        String source = readString("Informe a chave Pix da conta origem: ");
        String target = readString("Informe a chave Pix da conta destino: ");
        BigDecimal amount = readMoney("Informe o valor da transferencia (reais): ");
        try {
            accountRepository.transferMoney(source, target, amount);
            var src = accountRepository.findByPix(source);
            var tgt = accountRepository.findByPix(target);
            System.out.println("OK TRANSFERENCIA | DE=" + primaryPix(src.getPix())
                    + " | PARA=" + primaryPix(tgt.getPix())
                    + " | valor=" + brl(amount)
                    + " | saldo_src=" + brl(src.getBalance())
                    + " | saldo_dst=" + brl(tgt.getBalance()));
        } catch (Exception ex){
            System.out.println(ex.getMessage());
        }
    }

    private static void showAccountHistory(){
        String pix = readString("Informe a chave Pix da conta: ");
        try{
            var acc = accountRepository.findByPix(pix);
            System.out.println("HIST CONTA | PIX=" + primaryPix(acc.getPix()));
            var sortedHistory = accountRepository.getHistory(pix);
            sortedHistory.forEach((k,v)->{
                System.out.println("DATA=" + k.format(ISO_DATE_TIME)
                        + " | firstTx=" + v.getFirst().transactionId()
                        + " | lastDesc=" + v.getLast().description()
                        + " | n=" + v.size());
            });
        } catch (Exception ex){
            System.out.println(ex.getMessage());
        }
    }

    private static void createInvestmentWallet(){
        String pix = readString("Informe a chave Pix da conta: ");
        BigDecimal initial = readMoney("Informe o aporte inicial (reais): ");
        BigDecimal rate = readMoney("Informe a taxa (em %): ");
        try {
            var wallet = investmentRepository.createWallet(pix, initial, rate);
            printWalletLine(wallet, "OK CARTEIRA CRIADA |");
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }

    private static void listWalletsByPix(){
        String pix = readString("Informe a chave Pix da conta: ");
        try {
            var wallets = investmentRepository.listWalletsByPix(pix);
            if (wallets.isEmpty()) {
                System.out.println("Nenhuma carteira para o PIX informado.");
                return;
            }
            wallets.forEach(w -> printWalletLine(w, "WALLET |"));
        } catch (Exception ex){
            System.out.println(ex.getMessage());
        }
    }

    private static void depositIntoInvestmentWallet(){
        String pix = readString("Informe a chave Pix da conta: ");
        long invId = readIntOption(1, Integer.MAX_VALUE);
        BigDecimal amount = readMoney("Informe o valor do aporte (reais): ");
        try {
            var w = investmentRepository.deposit(pix, invId, amount);
            System.out.println("OK APORTE | PIX=" + primaryPix(w.getAccount().getPix())
                    + " | invId=" + w.getInvestment().id()
                    + " | valor=" + brl(amount)
                    + " | saldo=" + brl(w.getBalance()));
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }

    private static void withdrawFromInvestmentWallet(){
        String pix = readString("Informe a chave Pix da conta: ");
        long invId = readIntOption(1, Integer.MAX_VALUE);
        BigDecimal amount = readMoney("Informe o valor do saque (reais): ");
        try {
            var w = investmentRepository.withdraw(pix, invId, amount);
            System.out.println("OK SAQUE INVEST | PIX=" + primaryPix(w.getAccount().getPix())
                    + " | invId=" + w.getInvestment().id()
                    + " | valor=" + brl(amount)
                    + " | saldo=" + brl(w.getBalance()));
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }

    private static void increaseOneInvestment(){
        String pix = readString("Informe a chave Pix da conta: ");
        long invId = readIntOption(1, Integer.MAX_VALUE);
        try {
            var w = investmentRepository.increase(pix, invId);
            System.out.println("OK RENDIMENTO | PIX=" + primaryPix(w.getAccount().getPix())
                    + " | invId=" + w.getInvestment().id()
                    + " | saldo=" + brl(w.getBalance()));
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }

    private static void updateAllInvestments(){
        try {
            int before = investmentRepository.listWallets().size();
            investmentRepository.updateAmount();
            System.out.println("OK RENDIMENTO GLOBAL | carteiras=" + before);
        } catch (Exception ex){
            System.out.println(ex.getMessage());
        }
    }

    private static void listAllInvestmentWallets(){
        List<InvestmentWallet> wallets = investmentRepository.listWallets();
        if (wallets.isEmpty()){
            System.out.println("Nenhuma carteira de investimentos encontrada.");
            return;
        }
        wallets.forEach(w -> printWalletLine(w, "WALLET |"));
    }

    private static void listAllAccounts(){
        List<AccountWallet> accounts = accountRepository.list();
        if (accounts.isEmpty()){
            System.out.println("Nenhuma conta encontrada.");
            return;
        }
        accounts.forEach(a -> printAccountLine(a, "ACCOUNT |"));
    }
}