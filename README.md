# Assessment

## Table of contents
* [Technologies](#technologies)
* [Setup](#setup)
* [Understanding the external API](#understanding-the-external-api)
* [System Design](#system-design)

## Technologies

* Java 11
* Spring Boot
* Maven

## Setup

**Prerequisites:**


You need to install java 11 and maven 3.6 and add them to your system environment <br/>
After that, you run this command to install required dependency before start

```
$ mvn clean install
```

**Run the Application:**

```
mvn spring-boot:run
```

## Understanding the external API

**(1) Details about the available bus lines  <br/>
GET "https://test.uwave.sg/busLines":**

Response

```json
{
  "payload": [
    {
      "fullName": "Campus WeekEnd Rider Brown",
      "id": "44481",
      "origin": "ntu",
      "shortName": "Brown",
      "busStops": [
        {
          "id": "377906",
          "lat": 1.33781,
          "lng": 103.69739,
          "name": "Pioneer MRT Station Exit B at Blk 649A"
        },
        ...
      ],
      "path": [
        [
          1.33771,
          103.69735
        ],
        ...
      ]
    }
    ...
  ],
  "status": 1000000
}
```

All fields of Response

| Field   | Description                           |
|---------|---------------------------------------|
| Status  | status of request (success or failed) |
| Payload | List of Available Bus lines           |

All fields of Bus Line

| Field     | Description                                                                                                        |
|-----------|--------------------------------------------------------------------------------------------------------------------|
| id        | unique identifier for this bus line                                                                                |
| fullName  | Full Name of the Bus Line                                                                                          |
| shortName | shorter or abbreviated name of the Bus Line                                                                        |
| origin    | Represents the starting point of the bus line                                                                      |
| busStops  | List of bus stops belong to this bus line                                                                          |
| path      | List of the path the bus takes on this line, the first value is for latitude and the second value is for longitude |

All fields of Bus Stops

| Field | Description                         |
|-------|-------------------------------------|
| id    | unique identifier for this bus line |
| lat   | Latitude of this bus stop           |
| lng   | Longitude of bus stop               |
| name  | Full Mame of the bus stop           |

**(2) Bus locations of the running buses based on bus lines <br/>
GET "https://test.uwave.sg/busPositions/" + busLineId**

Response

```json
{
  "payload": [
    {
      "bearing": 139,
      "crowdLevel": "high",
      "lat": 1.345394,
      "lng": 103.688144,
      "vehiclePlate": "PA9552U"
    },
    ...
  ],
  "status": 1000000
}
```

All fields of Response

| Field   | Description                                    |
|---------|------------------------------------------------|
| Status  | status of request (success or failed)          |
| Payload | List of Available Running Bus in the Bus lines |

All fields of Running Buses

| Field        | Description                                                                             |
|--------------|-----------------------------------------------------------------------------------------|
| bearing      | The direction in which the bus is currently heading, expressed in degrees from 0 to 360 |
| crowdLevel   | indicates the current crowd level inside the bus                                        |
| lat          | current geographical coordinates of running bus - latitude                              |
| lng          | current geographical coordinates of running bus - longitude                             |
| vehiclePlate | The license plate number of the bus.                                                    |

## System Design

**1/ Relevant information of the bus stops and bus lines**

Endpoint: **/v1/buses/lines** <br/>
Method: GET

The response will be cached for 12hour since the data won't be changed frequently and since we don't share the data
across the services, we gonna cached in the memory of server

Response

Example:

```json
[
  {
    "fullName": "Campus WeekEnd Rider Brown",
    "id": "44481",
    "origin": "ntu",
    "shortName": "Brown",
    "busStops": [
      {
        "id": "377906",
        "lat": 1.33781,
        "lng": 103.69739,
        "name": "Pioneer MRT Station Exit B at Blk 649A"
      },
      ...
    ],
    "path": [
      [
        1.33771,
        103.69735
      ],
      ...
    ]
  }
  ...
],
```

All fields of Bus Line

| Field     | Description                                                                                                        | Data Type              |
|-----------|--------------------------------------------------------------------------------------------------------------------|------------------------|
| id        | unique identifier for this bus line                                                                                | String                 |
| fullName  | Full Name of the Bus Line                                                                                          | String                 |
| shortName | shorter or abbreviated name of the Bus Line                                                                        | String                 |
| origin    | Represents the starting point of the bus line                                                                      | String                 |
| busStops  | List of bus stops belong to this bus line                                                                          | List of BusStop        |
| path      | List of the path the bus takes on this line, the first value is for latitude and the second value is for longitude | List of List of Double |

All fields of Bus Stops

| Field | Description                         | Data Type |
|-------|-------------------------------------|-----------|
| id    | unique identifier for this bus line | String    |
| lat   | Latitude of this bus stop           | Double    |
| lng   | Longitude of bus stop               | Double    |
| name  | Full Mame of the bus stop           | String    |

**2/ Locations of the running buses in the requested bus line**

Endpoint: **/v1/buses/lines/{busLinesId}/positions**<br/>
Path Variable: busLinesId <br/>
Method: GET

Response

Example:

```json
[
  {
    "bearing": 139,
    "crowdLevel": "high",
    "lat": 1.345394,
    "lng": 103.688144,
    "vehiclePlate": "PA9552U"
  },
  ...
],
```

All fields of Running Bus

| Field     | Description                                                                             | Data Type |
|-----------|-----------------------------------------------------------------------------------------|-----------|
| id        | The direction in which the bus is currently heading, expressed in degrees from 0 to 360 | Double    |
| fullName  | indicates the current crowd level inside the bus                                        | String    |
| shortName | current geographical coordinates of running bus - latitude                              | Double    |
| origin    | current geographical coordinates of running bus - longitude                             | Double    |
| busStops  | The license plate number of the bus.                                                    | String    |

**3/ Estimated arrival time/duration of the incoming buses at the requested bus stop**

Solution: I will use Google Map API to calculate the estimated arrival time

    Pro:
        Easy to implement 
        Realiable and Scalable: Gg can handle large amount of traffic
        Since Google use many factor (such as real-time and historical data, machine learning, route information, ...)to analyze so the estimate time of arrival will have high accuracy
    Cons:
        Can increase cost since google only allow us to call their API for the specific amount of time
        More latency since we use google rest api to get the data 
        We will depend on GG so if Google experiences on downtimes or changes their policies/pricing, it can affect our application

What I am missing now:

    The application still lack of checking if the bus passed the bus station. In my opion, from the API at (2), it should provide more option such as passedBusStation which provide the information the last Bus Passed so API consumer can check

Endpoint: **GET /v1/buses/bus-stop/{busStopId}**<br/>
Path Variable: busStopId (The id of the bus stop) <br/>
Method: GET

Response is a Map (Key, Value)

Example:

```json
{
  "Campus Loop - Blue (CL-B)": [
    {
      "bearing": 56.8,
      "crowdLevel": "low",
      "lat": 1.354888,
      "lng": 103.68404,
      "vehiclePlate": "PD742G",
      "duration": {
        "inSeconds": 160,
        "humanReadable": "3 mins"
      }
    }
  ],
  ....
}
```

With

    Key: Full name of bus Line (Example: Campus Loop - Blue (CL-B))
    Value: List Running bus of that Bus Line

All fields of Running Bus

| Field     | Description                                                                             | Data Type |
|-----------|-----------------------------------------------------------------------------------------|-----------|
| bearing   | The direction in which the bus is currently heading, expressed in degrees from 0 to 360 | Double    |
| fullName  | indicates the current crowd level inside the bus                                        | String    |
| shortName | current geographical coordinates of running bus - latitude                              | Double    |
| origin    | current geographical coordinates of running bus - longitude                             | Double    |
| duration  | The estimate time of arrival of running bus to the bus station                          | Duration  |

All fields of Duration

| Field         | Description                                                    | Data Type |
|---------------|----------------------------------------------------------------|-----------|
| inSeconds     | The time the bus take to arrive to bus station in seconds Unit | Double    |
| humanReadable | The readable time the bus take to arrive to bus station        | String    |

