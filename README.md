# Java Bank — Desafio DIO (POO + Console | BigDecimal)

Simulação de sistema bancário em Java com POO: **contas**, **depósitos**, **saques**, **transferências via PIX**, **múltiplas carteiras de investimento por conta**, aportes/saques e **histórico de transações**.  
Todo o fluxo monetário usa **BigDecimal** (valores em **reais**, escala 2).

## 🚀 Conceitos aplicados
- **POO**: abstração, herança, polimorfismo e encapsulamento (`Wallet` → `AccountWallet` / `InvestmentWallet`)
- **Composição**: `InvestmentWallet` possui `AccountWallet` e `Investment`
- **Records**: `Investment`, `Transaction`
- **Repositórios em memória**: `AccountRepository`, `InvestmentRepository`
- **Console interativo** com validação de entrada
- **BigDecimal** para valores monetários (evita erros de ponto flutuante)

## 📦 Requisitos
- **JDK 21**
- **Gradle 8+**
- Terminal/ambiente em **UTF-8** (o run via Gradle já define `-Dfile.encoding=UTF-8`)

## ▶️ Como executar
```bash
./gradlew clean build
./gradlew run
# ou
java -Dfile.encoding=UTF-8 -jar build/libs/<seu-jar>.jar
```

## 📋 Menu (resumo)
1. Criar conta
2. Criar carteira (pix, aporte inicial em **reais**, taxa %)
3. Sacar da conta
4. Depositar na conta
5. Transferir entre contas
6. Histórico da conta
7. Listar carteiras por PIX
8. Aportar em carteira (PIX + investmentId)
9. Sacar da carteira (PIX + investmentId)
10. Atualizar rendimento de 1 carteira (PIX + investmentId)
11. Atualizar rendimento de **todas** as carteiras
12. Listar **todas** as carteiras
13. Listar **todas** as contas
14. Sair

> Valores de dinheiro aceitam vírgula ou ponto (ex.: `100,50` ou `100.50`).

## 🧭 Fluxos rápidos
- **Criar conta** → `pix1;pix2` e **depósito inicial (reais)**.
- **Criar carteira** → `PIX`, **aporte inicial (reais)** e **taxa (%)**. Cada conta pode ter **N** carteiras (identificadas por `invId`).
- **Operações em carteira** → sempre por **PIX + investmentId** (aportar, sacar, rendimento).
- **Listagens** → **12** (todas as carteiras), **13** (todas as contas) e **7** (carteiras por PIX).

## 🗂️ Estrutura do projeto
```
src/main/java
  └─ br/com/dio
     ├─ model/
     │  ├─ Wallet (abstrata), AccountWallet, InvestmentWallet
     │  ├─ Investment (record), Transaction (record)
     ├─ repository/
     │  ├─ AccountRepository, InvestmentRepository, CommonsRepository
     └─ Main (CLI)
build.gradle.kts
```

## 🛠️ Decisões de projeto
- **BigDecimal** em toda a modelagem monetária (escala 2, `RoundingMode.DOWN`)
- **Múltiplas carteiras por conta** (chave: `PIX + investmentId`)
- Saída **padronizada** em **uma linha** por operação/entidade
- Histórico agregado por `LocalDateTime` na conta

## ❗ Problemas comuns
- **Acentos quebrados**: garanta `UTF-8` (o Gradle `run` já ajuda).
- **Versão do Java**: compile com **JDK 21**.
