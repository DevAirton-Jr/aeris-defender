<div align="center">
  <img src="https://img.shields.io/badge/Status-Em_Desenvolvimento-blue?style=for-the-badge&logo=appveyor" />
  <img src="https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=java&logoColor=white" />
  <img src="https://img.shields.io/badge/JavaFX-1D2951?style=for-the-badge&logo=javafx&logoColor=white" />
  <br/>
  <h1>🛡️ SentinelAV</h1>
  <p><strong>Antivírus Heurístico de Alta Performance com Interface Cyberpunk</strong></p>
</div>

---

## 📖 Sobre o Projeto
O **SentinelAV** é um ecossistema de proteção local construído em Java. Ele não é apenas um scanner de diretórios, mas um motor antivírus completo (em construção) que conta com análise heurística, checagem de Magic Bytes, detecção de pacotes (Packer Detection) e proteção de arquivos em tempo real. Tudo isso embalado em uma interface JavaFX hiper-moderna de alto contraste no estilo Hacker/Cyberpunk.

## 🏗️ Arquitetura do Sistema
O projeto foi moldado seguindo princípios de **Clean Architecture** e forte separação de responsabilidades (SOLID). O coração do Sentinel roda desacoplado da interface gráfica (UI), garantindo máxima performance e escalabilidade.

### Módulos Principais:
1. **Core Engine (`com.sentinelav.core`)**
   - `SentinelEngine`: O maestro (Singleton) que orquestra e mantém o estado global do Scanner e do RealTimeProtector.
2. **Analysis (`com.sentinelav.analysis`)**
   - `FileScanner`: Motor ultrarrápido de navegação de pastas (NIO.2 + Multi-threading via ExecutorService).
   - `MagicBytesChecker`: Analisador de DNA Digital que lê cabeçalhos binários reais, ignorando extensões falsas.
   - `PackerDetector`: Scanner de assinatura de empacotadores (ex: UPX, ASPack) muito usados em malwares.
   - `ThreatDetector`: Comparador rápido de assinaturas (Hashes SHA-256).
   - `BehaviorAnalyzer`: Avalia escores baseados em nomenclatura e atributos do arquivo.
   - `RiskAnalyzer`: A calculadora final. Pesa as heurísticas e decide o veredito final.
   - `QuarantineManager`: Move arquivos para isolamento, bloqueando acessos indevidos e permitindo a exclusão segura.
3. **Real-Time Protection (`com.sentinelav.realtime`)**
   - `RealTimeProtector`: Utiliza o `WatchService` do sistema operacional para monitorar diretórios-chave (ex: Downloads, Desktop) interceptando arquivos maliciosos na criação.
4. **Interface Gráfica (`com.sentinelav.ui`)**
   - Aplicação em **JavaFX** construída com FXMLs divididos por Controllers lógicos (Dashboard, Scanner, Settings, Quarantine).
   - Estilização completa customizada via CSS (Tema Dark Neon/Cyan).

## 🚀 Como Executar

### Pré-requisitos
- JDK 17 ou superior.
- Gradle.

### Via Terminal
1. Clone este repositório:
   ```bash
   git clone https://github.com/DevAirton-Jr/aeris-defender.git
   ```
2. Na raiz do projeto, execute o Gradle Wrapper:
   ```bash
   # No Windows
   .\gradlew run
   
   # No Linux/Mac
   ./gradlew run
   ```

## 🛠️ Roadmap Futuro
- [ ] Integração com Machine Learning / I.A. de rede neural local.
- [ ] Conexão com Cloud Sandboxing.
- [ ] Refinamento e persistência da Quarentena em banco de dados SQLite.
- [ ] Firewall integrado e Monitor de Tráfego Web.

---
> *"Onde quer que um byte malicioso tente se esconder, o Sentinel começa aqui."*
