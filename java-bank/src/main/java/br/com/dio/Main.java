package br.com.dio;

import br.com.dio.exception.AccountNotFoundException;
import br.com.dio.exception.AccountWithInvestmentException;
import br.com.dio.exception.InvestmentNotFoundException;
import br.com.dio.exception.NoFundsEnoughException;
import br.com.dio.exception.WalletNotFoundException;
import br.com.dio.model.InvestmentWallet;
import br.com.dio.repository.AccountRepository;
import br.com.dio.repository.InvestmentRepository;

import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import static java.time.format.DateTimeFormatter.ISO_DATE_TIME;

public class Main {

    private static final Scanner input = new Scanner(System.in);
    private static final AccountRepository accountRepository = new AccountRepository();
    private static final InvestmentRepository investmentRepository = new InvestmentRepository(accountRepository);

    public static void main(String[] args) {
        while (true) {
            printMenu();
            int opt = input.nextInt();
            switch (opt) {
                case 1 -> createAccount();
                case 2 -> initInvestment();               // iniciar investimento (pix, aporte, taxa)
                case 3 -> withdraw();                     // saque da conta
                case 4 -> deposit();                      // depósito na conta
                case 5 -> transferToAccount();            // transferência entre contas
                case 6 -> checkHistory();                 // histórico de transações
                case 7 -> depositInInvestment();          // aportar em investimento
                case 8 -> increaseInvestments();          // atualizar rendimento da carteira de 1 conta (pix)
                case 9 -> withdrawFromInvestment();       // sacar da carteira de investimento
                case 10 -> updateAllInvestments();        // atualizar rendimento de TODAS as carteiras
                case 11 -> listInvestmentWallets();       // listar carteiras de investimento
                case 12 -> createWalletInvestment();      // criar carteira de investimento vazia para um pix
                case 13 -> { System.out.println("Saindo..."); return; }
                default -> System.out.println("Opção inválida");
            }
        }
    }

    private static void printMenu() {
        System.out.println("\n=== MENU ===");
        System.out.println("1  - Criar conta");
        System.out.println("2  - Iniciar investimento");
        System.out.println("3  - Sacar");
        System.out.println("4  - Depositar");
        System.out.println("5  - Transferir");
        System.out.println("6  - Histórico");
        System.out.println("7  - Aportar em investimento");
        System.out.println("8  - Atualizar investimento");
        System.out.println("9  - Sacar de investimento");
        System.out.println("10 - Atualizar TODOS investimentos");
        System.out.println("11 - Listar carteiras de investimento");
        System.out.println("12 - Criar carteira de investimento");
        System.out.println("13 - Sair");
        System.out.print("Opção: ");
    }

    private static void createAccount(){
        System.out.println("Informe as chaves Pix (separadas por ';')");
        var pix = Arrays.stream(input.next().split(";")).toList();
        System.out.println("Informe o valor inicial do deposito");
        var amount = input.nextLong();
        var account = accountRepository.create(pix, amount);
        System.out.println("Conta criada: " + account);
    }

    private static void withdraw(){
        System.out.println("Informe a chave Pix da conta: ");
        var pix = input.next();
        System.out.println("Informe o valor do saque: ");
        var amount = input.nextLong();
        try{
            accountRepository.withdraw(pix, amount);
            System.out.println("Saque de " + amount + " realizado na conta '" + pix + "'");
        }catch (AccountNotFoundException | NoFundsEnoughException ex){
            System.out.println(ex.getMessage());
        }
    }

    private static void deposit(){
        System.out.println("Informe a chave Pix da conta: ");
        var pix = input.next();
        System.out.println("Informe o valor do deposito: ");
        var amount = input.nextLong();
        try {
            accountRepository.deposit(pix, amount);
            System.out.println("Depósito de " + amount + " realizado na conta '" + pix + "'");
        }catch (AccountNotFoundException ex){
            System.out.println(ex.getMessage());
        }
    }

    private static void transferToAccount(){
        System.out.println("Informe a chave Pix da conta origem: ");
        var source = input.next();
        System.out.println("Informe a chave Pix da conta destino: ");
        var target = input.next();
        System.out.println("Informe o valor da transferencia: ");
        var amount = input.nextLong();
        try {
            accountRepository.transferMoney(source, target, amount);
            System.out.println("Transferência de " + amount + " realizada de '" + source + "' para '" + target + "'");
        }catch (AccountNotFoundException | NoFundsEnoughException ex){
            System.out.println(ex.getMessage());
        }
    }

    private static void checkHistory(){
        System.out.println("Informe a chave Pix da conta: ");
        var pix = input.next();
        try{
            var sortedHistory = accountRepository.getHistory(pix);
            sortedHistory.forEach((k,v)->{
                System.out.println(k.format(ISO_DATE_TIME));
                System.out.println("Primeira transação: " + v.getFirst().transactionId());
                System.out.println("Última descrição: " + v.getLast().description());
                System.out.println("Qtde transações: " + v.size());
            });
        }catch (AccountNotFoundException ex){
            System.out.println(ex.getMessage());
        }
    }

    private static void createWalletInvestment(){
        System.out.println("Informe a chave Pix da conta: ");
        var pix = input.next();
        try {
            investmentRepository.createWallet(pix);
            System.out.println("Carteira de investimentos criada para '" + pix + "'");
        } catch (AccountNotFoundException | AccountWithInvestmentException ex) {
            System.out.println(ex.getMessage());
        }
    }

    private static void initInvestment(){
        System.out.println("Informe a chave Pix da conta: ");
        var pix = input.next();
        System.out.println("Informe o valor inicial do investimento: ");
        var amount = input.nextLong();
        System.out.println("Informe a taxa (em % inteiro) do serviço de investimento: ");
        var rate = input.nextInt();
        try {
            investmentRepository.initInvestment(pix, amount, rate);
            System.out.println("Investimento iniciado para '" + pix + "' com aporte de " + amount + " e taxa " + rate + "%");
        } catch (AccountNotFoundException | NoFundsEnoughException | AccountWithInvestmentException ex) {
            System.out.println(ex.getMessage());
        }
    }

    private static void depositInInvestment(){
        System.out.println("Informe a chave Pix da conta: ");
        var pix = input.next();
        System.out.println("Informe o valor do aporte: ");
        var amount = input.nextLong();
        try {
            investmentRepository.deposit(pix, amount);
            System.out.println("Aporte de " + amount + " realizado na carteira de '" + pix + "'");
        } catch (WalletNotFoundException | NoFundsEnoughException ex) {
            System.out.println(ex.getMessage());
        }
    }

    private static void withdrawFromInvestment(){
        System.out.println("Informe a chave Pix da conta: ");
        var pix = input.next();
        System.out.println("Informe o valor do saque do investimento: ");
        var amount = input.nextLong();
        try {
            investmentRepository.withdraw(pix, amount);
            System.out.println("Saque de investimento de " + amount + " realizado para '" + pix + "'");
        } catch (WalletNotFoundException | NoFundsEnoughException ex) {
            System.out.println(ex.getMessage());
        }
    }

    private static void increaseInvestments(){
        System.out.println("Informe a chave Pix da conta: ");
        var pix = input.next();
        try {
            investmentRepository.increase(pix);
            System.out.println("Investimento da carteira '" + pix + "' atualizado com rendimento");
        } catch (WalletNotFoundException | InvestmentNotFoundException ex) {
            System.out.println(ex.getMessage());
        }
    }

    private static void updateAllInvestments(){
        investmentRepository.updateAmount();
        System.out.println("Todos os investimentos foram atualizados com rendimento");
    }

    private static void listInvestmentWallets(){
        List<InvestmentWallet> wallets = investmentRepository.listWallets();
        if (wallets.isEmpty()){
            System.out.println("Nenhuma carteira de investimentos encontrada.");
            return;
        }
        wallets.forEach(w -> {
            var pixList = w.getAccount().getPix();
            String anyPix = pixList.isEmpty() ? "<sem pix>" : pixList.getFirst();
            System.out.println("PIX: " + anyPix
                    + " | saldo carteira=" + w.getBalance()
                    + " | taxa=" + w.getInvestment().tax() + "%"
            );
        });
    }
}
