# AO: Agent Orchestration
## A Coordination Layer for Human-Level Robot Intelligence (HLRI)
Agent Orchestration (AO) is a unified coordination layer designed to integrate and orchestrate multiple adaptive AI agents within a Human-Level Robot Intelligence (HLRI) system.

In HLRI environments, intelligent behavior emerges from the collaboration of specialized agents that adapt to space, users, and objects in real time. AO serves as the central orchestration component that enables these agents to operate as a single, coherent system rather than as isolated modules.

By managing execution flow and scheduling across agents, AO allows the system to respond consistently to dynamic environments and user demands.

## Execution
### Agent Orchestration (AO) 모듈 실행
```bash
실행 명령어: > mvn clean compile exec:java -pl ao-core
```
### Mock-up Agent 실행
```bash
실행 명령어: >
창 1: mvn clean compile exec:java -pl ao-framework -P isa
창 2: exec:java -pl ao-framework -P iua
창 3: exec:java -pl ao-framework -P ioa
```
