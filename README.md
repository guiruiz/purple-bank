# Purple Bank

## Overview

Purple Bank is a REST-based API designed to do checks and balances.
It only accepts and responses contents in JSON format.

With Purple Bank API, you can:
* Create User
* Get User State
* Create transactions

## Resources

### User
User entity represents a Purple Bank account. A User can have many Transactions.

| Field         | Type                     | Description                               |
| ------------- |------------------------- | ----------------------------------------- |
| id            | uuid                     | A key that uniquely identifies the User   |
| name          | string                   | The user name                             |
| balance       | bigdecimal               | The user  total balance                   |
| transactions  | vector                   | A vector containing user's transactions   |

### Transaction
Transaction entity represents a monetary transaction. A Transaction must belongs to a User.

| Field         | Type                       | Description                                      |
| ------------- |-------------------------   | ------------------------------------------------ |
| id            | uuid                       | A key that uniquely identifies the Transaction   |
| operation     | string ["credit"  "debit"] | The transaction operation                        |
| amount        | number                     | The transaction amount                           |
| timestamp     | string                     | The timestamp of transaction creation            |

## API Endpoints

### Creating User
To create a new user, make a `POST` request on `/users` passing user attributes on request body.


```
POST /users/
```

Example request body
```json
{
  "name": "Joao"
}
```


Example response body (201)
```json
{
    "id": "82b9a195-e8de-4cdf-8a8d-ce79e1dd264b",
    "name": "Joao",
    "balance": 0,
    "transactions": []
}
```

Response Status Codes

| Status Code   | Meaning                    |
| ------------- |-------------------------   |
| 201           | User was created           |
| 400           | Invalid user params        |



### Getting User
To retrieve a user, make a `GET` request on `/users/:user-id` passing the user id as path-param.
```
GET /users/:user-id
```

Example response body (200)
```json
{
    "id": "276b17a0-fa61-431e-ab8f-1049a5afa7ba",
    "name": "Paulo",
    "balance": 0,
    "transactions": []
}
```

Response Status Codes

| Status Code   | Meaning                  |
| ------------- |------------------------- |
| 200           | User was found           |
| 404           | User not found           |

### Creating Transaction

To creates a new transaction, make a `POST` request on `/users/:user-id/transaction` passing the user id as path-param and transaction attributes on request body.
```
POST /users/:user-id/transactions
```

Example request body
```json
{  
   "operation": "credit",
   "amount": 20.50
}
```

Example response body (201)
```json
{
    "id": "13409d68-dfee-431a-beb1-c5553a96663e",
    "operation": "credit",
    "amount": 20.50,
    "timestamp": "2019-06-07T07:25:06.561Z"
}
```
Response Status Codes

| Status Code   | Meaning                     |
| ------------- |---------------------------- |
| 201           | Transaction was created     |
| 400           | Invalid transaction params  |
| 403           | User has not enough balance |
| 404           | User not found              |

## Running the application
To run the platform in production mode, execute `lein run` command on terminal.

To run it in development mode, execute `lein run-dev` command on terminal.

You can also run it from REPL executing  `(-main)` for production mode or `(run-dev)` for development mode.


## Tests
The project is using [Midje](https://github.com/marick/Midje) and [selvage](https://github.com/nubank/selvage) to implement unit and integration tests.
The tests has full coverage of adapters, controller and logic code.

To run the test pipeline, execute `lein midje` command on terminal.

You can also run it from REPL executing  `(do (use 'midje.repl) (load-facts))`.


## System Design
I've decided to use hexagonal architecture on this project for three main reasons:
* Isolate the application core (controller and logic) from services to write automated tests that coverages the whole code.
* Make it easy to couple and decouple services according to the needs.
* Get closer to microservices development mindset at Nubank's daily work.

### Components
I've decided to use [Components](https://github.com/stuartsierra/component) framework because it makes possible to have stateful resources in a functional paradigm and also it fits very well on hexagonal architecture concepts, allowing to easily couple and decouple new ports.

* Config: Holds environment configurations.
* Logger: Port component responsible for system logging. It uses a different implementation according to running environment, such as:
  * Debug-Logger on development/test mode to log received events and data.
  * Logger on production mode to log only received events for data privacy purposes. In a real application, it could send logs to Datadog, for example.
* Storage: Port component responsible for system storage. It uses InMemoryStorage implementation to any environment.
* Routes: Encapsulates pedestal http routes.
* Service: Builds pedestal service configurations according to running environment.
* Servlet: Port component responsible for http server. It creates a pedestal server from service component and only starts it if running environment is development or production.  

To make sure that services can be easily changed, its implementations must defines a protocol that specifies the needed functions.
In our case:
* Logger components must defines logger-client protocol and its functions.
* Storage components must defines storage-client protocol and its functions.


### Controller Results
The concept behind controller result is to provide a default schema to be used on communications between adapters and controllers.
It allows to each adapter handles the response coming from the controller actions in their own way.

A controller result is a map data structure that contains `:data` and `:error` keys.

Whenever a controller action is successful, it must associates the resulted data to `:data` key, nil value to `:error` key and returns the result map.
If the action is unsuccessful, it must associate the reason as keyword to `:error`, nil value to `:data` and returns the result map.
