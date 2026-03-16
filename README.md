# Sistema de Gestão Financeira Pessoal (Mobile)

Este repositório contém a documentação e o código-fonte de uma aplicação móvel desenvolvida para o controle, planejamento e centralização da gestão financeira pessoal.

##  Visão Geral

O projeto foi concebido com uma abordagem dual, visando atender tanto a requisitos funcionais de negócio quanto a objetivos de aprimoramento técnico.

### Objetivos do Projeto
1.  **Centralização Financeira:** Prover uma plataforma unificada para o monitoramento e gestão de despesas de múltiplos métodos de pagamento e diversas instituições bancárias.
2.  **Aprimoramento Técnico (Android):** Desenvolver uma aplicação nativa utilizando linguagens e frameworks consolidados no mercado atual. O projeto atua como um estudo de caso para a aplicação de conhecimentos em um ambiente de desenvolvimento e arquitetura previamente inexplorados.

## Ciclo de Desenvolvimento

O roteiro segue as etapas abaixo:

1.  **Prototipagem de Alta Fidelidade:** Definição da experiência do usuário (UX) e interface visual (UI).
2.  **Desenvolvimento do Front-end:** Implementação das interfaces e componentes visuais nativos (XML/View Binding).
3.  **Implementação de Regras de Negócio:** Desenvolvimento da lógica de interações, validação de dados, gestão de faturas e sincronização de saldos.
4.  **Integração com Backend (BaaS):** Conexão com banco de dados NoSQL em tempo real.
5.  **Refinamento:** Ajustes de performance, segurança (Biometria) e polimento visual.


## Tecnologias e Ferramentas (Execução)

O aplicativo foi desenvolvido de forma nativa para o ecossistema Android, utilizando as seguintes tecnologias:

* **Linguagem:** Kotlin
* **Arquitetura:** MVVM (Model-View-ViewModel)
* **Interface:** XML com View Binding
* **Assincronismo:** Kotlin Coroutines & Lifecycle Scope
* **Backend / Banco de Dados:** Firebase Firestore (NoSQL em tempo real)
* **Segurança:** AndroidX Biometric (Autenticação por Digital)
* **Gráficos:** MPAndroidChart
* **Trafego de Dados:** Parcelable para transição de objetos entre telas

### Principais Funcionalidades Implementadas
* **Login Biométrico:** Acesso utilizando os sensores biométricos do dispositivo.
* **Dashboard Dinâmico:** Gráficos e cálculos automáticos de saldo, limite de crédito e uso, com filtros por método de pagamento.
* **Gestão de Faturas (Smart):** Lógica de fechamento de faturas, abatendo parcelas pagas, recalculando limites e transpondo parcelas futuras para os meses seguintes.
* **Sincronização de Saldos:** Espelhamento automático de saldo em conta corrente entre cartões de débito e crédito da mesma instituição bancária.
* **Adaptação de UI em Tempo Real:** A interface se molda ao tipo de pagamento selecionado (ex: ocultando limites de crédito quando um cartão de débito ou dinheiro físico é selecionado).

## Execução

Para executar este projeto localmente, é necessário ter o ambiente de desenvolvimento Android configurado e adicionar os arquivos de segurança que não são versionados no repositório.

### Pré-requisitos
* **Android Studio** (versão Hedgehog ou superior recomendada).
* Aparelho físico ou Emulador Android com suporte a biometria (opcional para testar o login biométrico).
* SDK do Android (Mínimo API 24, Target API 34+).

### Passo a Passo

1. **Clone o repositório:**
   ```bash
   git clone [https://github.com/Victor-Vaglieri/AppControle.git](https://github.com/SEU-USUARIO/NOME-DO-REPOSITORIO.git)
   ```

2. Abra o projeto:

    Abra o Android Studio, selecione Open e navegue até a pasta clonada.

3. Configuração do Firebase:

    Como o projeto utiliza o Firebase Firestore, você precisará do arquivo de configuração do Google:
    * Crie um projeto no Firebase Console.
    * Ative o banco de dados Firestore.
    * Registre um app Android com o mesmo package name do projeto (com.example.controledovitao).
    * Baixe o arquivo google-services.json e coloque-o dentro da pasta app/ do projeto.

4. Configuração de Variáveis Locais (Biometria):

    O projeto utiliza credenciais seguras para o bypass do login via biometria. Abra o arquivo local.properties (na raiz do projeto) e adicione as seguintes linhas com as suas credenciais de teste:

```Properties
USER_EMAIL="seu_email_de_teste@gmail.com"
USER_PASSWORD="sua_senha_de_teste"
```

5. Sincronização e Build:

* Aguarde o Gradle sincronizar todas as dependências do projeto.
* Selecione um emulador ou conecte o seu smartphone.
* Clique em Run (Shift + F10) ou no ícone de "Play" na barra superior do Android Studio.

## Prototipagem e Design (UI/UX)

A interface do usuário e a experiência de navegação foram projetadas utilizando a plataforma **Figma**. O desenvolvimento visual utilizou como base o *design system* ["Mobile Apps – Prototyping Kit (Community)"](https://www.figma.com/community/file/1129468881607079432).

O protótipo de alta fidelidade é navegável e permite a visualização dos principais fluxos de interação proposto.

**[Acessar Protótipo Interativo no Figma](https://www.figma.com/proto/g9fKlEtAOWtKDcpinnjX5W/Controle-do-Vit%C3%A3o?node-id=1249-7125&t=LiYiQItppX6qqnvE-1)**

### Galeria do Protótipo

Abaixo apresenta a visualização das principais interfaces e funcionalidades do sistema:

<details>
  <summary><strong>Autenticação e Dashboard (Clique para expandir)</strong></summary>
  <br>
  
  **Autenticação (Login)**
  <br>
  <img width="384" height="820" alt="{A68197D6-5620-4D2D-AB2E-4620B30AB7B8}" src="https://github.com/user-attachments/assets/f09e762e-d3d9-40e6-b61b-78886e4aae20" />
  <br><br>

  **Dashboard (Home)**
  <br>
  <img width="379" height="820" alt="{58406311-C86A-41E8-887E-51345C407AED}" src="https://github.com/user-attachments/assets/70690d60-1b41-48fc-a34d-c59164c677c2" />
  <br><br>
  
  **Home (Seleção de Pagamento)**
  <br>
  <img width="373" height="815" alt="{CB5E0CA3-3E7B-4FC0-AACB-D2B51F943CA2}" src="https://github.com/user-attachments/assets/c610936a-0939-49cb-8315-4aba898d3b2a" />
  <img width="376" height="815" alt="{2A34762E-6DEA-4F95-AE9F-3070A8299CEF}" src="https://github.com/user-attachments/assets/ac2795c4-c7bc-406d-9b3d-75e92062523c" />
</details>

<details>
  <summary><strong>Gestão de Despesas e Faturas</strong></summary>
  <br>
  
  **Detalhes de Despesa**
  <br>
  <img width="377" height="818" alt="{CC31BDFF-C15D-44B6-A890-F04A7168123E}" src="https://github.com/user-attachments/assets/cace8de5-51e5-445f-b257-e168b26002de" />
  <br><br>

  **Detalhamento de Fatura**
  <br>
  <img width="382" height="816" alt="{DDB3ABFD-237E-48B4-97CE-EED13DF0BB04}" src="https://github.com/user-attachments/assets/8f0dcecb-3552-4e1c-a7e0-e7c7f5d02342" />
  <br><br>

  **Registro de Nova Despesa**
  <br>
  <img width="372" height="816" alt="{2ED5DDAC-1A89-4D69-BB17-7054901BEADB}" src="https://github.com/user-attachments/assets/a11ff3a4-0df2-45b5-8012-b372ebe0e513" />
</details>

<details>
  <summary><strong>Investimentos e Simuladores</strong></summary>
  <br>
  
  **Simulador (Renda Fixa/Banco)**
  <br>
  <img width="378" height="818" alt="{973899BA-BBF2-4B06-825F-241E5FC9D2E2}" src="https://github.com/user-attachments/assets/5190ed10-9433-44ba-b7ec-cbb7b2b09a50" />
  <br><br>

  **Simulador (Criptoativos)**
  <br>
  <img width="366" height="821" alt="{3F2900C5-FA36-4F30-9C3A-C590D15DD5B2}" src="https://github.com/user-attachments/assets/e2d234ac-219d-4cc3-b28e-bc4e0716e425" />
  <br><br>

  **Portfólio de Investimentos**
  <br>
  <img width="376" height="817" alt="{6BC1EBE8-BF38-443C-96A3-B94D1B938AD3}" src="https://github.com/user-attachments/assets/c236ac23-2a9a-4fba-81c6-91fa3047d4b5" />
  <br><br>
  
  **Edição de Investimento**
  <br>
  <img width="373" height="817" alt="{34CA9B38-E1DA-4AEE-858F-7C692E5A60E0}" src="https://github.com/user-attachments/assets/67754a3e-8eeb-4ce5-a937-193a61a8d84d" />
</details>

<details>
  <summary><strong>Métodos de Pagamento</strong></summary>
  <br>
  
  **Menu de Métodos de Pagamento**
  <br>
  <img width="377" height="817" alt="{59DEDDAF-404E-459E-A2A5-54D80C0FA90D}" src="https://github.com/user-attachments/assets/31cc157e-1497-422a-a392-3303b9559d51" />
  <br><br>

  **Cadastro de Método de Pagamento**
  <br>
  <img width="378" height="820" alt="{686E478A-603F-469A-B89B-892AFD9C1C46}" src="https://github.com/user-attachments/assets/a156ad29-0f49-422a-9d32-4a19bb742c42" />
  <br><br>

  **Gerenciamento de Métodos Salvos**
  <br>
  <img width="378" height="818" alt="{0F1A1F2B-B921-43C5-A457-E878AEEE1E6F}" src="https://github.com/user-attachments/assets/25080db5-8ee5-4b55-8dd4-da26d569ebc4" />
  <br><br>
  
  **Edição de Método de Pagamento**
  <br>
  <img width="372" height="822" alt="{622364F5-6A73-41A3-9BB9-0D46F8410E62}" src="https://github.com/user-attachments/assets/53ba3a63-3e26-4605-9d15-4b7de8544182" />
</details>

<details>
  <summary><strong>Sistema e Configurações</strong></summary>
  <br>
  
  **Configurações do Sistema**
  <br>
  <img width="376" height="815" alt="{EEE33161-9316-4AFE-9B1A-403F8F2A7AA3}" src="https://github.com/user-attachments/assets/c421e562-73b4-4a9b-bf45-0fa1ff05dd7c" />
  <br><br>

  **Central de Notificações**
  <br>
  <img width="382" height="810" alt="{1A0441A3-946A-4222-98C9-E1FCB06E4EF4}" src="https://github.com/user-attachments/assets/bbb292e2-80fb-4f45-bbc7-f10cf335a68c" />
</details>

<br>

## Estrutura do Projeto

```text
com.example.controledovitao
│
├── data/                  # Camada de Dados
│   ├── model/             # Data classes e Enums (Payment, Spent, Options) com Parcelize
│   └── repository/        # Integração com Firebase Firestore e regras de persistência
├── viewmodel/             # Camada Lógica (ViewModel)
├── ui/                    # Camada de Apresentação (View)
│   ├── adapter/           # Adapters para RecyclerViews
│   ├── components/        # Componentes visuais customizados (ExpenseItemView, etc)
│   └── ...                # Activities (HomeActivity, BillActivity, etc)
└── utils/                 # Extensões e formatadores de moeda/data
```
