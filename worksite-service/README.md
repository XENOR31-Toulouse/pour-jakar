# worksite-service (hexagonal architecture)

This microservice is organized following **Hexagonal / Ports & Adapters** principles.

## Package layout

```
com.omenaapp.worksite_service
  ├── domain
  │   ├── model                 # Pure domain objects (no Spring/JPA)
  │   └── port/out              # Outbound ports (interfaces)
  ├── application
  │   └── service               # Use-cases (pure Java, no Spring annotations)
  ├── adapter
  │   ├── in/web                # REST controllers + web/security adapters
  │   └── out/persistance       # JPA entities + Spring Data repos + persistence adapters
  └── infrastructure
      └── config                # Spring wiring (Bean composition)
```

## Flow (example)

`REST Controller (adapter/in)` → calls an `application/service` use-case → depends only on `domain/port/out` → implemented by `adapter/out/*`.

## Notes

* Domain + application layers are framework-agnostic.
* Persistence is isolated behind outbound ports.
* Spring is used for wiring (DI) and for the inbound HTTP adapter.
test