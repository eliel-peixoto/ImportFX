# Sistema ImportFX

## Descrição

Sistema para gestão de uma loja de produtos importados, controlando fornecedores internacionais, catálogo de produtos cotados em moeda estrangeira e pedidos de importação. O sistema organiza o fluxo desde a cotação do produto no fornecedor até a entrega ao cliente final, aplicando impostos de importação e calculando o preço final em Real.

Como diferencial, o preço de cada pedido é calculado usando a cotação de câmbio real do dia, obtida via API pública do Banco Central do Brasil. A cotação usada é "congelada" no momento da confirmação do pedido, simulando o comportamento real de uma operação de câmbio comercial, e o sistema deve lidar de forma resiliente com indisponibilidade momentânea da API.

## Requisitos Funcionais

### 1. Cadastro de Fornecedores e Produtos
- REQ01: Cadastrar fornecedores internacionais (nome, país de origem, moeda padrão: USD, EUR, GBP)
- REQ02: Cadastrar produtos vinculados a um fornecedor, com preço na moeda de origem e categoria (ex.: Eletrônicos, Vestuário, Brinquedos)
- REQ03: Implementar herança para categorias de produto, cada uma com alíquota de imposto de importação própria

### 2. Cotação de Câmbio (API Pública)
- REQ04: Consultar a cotação de compra/venda do dia para USD, EUR e GBP usando a API pública **PTAX do Banco Central do Brasil** (Olinda/OData, sem necessidade de chave)
- REQ05: Exibir a cotação vigente na tela de criação de pedido antes da confirmação
- REQ06: Manter cache local da última cotação obtida por moeda, usando-a como fallback e sinalizando ao usuário quando a API estiver indisponível

### 3. Pedidos de Importação
- REQ07: Cadastrar clientes (nome, CPF, endereço)
- REQ08: Criar pedido de importação vinculado a um cliente e um fornecedor
- REQ09: Adicionar itens ao pedido (composição), cada item referenciando um produto e uma quantidade
- REQ10: Calcular subtotal do pedido em Real, convertendo cada item pela cotação do dia da criação do pedido

### 4. Impostos e Fechamento
- REQ11: Aplicar automaticamente a alíquota de imposto de importação da categoria de cada produto sobre o subtotal convertido
- REQ12: Calcular frete internacional com base no peso total declarado dos itens
- REQ13: Gerar fatura final do pedido (subtotal + impostos + frete) em PDF

### 5. Acompanhamento de Status
- REQ14: Controlar status do pedido: Aguardando Pagamento, Pago, Em Trânsito, Alfândega, Entregue
- REQ15: Notificar (em tela) o cliente sempre que o status do pedido mudar

### 6. Relatórios
- REQ16: Relatório de pedidos por período, com totais convertidos e impostos arrecadados
- REQ17: Relatório de produtos mais importados por categoria, exportável em CSV

### 7. Regras e Restrições
- REQ18: **Congelar** a cotação de câmbio utilizada no momento da confirmação do pedido, mesmo que a cotação mude posteriormente
- REQ19: **Bloquear** a confirmação de um pedido caso nenhuma cotação (nem em cache) esteja disponível para a moeda do fornecedor
- REQ20: **Não permitir** alteração de itens de um pedido após o status mudar para "Pago"
- REQ21: **Validar** que o valor total do pedido, após conversão, não exceda o limite de isenção de importação por CPF (ex.: equivalente a US$ 50) sem gerar automaticamente o imposto correspondente
- REQ22: **Bloquear** a exclusão de fornecedores que possuam pedidos em andamento
- REQ23: **Garantir** que todo pedido tenha ao menos um item antes de ser confirmado

## Possíveis APIs/Bibliotecas

JavaFX, API PTAX do Banco Central (https://olinda.bcb.gov.br/olinda/servico/PTAX/versao/v1/odata — pública, sem chave), `java.net.http.HttpClient`, Jackson/Gson, iText (PDF), Java Time API, JUnit.

**Requisito bônus (opcional, fora da contagem oficial):** exportar o extrato de um pedido como página HTML estática, exibindo a cotação usada e o detalhamento de impostos, aberta automaticamente no navegador via `Desktop.getDesktop().browse()`.

## Diagrama
![Diagrama de UML](uml/UML import FX.pdf)

## Integrantes do grupo com nome completo
* Artur Cândido Dourado do Nascimento
* Eliel Peixoto Teófilo de Jesus
* Thiago Sousa Fischer de Vasconcelos - thiago.fischer@ufrpe.br
* Vitor Miguel da Paz Azevedo - vitor.miguel@ufrpe.br
* Wendel Lucas Oliveira Freitas -
wendel.lucas@ufrpe.br
