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
