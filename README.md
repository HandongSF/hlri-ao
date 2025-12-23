# 🤖 HLRI-AO  
## Agent Orchestration for Human-Level Robot Intelligence

AO (Agent Orchestration)는 Human-Level Robot Intelligence(HLRI) 시스템에서  
공간(ISA), 사용자(IUA), 객체(IOA)에 적응하는 다수의 AI 에이전트를  
**하나의 일관된 인지–행동 시스템으로 통합하는 중앙 오케스트레이션 계층**이다.

HLRI 환경에서는 지능이 단일 모델이 아니라,  
서로 다른 전문 에이전트들의 **실시간 협업**을 통해 발현된다.  
AO는 이들 에이전트가 독립적으로 동작하는 것을 넘어,  
**하나의 coherent system**으로 작동하도록 실행 흐름과 의사결정을 조율한다.

특히 AO는 **실행 흐름 관리, 스케줄링, 에이전트 선택 및 평가**를 담당하며,  
동적 환경 변화와 사용자 요구에 대해 시스템이 **일관되고 안정적으로 반응**할 수 있도록 한다.

---

## ✨ Core Concepts

### 🧩 Agent Orchestration
AO는 ISA, IUA, IOA가 생성하는 인지 결과와 행동 후보를 수집·조정하여  
최종 행동을 결정하는 **중앙 조정 레이어**이다.  
에이전트 간 우선순위 충돌을 해소하고, 상황에 따라 활성 에이전트를 동적으로 선택한다.

### 🔁 Closed-loop Control
Perception → Cognition → Action 흐름을 단절 없이 연결하며,  
각 에이전트의 상태와 결과를 통합해 **goal-directed behavior**를 생성한다.

### ⚡ Event-driven & Asynchronous
비동기 이벤트 기반 구조를 통해  
주행 중 사용자 명령, 환경 변화 등 **동시 발생 이벤트**에 유연하게 대응한다.

---

## 🧠 HLRI Multi-Agent Structure

- **ISA (Instant Spatial Adaptation Agent)**  
  실시간 공간 인식, Scene Graph 생성, 환경 기반 이동 판단

- **IUA (Instant User Adaptation Agent)**  
  사용자 발화, 감정, 의도 이해 및 상호작용 생성

- **IOA (Instant Object Adaptation Agent)**  
  객체 인식, 상태 추론, 행동/조작 전략 생성

- **AO (Agent Orchestration)**  
  위 세 에이전트를 통합 제어하는 중앙 오케스트레이션 계층

---

## 📡 Communication Architecture

AO 기반 HLRI 시스템은 고성능 실시간 통신을 위해 다음을 사용한다:

- **gRPC + Protocol Buffers**
  - 에이전트 간 고속 RPC 통신
  - 양방향 스트리밍 지원 (비디오/상태 업데이트)

---


## 🛠 Installation & Execution

본 가이드는 **Linux (Ubuntu 22.04)** 및 **Python 3.10+** 환경을 기준으로 작성되었습니다.  
AO 프레임워크는 각 에이전트가 독립적인 프로세스로 실행되고,  
Agent Orchestration 모듈이 이를 통합 제어하는 구조를 따릅니다.

### 1️⃣ Environment Setup

```bash
python3 -m venv venv
source venv/bin/activate
pip install -r requirements.txt
```

### 2️⃣ Agent Services Execution

각 에이전트(ISA / IUA / IOA)는 독립 프로세스로 실행됩니다.
```bash
python isa/server.py
python iua/server.py
python ioa/server.py
```

### 3️⃣ Agent Orchestration (AO) Execution

모든 에이전트가 준비된 상태에서 AO를 실행합니다.
```bash
python launcher.py
```
