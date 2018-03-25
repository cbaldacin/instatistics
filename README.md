# instantaneous statistics
We would like to have a restful API for our statistics. The main use case for our API is to
calculate realtime statistic from the last 60 seconds. There will be two APIs, one of them is
called every time a transaction is made. It is also the sole input of this rest API. The other one
returns the statistic based of the transactions of the last 60 seconds.

## Specs

POST /transactions

Every Time a new transaction happened, this endpoint will be called.

#### Request sample
```http
POST /transactions
Content-Type: application/json

{
    "amount": 12.3,
    "timestamp": 1488347603000
}

```

Where:
* amount is double specifying transaction amount
* timestamp is epoch in millis in UTC time zone specifying transaction time


#### Success response
```http
HTTP/1.1 201 Created
```

#### Error response - timestamp is older than 60 seconds
```http
HTTP/1.1 204 No Content
```

### Statistics

GET /statistics

This is the main endpoint of this task, this endpoint have to execute in constant time
and memory (O(1)). It returns the statistic based on the transactions which happened
in the last 60 seconds.

#### Request sample
```http
GET /statistics
Accept: application/json
```

#### Response sample
```http
HTTP/1.1 200 OK
Content-Type: application/json

{
    "sum": 1000,
    "avg": 100,
    "max": 200,
    "min": 50,
    "count": 10
}
```

Where:
* sum is a double specifying the total sum of transaction value in the last 60
seconds
* avg is a double specifying the average amount of transaction value in the last
60 seconds
* max is a double specifying single highest transaction value in the last 60
seconds
* min is a double specifying single lowest transaction value in the last 60
seconds
* count is a long specifying the total number of transactions happened in the last
60 seconds

## Requirements
For the rest api, the biggest and maybe hardest requirement is to make the GET
/statistics execute in constant time and space. The best solution would be O(1). It is
very recommended to tackle the O(1) requirement as the last thing to do as it is not
the only thing which will be rated in the code challenge.

Other requirements, which are obvious, but also listed here explicitly:
* The API have to be threadsafe with concurrent requests
* The API have to function properly, with proper result
* The project should be buildable, and tests should also complete successfully. e.g. If maven is used, then mvn clean install should complete successfully.
* The API should be able to deal with time discrepancy, which means, at any point of time, we could receive a transaction which have a timestamp of the past
* Make sure to send the case in memory solution without database (including in-memory database)
* Endpoints have to execute in constant time and memory (O(1))

## Solution and Design considerations

* The solution is simple and consist of two parts:
    * For each transaction received we will simply store it in a List
    * There will be a scheduled job configured to run every second that:
        - removes expired transactions from the List;
        - calculates de statistic result to be consumed by HTTP GET /statistic.
(Since there will be a refreshed statistic every second, there won't be any further calculation for when requesting the statistic of     the last 60 seconds)

* OBS:. This solution only works for single instance environment. For multiple instances, some changes are required.


