# Lock

## Write Lock (Exclusive Lock)
- `read` 및 `write` 할 때 사용할 수 있다.
- 다른 트랜잭션이 같은 데이터를 `read` 또는 `write` 하는 것을 허용하지 않는다. (Exclusive)


## Read Lock (Shared Lock)
- `read` 할 때 사용한다.
- 다른 트랜잭션이 같은 데이터를 `read` 하는 것을 허용한다. (Shared)
- 다른 트랜잭션이 `write` 를 시도하면 Lock이 `release` 될 때까지 기다린다.


## Lock Case Examples

### 1. Tx1이 X-Lock을 획득하고 Tx2가 X-Lock 획득을 시도하는 경우
- Tx1이 X-Lock을 획득한다.
- Tx2가 X-Lock 획득을 시도하면 Lock을 획득하지 못하고 블록되며 Lock을 획득할 때까지 기다린다.
- Tx1이 수행을 완료하고 Lock을 반환한다.
- Tx2가 X-Lock을 획득하고 트랜잭션을 수행한다.


### 2. Tx1이 X-Lock을 획득하고 Tx2가 S-Lock 획득을 시도하는 경우
- Tx1이 X-Lock을 획득한다.
- Tx2가 S-Lock 획득을 시도하면 Lock을 획득하지 못하고 블록되며 Lock을 획득할 때까지 기다린다.
- Tx1이 수행을 완료하고 Lock을 반환한다.
- Tx2가 S-Lock을 획득하고 트랜잭션을 수행한다.

### 3. Tx1이 S-Lock을 획득하고 Tx2가 X-Lock 획득을 시도하는 경우
- Tx1이 S-Lock을 획득한다.
- Tx2가 X-Lock 획득을 시도하면 Lock을 획득하지 못하고 블록되며 Lock을 획득할 때까지 기다린다.
- Tx1이 수행을 완료하고 Lock을 반환한다.
- Tx2가 X-Lock을 획득하고 트랜잭션을 수행한다.

### 4. Tx1이 S-Lock을 획득하고 Tx2가 S-Lock 획득을 시도하는 경우 
- Tx1, Tx2가 서로에게 영향을 주지않고 S-Lock을 획득하여 트랜잭션을 수행할 수 있다.

|            | Read Lock | Write Lock |
|------------|-----------|------------|
| Read Lock  | O         | X          |
| Write Lock | X         | X          |


## Lock과 Anomaly
- Lock만으로는 트랜잭션의 Serializability를 보장할 수 없다.
  - Serializability에 대해서 정리하기, (Serial Sechdule에 대한 개념 정리 필요)
- Tx1와 Tx2가 동시에 수행되는 상황을 가정하며, 각각의 트랜잭션의 수행 동작은 다음과 같다.
  - Tx1 : X와 Y의 합을 X에 저장한다.
  - Tx2 : X와 Y의 합을 Y에 저장한다.

| Step   | Tx1              | Tx2              |
|--------|------------------|------------------|
| STEP 1 | S-Lock(Y)        | S-Lock(X)        |
| STEP 2 | Read(Y)          | Read(X)          |
| STEP 3 | Unlock(Y)        | Unlock(X)        |
| STEP 4 | X-Lock(X)        | X-Lock(Y)        |
| STEP 5 | Read(X)          | Read(Y)          |
| STEP 6 | Write(X = X + Y) | Write(Y = X + Y) |
| STEP 7 | Unlock(X)        | Unlock(Y)        |

- Serial Schedule Tx1 → Tx2 : X = 300, Y = 500
- Serial Schedule Tx2 → Tx1 : X = 400, Y = 300

### Tx1과 Tx2가 동시에 섞여서 실행되는 경우
| Step    | Tx1                         | Tx2                         |
|---------|-----------------------------|-----------------------------|
| STEP 1  |                             | S-Lock(X)                   |
| STEP 2  |                             | Read(X) // X = 100          |
| STEP 3  |                             | Unlock(X)                   |
| STEP 4  | S-Lock(Y)                   |                             |
| STEP 5  |                             | X-Lock(Y) // Block          |
| STEP 6  | Read(Y) // Y = 200          |                             |
| STEP 7  | Unlock(Y)                   |                             |
| STEP 8  |                             | Read(Y) // Y = 200          |
| STEP 9  |                             | Write(Y = X + Y) // Y = 300 |
| STEP 10 |                             | Unlock(Y)                   |
| STEP 11 | X-Lock(X)                   |                             |
| STEP 12 | Read(X) // X = 100          |                             |
| STEP 13 | Write(X = X + Y) // X = 300 |                             |
| STEP 14 | Unlock(X)                   |                             |
- 위에서 확인한 두 Serial Schedule의 결과와 일치하지 않음을 확인할 수 있는데 즉, 두 트랜잭션이 동시에 실행되면서 Nonserializable하게 실행됨을 알 수 있다.
- 두 트랜잭션이 Serializable하지 않게 동작하게된 원인은 STEP 3부터 STEP 5까지 이어지는 과정에 있다.
- 위의 문제를 해결하기 위해서는 STEP 5와 STEP 3이 바뀌면 된다. (STEP 4가 실행될 수 없음)

| Step    | Tx1                         | Tx2                         |
|---------|-----------------------------|-----------------------------|
| STEP 1  |                             | S-Lock(X)                   |
| STEP 2  |                             | Read(X) // X = 100          |
| STEP 3  |                             | X-Lock(Y)                   |
| STEP 4  | S-Lock(Y) // Block          |                             |
| STEP 5  |                             | Unlock(X)                   |
| STEP 6  |                             | Read(Y) // Y = 200          |
| STEP 7  |                             | Write(Y = X + Y) // Y = 300 |
| STEP 8  |                             | Unlock(Y)                   |
| STEP 9  | Read(Y) // Y = 300          |                             |
| STEP 10 | Unlock(Y)                   |                             |
| STEP 11 | X-Lock(X)                   |                             |
| STEP 12 | Read(X) // X = 100          |                             |
| STEP 13 | Write(X = X + Y) // X = 400 |                             |
| STEP 14 | Unlock(X)                   |                             |

- Tx1이 먼저 실행된다면 STEP 10, STEP 11에 의해 잠재적으로 문제가 발생할 수 있기 때문에 STEP 10과 STEP 11의 순서를 바꿔주어야한다.

| Step    | Tx1                         | Tx2                         |
|---------|-----------------------------|-----------------------------|
| STEP 1  |                             | S-Lock(X)                   |
| STEP 2  |                             | Read(X) // X = 100          |
| STEP 3  |                             | X-Lock(Y)                   |
| STEP 4  | S-Lock(Y) // Block          |                             |
| STEP 5  |                             | Unlock(X)                   |
| STEP 6  |                             | Read(Y) // Y = 200          |
| STEP 7  |                             | Write(Y = X + Y) // Y = 300 |
| STEP 8  |                             | Unlock(Y)                   |
| STEP 9  | Read(Y) // Y = 300          |                             |
| STEP 10 | X-Lock(X)                   |                             |
| STEP 11 | Unlock(Y)                   |                             |
| STEP 12 | Read(X) // X = 100          |                             |
| STEP 13 | Write(X = X + Y) // X = 400 |                             |
| STEP 14 | Unlock(X)                   |                             |

- Anomaly 현상을 방지하기 위해서는 각각의 트랜잭션에서 Locking 관련된 오퍼레이션이 최초의 Unlock 오퍼레이션보다 먼저 수행되도록 해야한다.
- 이러한 프로토콜을 2PL(Two-Phase Locking)이라고한다.

### 2PL(Two-Phase Locking)
- 2PL프로토콜은 프로토콜의 이름처럼 Expanding Phase와 Shrinking Phase로 구성되어있다.
- Expanding Phase(Growing Phase) : Lock을 취득하기만하고 반환하지는 않는 Phase
- Shrinking Phase(Contracting Phase) : Lock을 반환하기만하고 취득하지는 않는 Phase
- 즉, 2PL 프로토콜은 트랜잭션에서 모든 Locking 오퍼레이션이 최초의 Unlock 오퍼레이션보다 먼저 수행되도록 하는 것이고, 한번 Unlock을 시작하면 새로운 Lock을 획득하지 않는다고 볼 수 있다.
- 단, 상황에 따라서 2PL 프로토콜을 수행하면 데드락이 발생할 수 있다.

| STEP   | Tx1                | Tx2                |
|--------|--------------------|--------------------|
| STEP 1 |                    | S-Lock(X)          |
| STEP 2 | S-Lock(Y)          |                    |
| STEP 3 | Read(Y) // Y = 200 |                    |
| STEP 4 | X-Lock(X) // Block |                    |
| STEP 5 |                    | Read(X) // X = 100 |
| STEP 6 |                    | X-Lock(Y) // Block |

### 2PL 프로토콜의 여러가지 종류
- Conservative 2PL : 모든 Lock을 취득한 다음 트랜잭션을 시작한다.
  - Deadlock Free한 알고리즘이다.
  - 트랜잭션에서 필요로하는 모든 Lock을 취득한 다음에 수행가능하기 때문에 트랜잭션 실행 자체가 느려질 수 있고, 실용적이지 않다.
- Strict 2PL(S2PL) : Strict Schedule을 보장하는 2PL 프로토콜이다.
  - Recoverability를 보장한다. (Rollback이 발생했을 때 Anomaly가 발생하지 않도록 한다.)
  - Strict Schedule이란 어떤 데이터에 대해서 Write하는 트랜잭션이 있다면, 해당 트랜잭션이 Commit, Rollback이 되기 전까지 다른 트랜잭션이 그 데이터에 대해서 Read, Write 하지 않는다. 
  - Write Lock을 취득했다면 트랜잭션이 Commit, Rollback 할 때 Lock을 반환한다.
- Strong Strict 2PL(SS2PL, Rigorous 2PL) : Strict Schedule을 보장하는 2PL 프로토콜로, S2PL과 동일하지만 Read Lock 또한 트랜잭션이 Commit, Rollback할 때 반환한다.
  - S2PL보다 구현이 쉽지만, Lock의 범위가 길어진다는 단점이 있다.

### 2PL 방식의 문제점
- S2PL, SS2PL은 초창기 RDBMS에서 가장 많이 사용되는 구현방식이지만 Read → Read를 제외하고는 한 쪽이 Block 되기 때문에 전체 처리량이 좋지 않다.
- Read와 Write가 서로를 Block하는 것만이라도 해결하기 위해서 나온 방식이 MVCC(Multiversion Concurrency Control)이다.
- 오늘날의 RDBMS는 Lock과 MVCC를 혼용해서 사용한다.
