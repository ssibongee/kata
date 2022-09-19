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
