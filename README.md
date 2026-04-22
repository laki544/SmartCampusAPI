# Smart Campus Sensor & Room Management API
**Name:** Lakindu Chehan Rathugamage
**ID:** 20232736 / W2120616
**Module:** 5COSC022C – Client-Server Architectures  
**Coursework:** Smart Campus Sensor & Room Management API  
**Technology Stack:** Java 17, JAX-RS (Jersey), Maven, Apache Tomcat 9  

## API Overview

This project is a RESTful API for managing university campus rooms, sensors deployed inside those rooms, and the historical readings produced by each sensor.

The API is implemented using **JAX-RS (Jersey)** and follows a resource-based design:

- **Rooms** are managed through `/rooms`
- **Sensors** are managed through `/sensors`
- **Sensor readings** are managed through the nested path `/sensors/{sensorId}/readings`

The service uses **in-memory data structures only**, as required by the coursework. No database or Spring Boot has been used.

## API Design Summary

### Core resources

#### Room
A room stores:
- `id`
- `name`
- `capacity`
- `sensorIds`

#### Sensor
A sensor stores:
- `id`
- `type`
- `status`
- `currentValue`
- `roomId`

#### SensorReading
A sensor reading stores:
- `id`
- `timestamp`
- `value`

### Resource hierarchy

The API structure reflects the real campus hierarchy:

- `/api/v1` → discovery endpoint
- `/api/v1/rooms` → room collection
- `/api/v1/rooms/{id}` → single room
- `/api/v1/sensors` → sensor collection
- `/api/v1/sensors/{id}` → single sensor
- `/api/v1/sensors/{sensorId}/readings` → readings belonging to one sensor

### In-memory storage

The application uses a shared `DataStore` class containing:
- `rooms` → `ConcurrentHashMap<String, Room>`
- `sensors` → `ConcurrentHashMap<String, Sensor>`
- `readings` → `ConcurrentHashMap<String, List<SensorReading>>`

This satisfies the coursework requirement to use collections instead of a database.

## Main Features Implemented

- JAX-RS application configuration with versioned API base path
- Discovery endpoint at the API root
- Create, list, retrieve, and delete rooms
- Create, list, filter, retrieve, and delete sensors
- Validation that a sensor can only be linked to an existing room
- Nested sensor reading resource using a sub-resource locator
- Historical reading storage per sensor
- Automatic update of a sensor’s `currentValue` when a new reading is added
- Custom exception mappers for 409, 422, 403, and 500 responses
- Request/response logging using JAX-RS filters

## Project Structure

```text
src/main/java/com/smartcampus/
├── config/
│   └── AppConfig.java
├── exceptions/
│   ├── GlobalExceptionMapper.java
│   ├── LinkedResourceNotFoundException.java
│   ├── LinkedResourceNotFoundExceptionMapper.java
│   ├── RoomNotEmptyException.java
│   ├── RoomNotEmptyExceptionMapper.java
│   ├── SensorUnavailableException.java
│   └── SensorUnavailableExceptionMapper.java
├── filters/
│   └── LoggingFilter.java
├── model/
│   ├── Room.java
│   ├── Sensor.java
│   └── SensorReading.java
└── resources/
    ├── ApiResource.java
    ├── DataStore.java
    ├── RoomResource.java
    ├── SensorReadingResource.java
    └── SensorResource.java
```

## How to Build and Run

### Requirements

- Java 17
- Maven 3+
- Apache Tomcat 9

### Build steps

1. Clone the GitHub repository.
2. Open a terminal in the project root.
3. Build the project:

```bash
mvn clean package
```

4. After a successful build, the WAR file will be created in:

```text
target/SmartCampusAPI.war
```

### Run steps on Tomcat

1. Copy `target/SmartCampusAPI.war` into the Tomcat `webapps` folder.
2. Start Apache Tomcat.
3. Open the API using the base URL:

```text
http://localhost:8080/SmartCampusAPI/api/v1
```

Because the project uses `context.xml` with `/SmartCampusAPI`, the application runs under that context path.

## Sample curl Commands

### 1. Discovery endpoint

```bash
curl -X GET http://localhost:8080/SmartCampusAPI/api/v1
```

### 2. Create a room

```bash
curl -X POST http://localhost:8080/SmartCampusAPI/api/v1/rooms \
  -H "Content-Type: application/json" \
  -d '{
    "id": "LIB-301",
    "name": "Library Quiet Study",
    "capacity": 40
  }'
```

### 3. Get all rooms

```bash
curl -X GET http://localhost:8080/SmartCampusAPI/api/v1/rooms
```

### 4. Get a single room

```bash
curl -X GET http://localhost:8080/SmartCampusAPI/api/v1/rooms/LIB-301
```

### 5. Create a sensor linked to a room

```bash
curl -X POST http://localhost:8080/SmartCampusAPI/api/v1/sensors \
  -H "Content-Type: application/json" \
  -d '{
    "id": "CO2-001",
    "type": "CO2",
    "status": "ACTIVE",
    "currentValue": 415.2,
    "roomId": "LIB-301"
  }'
```

### 6. Get all sensors

```bash
curl -X GET http://localhost:8080/SmartCampusAPI/api/v1/sensors
```

### 7. Filter sensors by type

```bash
curl -X GET "http://localhost:8080/SmartCampusAPI/api/v1/sensors?type=CO2"
```

### 8. Add a reading to a sensor

```bash
curl -X POST http://localhost:8080/SmartCampusAPI/api/v1/sensors/CO2-001/readings \
  -H "Content-Type: application/json" \
  -d '{
    "value": 430.8
  }'
```

### 9. Get all readings for a sensor

```bash
curl -X GET http://localhost:8080/SmartCampusAPI/api/v1/sensors/CO2-001/readings
```

### 10. Delete a sensor

```bash
curl -X DELETE http://localhost:8080/SmartCampusAPI/api/v1/sensors/CO2-001
```

### 11. Delete a room

```bash
curl -X DELETE http://localhost:8080/SmartCampusAPI/api/v1/rooms/LIB-301
```

## Report Answers
## Part 1: Service Architecture & Setup 
### Part 1.1 –  Project & Application Configuration

**Question:**  In your report, explain the default lifecycle of a JAX-RS Resource class. Is a new instance instantiated for every incoming request, or does the runtime treat it as a singleton? Elaborate on how this architectural decision impacts the way you manage and synchronize your in-memory data structures (maps/lists) to prevent data loss or race conditions?

**Answer:** By default, a JAX-RS resource class is typically created **per request**, meaning the runtime usually creates a new instance for each incoming HTTP request rather than treating the resource as a singleton. This design reduces the risk of accidental shared mutable state inside resource objects.

Because of that lifecycle, request-specific values can safely be stored in instance fields inside a resource class, but shared application data must not be stored there. In this project, the shared API state is kept in the static `DataStore` class instead. Since multiple requests can still reach the API at the same time, shared collections must be protected against concurrent access. That is why the main room and sensor stores are implemented with `ConcurrentHashMap`. This helps prevent lost updates and race conditions when several clients use the API at the same time.

### Part 1.2 – The ”Discovery” Endpoint 
**Question:** Why is the provision of “Hypermedia” (links and navigation within responses) considered a hallmark of advanced RESTful design (`HATEOAS`)? How does this approach benefit client developers compared to static documentation?

**Answer:** Hypermedia is important because it allows the server to guide clients through the API by returning links and navigational information inside responses. Instead of forcing the client to hardcode every route from external documentation, the API can tell the client what resources are available and where to go next.

This benefits client developers because the API becomes easier to explore, easier to integrate with, and more resilient to future changes. If the server changes how internal URLs are organised, clients can still follow the links returned by the API instead of depending entirely on static documentation. In this project, the discovery endpoint helps clients find the main collections such as rooms and sensors.

## Part 2: Room Management
### Part 2.1 – RoomResource Implementation
**Question:** When returning a list of rooms, what are the implications of returning only IDs versus returning the full room objects? Consider network bandwidth and client-side processing.

**Answer:** Returning only room IDs reduces response size, so it saves bandwidth and can be more efficient when the client only needs identifiers for later requests. However, it also means the client may need to make extra HTTP requests to obtain the full room details, which increases round trips.

Returning the full room objects gives the client all relevant metadata in one response, which reduces extra requests and simplifies client-side processing. The trade-off is that the response payload is larger. For this coursework, returning full room objects is more useful because each room contains meaningful information such as name, capacity, and linked sensor IDs.

### Part 2.2 –  RoomDeletion & Safety Logic 
**Question:**  Is the `DELETE` operation idempotent in your implementation? Provide a detailed justification by describing what happens if a client mistakenly sends the exact same `DELETE` request for a room multiple times.

**Answer:**  DELETE is idempotent when repeating the same request does not create additional side effects beyond the first successful deletion. In this implementation, the first successful `DELETE /rooms/{id}` removes the room. If the same request is sent again, the room no longer exists, so the API returns `404 Not Found` and does not delete anything further.

That means the server state after the first request and after repeated identical requests is effectively the same: the room is absent. For that reason, the operation is still considered idempotent, even though the status code of later requests may be different.

## Part 3: Sensor Operations & Linking
### Part 3.1 – Sensor Resource & Integrity 
**Question:** We explicitly use the `@Consumes(MediaType.APPLICATION_JSON)` annotation on the `POST` method. Explain the technical consequences if a client attempts to send data in a different format, such as `text/plain` or `application/xml`. How does JAX-RS handle this mismatch?

**Answer:** The POST methods explicitly use `@Consumes(MediaType.APPLICATION_JSON)`, which means the endpoint expects JSON input. If a client sends the request body using a different format such as `text/plain` or `application/xml`, JAX-RS will detect that the request media type does not match what the method consumes.

In that situation, the runtime normally rejects the request and returns **415 Unsupported Media Type**. If the body claims to be JSON but contains invalid JSON syntax, deserialisation will fail and the request will also be rejected by the framework. This behaviour is useful because it enforces a clear contract between the client and the API.

### Part 3.2 – Filtered Retrieval & Search
**Question:** You implemented this filtering using `@QueryParam`. Contrast this with an alternative design where the type is part of the URL path (e.g., `/api/v1/sensors/type/CO2`). Why is the query parameter approach generally considered superior for filtering and searching collections?

**Answer:** Using `@QueryParam("type")` for filtering is better because the client is still requesting the same sensor collection resource, but with an extra condition applied to the result set. Filtering does not represent a completely different resource; it represents a refined view of the same collection.

A path such as `/sensors/type/CO2` makes the filter look like a nested resource, which is less flexible and less natural for combining multiple optional filters in the future. Query parameters are more suitable for searching, filtering, sorting, and pagination because they are optional, composable, and widely understood by API clients.

## Part 4: Deep Nesting with Sub- Resources
### Part 4.1 – The Sub-Resource Locator Pattern
**Question:** Discuss the architectural benefits of the Sub-Resource Locator pattern. How does delegating logic to separate classes help manage complexity in large APIs compared to defining every nested path (e.g., `sensors/{id}/readings/{rid}`) in one massive controller class?

**Answer:** The sub-resource locator pattern improves structure by delegating nested resource logic to a dedicated class. In this project, `SensorResource` handles sensor-level operations, while `SensorReadingResource` handles the reading history that belongs to a specific sensor.

This separation keeps the code easier to read, easier to maintain, and easier to extend. Without a sub-resource locator, one very large controller class would need to manage every route for rooms, sensors, and readings, which would quickly become difficult to understand. Delegating nested logic also reflects the API hierarchy more clearly because readings are naturally a child resource of a sensor.

## Part 5: Advanced Error Handling, Exception Mapping & Logging 
### Part 5.2 – Dependency Validation (422 Unprocessable Entity)
**Question:** Why is HTTP `422` often considered more semantically accurate than a standard `404` when the issue is a missing reference inside a valid JSON payload?

**Answer:** HTTP 422 is more semantically accurate because the request itself is syntactically valid JSON, but one of the values inside that JSON is semantically invalid. In this case, the client submits a sensor object with a `roomId` field, but that referenced room does not exist.

A `404 Not Found` is usually more appropriate when the client is requesting a URI that does not exist. Here, the URI for creating sensors does exist, so the problem is not the endpoint itself. The problem is that the submitted representation contains an invalid reference. That is why `422 Unprocessable Entity` more precisely describes the failure.

### Part 5.4 –  The Global Safety Net (500) 
**Question:** From a cybersecurity standpoint, explain the risks associated with exposing internal Java stack traces to external API consumers. What specific information could an attacker gather from such a trace?

**Answer:** Exposing internal stack traces is dangerous because it reveals implementation details that should remain private. An attacker could learn class names, package names, file names, method names, framework versions, and details about the server’s internal structure.

That information can help an attacker identify weak points, guess library versions with known vulnerabilities, and craft more targeted attacks. Stack traces may also reveal sensitive logic about validation, business rules, or infrastructure layout. Returning a generic 500 response instead is safer because it hides those internals from external users while still signalling that the request failed.

### Part 5.5 – API Request & Response Logging Filters
**Question:** Why is it advantageous to use JAX-RS filters for cross-cutting concerns like logging, rather than manually inserting `Logger.info()` statements inside every single resource method?

**Answer:** JAX-RS filters are better for logging because logging is a cross-cutting concern that applies to many endpoints in the same way. By putting request and response logging in a filter, the behaviour is centralised in one place instead of being repeated inside every resource method.

This reduces duplicated code, keeps resource classes focused on business logic, and makes the logging behaviour consistent across the whole API. It also becomes much easier to maintain, because if the logging format needs to change, it only needs to be updated once in the filter class.

## Notes

- The application uses in-memory storage, so all data is lost when the server restarts.
- The API returns JSON responses for normal operations and for custom error cases.
- The logging filter records incoming request method/URI and outgoing response status code.
