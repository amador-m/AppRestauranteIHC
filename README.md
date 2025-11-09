# Intervalinho 🍔

**Aplicativo de sistema de pedidos para a cantina de um campus universitário, desenvolvido em Kotlin como projeto de faculdade.**

O Intervalinho é um aplicativo Android completo que simula um sistema de pedidos de ponta a ponta. Ele gerencia múltiplos tipos de usuários (Clientes, Funcionários e Administradores) com autenticação e um banco de dados em tempo real para sincronização instantânea de pedidos e status.

---

## 📸 Prints do App

| Login | Cardápio | Carrinho |
| :---: | :---: | :---: |
| <img width="250" alt="image" src="https://github.com/user-attachments/assets/7f2e9bd8-d778-4cc8-ab46-9d1f84d482ac" /> | <img width="250" alt="image" src="https://github.com/user-attachments/assets/da22a195-fba1-42f7-9e24-22adc311926a" /> | <img width="250" alt="image" src="https://github.com/user-attachments/assets/55f10b61-479d-437a-99c6-b615cb1115ce" /> |

| **Histórico** | **Perfil (Fidelidade)** | **Painel Admin** |
| :---: | :---: | :---: |
| <img width="250" alt="image" src="https://github.com/user-attachments/assets/e2c7006e-cef3-4ad5-b9f4-d87b2e30aa69" /> | <img width="250" alt="image" src="https://github.com/user-attachments/assets/600915b3-4929-4608-b3e2-d4be19d03505" /> | <img width="250" alt="image" src="https://github.com/user-attachments/assets/26deacb4-fd66-42e9-b181-6eec7828654d" /> |

---

## ✨ Funcionalidades

O sistema é dividido em três perfis de usuário, cada um com suas próprias permissões e funcionalidades:

### 👤 Cliente

O cliente é o consumidor final do aplicativo.

* **Autenticação:** Sistema completo de Login e Cadastro (com nome, email e senha).
* **Cardápio:** Visualiza o cardápio completo, com funcionalidade de **busca** e **filtro por categorias** (Lanches, Salgados, Bebidas, etc.).
* **Destaques:** Uma tela ("Mais Pedidos") que exibe os pratos mais vendidos (Top Hits) com base no histórico de pedidos de todos os usuários.
* **Carrinho de Compras:** Adiciona, remove e atualiza a quantidade de itens no carrinho. O `CartManager` gerencia o estado do carrinho de forma global (enquanto o app está aberto).
* **Checkout:** Finaliza o pedido, calculando subtotal, taxa de entrega e total. Permite a seleção da forma de pagamento (Pix, Cartão ou Dinheiro).
* **Histórico de Pedidos:** Acompanha todos os pedidos realizados, seus status em tempo real e visualiza os detalhes de cada um.
* **Pontos de Fidelidade:** A cada pedido finalizado, o cliente ganha 1 ponto. Ao acumular 10 pontos, eles são convertidos em 1 cupom de desconto.
* **Perfil:** Edita suas informações pessoais, como nome, @username, telefone, data de nascimento e pode selecionar um **avatar customizado** da galeria do app.
* **Informações:** Acessa uma tela com os horários de funcionamento do estabelecimento.

### 👨‍🍳 Funcionário

O funcionário é responsável por gerenciar os pedidos que chegam.

* **Login:** Entra em uma área restrita para funcionários.
* **Dashboard de Pedidos:** Visualiza todos os pedidos ativos (Pendentes, Em Preparo, Prontos) em tempo real.
* **Gestão de Status:** Pode atualizar o status de um pedido (ex: "Pendente" -> "Em Preparo") diretamente pelo dashboard, e o cliente recebe a atualização instantaneamente.
* **Visualização do Cardápio:** Pode visualizar o cardápio (o mesmo que o cliente vê), mas não pode adicionar itens ao carrinho ou editar pratos.
* **Perfil:** Acessa sua própria tela de perfil para editar seus dados.

### 👑 Administrador

O administrador tem controle total sobre o sistema.

* **Possui todas as permissões de Funcionário.**
* **Painel de Admin (Gestão de Cardápio):** Tem acesso a uma tela exclusiva para gerenciar o cardápio.
* **CRUD de Pratos:** Pode **Adicionar**, **Editar** e **Excluir** qualquer prato do cardápio.
* **Controle de Disponibilidade:** Pode marcar um prato como "Disponível" ou "Indisponível" através de um switch. Pratos indisponíveis aparecem "apagados" para os clientes e não podem ser comprados.

---

## 🛠️ Tecnologias Utilizadas

* **Linguagem:** Kotlin
* **Banco de Dados:** Firebase Realtime Database (para sincronização em tempo real de pedidos, cardápio e perfis)
* **Autenticação:** Firebase Authentication (autenticação por Email e Senha)
* **Arquitetura:** Estrutura baseada em MVVM (Activity/Fragment como View, `FirebaseManager` e `CartManager` atuando como Repositório/Serviço)
* **UI:** Android XML Layouts com Material Design 3 (MaterialCardView, BottomNavigationView, TextInputLayout, etc.)
* **Carregamento de Imagens:** Glide (para carregar imagens do cardápio e avatares)
* **Componentes:** `RecyclerView` (para todas as listas), `View Binding`, `Parcelize` (para passar objetos entre Activities)

--- 

Desenvolvido como um projeto da faculdade na disciplina de Interação Humano Computador por Maria Eduarda Amador Mota e Davi de Almeida Cejudo
