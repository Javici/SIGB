# SIGB – Documentación Técnica Completa
## Sistema Integral de Gestión y Mantenimiento de Biblioteca
### Java 25 (LTS) + Spring Boot 4.0.6 + Angular 19

**Autor:** Javier Lujan  
**Fecha:** Junio 2026  
**TFM Master Fullstack**

---

## Índice

1. [Visión general del proyecto](#1-visión-general-del-proyecto)
2. [Estructura de directorios](#2-estructura-de-directorios)
3. [Backend – Capa a capa](#3-backend--capa-a-capa)
   - 3.1 [Entidades JPA](#31-entidades-jpa)
   - 3.2 [DTOs con Java Records](#32-dtos-con-java-records)
   - 3.3 [Repositorios Spring Data JPA](#33-repositorios-spring-data-jpa)
   - 3.4 [Seguridad: JWT + Spring Security 7](#34-seguridad-jwt--spring-security-7)
   - 3.5 [Servicios de negocio](#35-servicios-de-negocio)
   - 3.6 [Controladores REST](#36-controladores-rest)
4. [Tecnologías Java 25 aplicadas](#4-tecnologías-java-25-aplicadas)
   - 4.1 [Virtual Threads (JEP 425)](#41-virtual-threads-jep-425)
   - 4.2 [Scoped Values (JEP 487)](#42-scoped-values-jep-487)
   - 4.3 [Structured Concurrency (JEP 505)](#43-structured-concurrency-jep-505)
   - 4.4 [Pattern Matching en primitivos (JEP 507)](#44-pattern-matching-en-primitivos-jep-507)
   - 4.5 [Java Records para DTOs](#45-java-records-para-dtos)
5. [Novedades Spring Boot 4 / Spring Security 7 aplicadas](#5-novedades-spring-boot-4--spring-security-7-aplicadas)
6. [Frontend Angular 19](#6-frontend-angular-19)
   - 6.1 [Arquitectura standalone](#61-arquitectura-standalone)
   - 6.2 [Angular Signals](#62-angular-signals)
   - 6.3 [Guards funcionales y protección de rutas](#63-guards-funcionales-y-protección-de-rutas)
   - 6.4 [Interceptor HTTP funcional](#64-interceptor-http-funcional)
   - 6.5 [Componentes por pantalla](#65-componentes-por-pantalla)
7. [Flujo completo de una petición](#7-flujo-completo-de-una-petición)
8. [Base de datos MySQL](#8-base-de-datos-mysql)
9. [Cómo ejecutar el proyecto](#9-cómo-ejecutar-el-proyecto)
10. [API Reference (endpoints)](#10-api-reference-endpoints)

---

## 1. Visión general del proyecto

El SIGB es una aplicación web fullstack que digitaliza la gestión de una biblioteca. Está dividida en dos procesos independientes que se comunican mediante una **API REST**:

```
[Navegador (Angular SPA)] ──HTTP/JSON──> [Spring Boot REST API] ──JDBC──> [MySQL]
```

Características implementadas:
- **Autenticación JWT** sin estado (stateless): cada petición lleva el token en el header `Authorization: Bearer <jwt>`.
- **Tres roles**: ADMIN, LIBRARIAN, READER. Las rutas están protegidas tanto en el backend (Spring Security) como en el frontend (Angular Guards).
- **Catálogo** con búsqueda multifiltro en tiempo real.
- **Préstamos físicos** con control de plazos, cálculo automático de multas y aplicación de sanciones.
- **Cola de reservas** con notificación automática por email al primer usuario en cola cuando se registra una devolución. El bibliotecario puede aceptar o denegar reservas pendientes.
- **Concurrencia de alto rendimiento** gracias a Virtual Threads de Java 25.

---

## 2. Estructura de directorios

```
sigb/
├── backend/                        ← Proyecto Maven (Spring Boot 4)
│   ├── pom.xml
│   └── src/main/
│       ├── java/com/library/sigb/
│       │   ├── LibraryApplication.java          ← Punto de entrada
│       │   ├── config/
│       │   │   ├── SecurityConfig.java          ← Spring Security 7
│       │   │   └── AsyncConfig.java             ← Virtual Threads para @Async
│       │   ├── entity/                          ← Entidades JPA (tablas MySQL)
│       │   │   ├── User.java
│       │   │   ├── Book.java
│       │   │   ├── Loan.java
│       │   │   ├── Reservation.java
│       │   │   ├── Role.java                    ← enum
│       │   │   ├── LoanStatus.java              ← enum
│       │   │   └── ReservationStatus.java       ← enum
│       │   ├── dto/                             ← Java Records (inmutables)
│       │   │   ├── LoginRequest.java
│       │   │   ├── LoginResponse.java
│       │   │   ├── RegisterRequest.java
│       │   │   ├── BookDto.java
│       │   │   ├── UserDto.java
│       │   │   ├── LoanDto.java
│       │   │   └── ReservationDto.java
│       │   ├── repository/                      ← Spring Data JPA
│       │   │   ├── UserRepository.java
│       │   │   ├── BookRepository.java
│       │   │   ├── LoanRepository.java
│       │   │   └── ReservationRepository.java
│       │   ├── security/                        ← JWT + Scoped Values
│       │   │   ├── JwtUtil.java
│       │   │   ├── JwtAuthFilter.java
│       │   │   ├── UserDetailsServiceImpl.java
│       │   │   └── UserContext.java             ← ScopedValue<User>
│       │   ├── service/                         ← Lógica de negocio
│       │   │   ├── AuthService.java
│       │   │   ├── BookService.java
│       │   │   ├── LoanService.java
│       │   │   ├── ReservationService.java
│       │   │   ├── UserService.java
│       │   │   └── NotificationService.java     ← Structured Concurrency
│       │   └── controller/                      ← REST API /api/v1/
│       │       ├── AuthController.java
│       │       ├── BookController.java
│       │       ├── LoanController.java
│       │       ├── ReservationController.java
│       │       ├── LibrarianReservationController.java  ← Gestión reservas por bibliotecario
│       │       └── UserController.java
│       └── resources/
│           ├── application.properties
│           └── data.sql                         ← Datos iniciales
│
└── frontend/                       ← Proyecto Angular 19
    ├── package.json
    ├── angular.json
    ├── proxy.conf.json              ← Redirige /api → localhost:8080
    └── src/app/
        ├── app.component.ts         ← Shell: navbar + router-outlet
        ├── app.config.ts            ← Providers globales (sin NgModule)
        ├── app.routes.ts            ← Rutas con lazy loading y guards
        ├── core/
        │   ├── models/              ← Interfaces TypeScript
        │   ├── services/            ← HTTP services
        │   ├── guards/              ← authGuard, roleGuard
        │   └── interceptors/        ← authInterceptor (inyecta JWT)
        ├── features/
        │   ├── auth/login/          ← Login + Registro (una sola vista)
        │   ├── catalog/             ← Catálogo con búsqueda avanzada
        │   ├── profile/             ← Perfil + mis reservas
        │   ├── librarian/           ← Gestión préstamos y catálogo
        │   └── admin/               ← Gestión de usuarios
        └── shared/navbar/           ← Barra de navegación reactiva
```

---

## 3. Backend – Capa a capa

### 3.1 Entidades JPA

Las entidades son clases Java anotadas con `@Entity` de Jakarta EE 11 (`jakarta.persistence.*`). Cada entidad mapea a una tabla en MySQL y Hibernate 7.1 genera el esquema automáticamente.

**Relaciones:**
```
User (1) ──< (N) Loan (N) >── (1) Book
User (1) ──< (N) Reservation (N) >── (1) Book
```

**Decisiones importantes:**

- `User.sanctionedUntil`: campo `LocalDate` nullable. Si su valor es una fecha futura, el usuario está sancionado y no puede pedir préstamos ni reservas. Se calcula con `user.isSanctioned()`.
- `Book.availableCopies`: se decrementa al crear un préstamo y se incrementa al registrar una devolución. Es la fuente de verdad para saber si se puede prestar.
- `Loan.overdueDays()`: método en la propia entidad que calcula los días de retraso comparando `dueDate` con `returnDate` (o con hoy si aún está activo).
- Todas las propiedades no nullable están anotadas con `@NonNull` de JSpecify.

### 3.2 DTOs con Java Records

Los DTOs son **Java Records** (característica estable desde Java 16, muy usada en Java 25). Un record genera automáticamente:
- Constructor canónico (con todos los campos)
- Getters (con el mismo nombre que el campo, sin `get`)
- `equals()`, `hashCode()`, `toString()`
- Son **inmutables**: no hay setters

Ejemplo:
```java
public record LoginResponse(
    @NonNull String token,
    @NonNull String username,
    @NonNull String email,
    @NonNull Role role
) {}
```

Esto ahorra ~40 líneas de código respecto a una clase tradicional. Los DTOs:
- **Nunca exponen campos sensibles** (p.ej. `UserDto` no tiene `password`)
- Tienen un método estático `from(Entity)` para la conversión
- Jackson 3 los serializa/deserializa directamente a/desde JSON

### 3.3 Repositorios Spring Data JPA

Heredan de `JpaRepository<Entity, Long>`. Spring Data JPA genera la implementación SQL en tiempo de compilación. Métodos disponibles sin escribir SQL:
- `findById(id)`, `findAll()`, `save()`, `deleteById()`
- Por convención de nombre: `findByUsername(username)`, `existsByEmail(email)`

Para búsquedas complejas se usa **JPQL** con `@Query`:

```java
@Query("""
    SELECT b FROM Book b
    WHERE (:title IS NULL OR LOWER(b.title) LIKE LOWER(CONCAT('%', :title, '%')))
    ...
    """)
List<Book> searchBooks(@Param("title") String title, ...);
```

Esto permite la búsqueda multifiltro: si el usuario deja un campo vacío, el parámetro es `null` y la condición se ignora.

### 3.4 Seguridad: JWT + Spring Security 7

**Flujo de autenticación:**
```
POST /api/v1/auth/login
    → AuthService.login()
        → AuthenticationManager verifica user+pass contra MySQL (BCrypt)
        → JwtUtil.generateToken() → firma HS256
    ← { token, username, email, role }
```

**Flujo de petición autenticada:**
```
GET /api/v1/librarian/loans
  Header: Authorization: Bearer <jwt>
    → JwtAuthFilter.doFilterInternal()
        → JwtUtil.extractUsername() → valida firma + expiración
        → UserRepository.findByUsername() → carga User
        → SecurityContextHolder.setAuthentication()
        → ScopedValue.runWhere(UserContext.CURRENT, user, chain)
    → LoanController → LoanService (tiene acceso a UserContext.CURRENT)
```

**Configuración de rutas (`SecurityConfig.java`):**
```java
.authorizeHttpRequests(auth -> auth
    .requestMatchers("/api/v1/auth/**").permitAll()           // pública
    .requestMatchers(GET, "/api/v1/books/**").permitAll()     // catálogo público
    .requestMatchers("/api/v1/admin/**").hasRole("ADMIN")     // solo ADMIN
    .requestMatchers("/api/v1/librarian/**").hasAnyRole("ADMIN","LIBRARIAN")
    .anyRequest().authenticated()
)
```

**Por qué BCrypt?** Es un algoritmo de hashing adaptativo: el coste computacional se puede aumentar con el tiempo. No es reversible (no se puede recuperar la contraseña original).

**Por qué JWT?** Stateless: el servidor no necesita almacenar sesiones. Escala horizontalmente sin base de datos de sesiones compartida. El token contiene el username y el rol, firmados con HMAC-SHA256.

### 3.5 Servicios de negocio

Los servicios son el corazón de la aplicación. Implementan la lógica de negocio y orquestan las operaciones sobre repositorios.

**LoanService – Registro de devolución:**
1. Carga el préstamo por ID
2. Calcula `overdueDays = loan.overdueDays()`
3. Si `overdueDays > 0`: aplica sanción al usuario con `applySanctionIfNeeded()`
4. Libera el ejemplar: `book.availableCopies++`
5. Cambia el estado del préstamo a RETURNED u OVERDUE
6. Llama a `notificationService.notifyNextInQueue(loan)` de forma **asíncrona**

**NotificationService – Structured Concurrency:**
```java
try (var scope = new StructuredTaskScope.ShutdownOnFailure()) {
    var dbTask    = scope.fork(() -> markReservationNotified(reservation)); // BBDD
    var emailTask = scope.fork(() -> sendAvailabilityEmail(reservation));   // SMTP
    var auditTask = scope.fork(() -> logAuditEvent(loan, reservation));     // Log
    scope.join().throwIfFailed(); // espera a las 3 y lanza si alguna falla
}
```

Las tres operaciones se ejecutan en **paralelo** en Virtual Threads hijos. Si cualquiera falla, `ShutdownOnFailure` cancela las demás y el error se propaga.

**ReservationService – Ciclo de vida de una reserva:**
- `createReservation()`: verifica que el usuario no esté sancionado, que el libro no esté disponible (si lo estuviera se usaría un préstamo directo), y que el usuario no tenga ya una reserva PENDING para el mismo libro. Asigna `queuePosition` correlativo.
- `acceptReservation()`: el bibliotecario acepta la reserva notificada → crea un préstamo y marca la reserva como FULFILLED.
- `denyReservation()`: el bibliotecario deniega la reserva → la marca como CANCELLED y notifica al siguiente en cola si procede.

### 3.6 Controladores REST

Siguen el patrón REST estándar:
- `GET`    → lectura, sin efectos secundarios
- `POST`   → creación de recurso
- `PUT`    → actualización completa o parcial
- `DELETE` → eliminación

Todos devuelven `ResponseEntity<T>` para controlar el código HTTP de respuesta (200, 201, 204, etc.). La validación de DTOs se hace con `@Valid` y las anotaciones de Jakarta Validation (`@NotBlank`, `@Email`, etc.).

**Resumen de controladores:**

| Controlador | Prefijo de ruta | Rol mínimo |
|------------|-----------------|-----------|
| `AuthController` | `/api/v1/auth` | Público |
| `BookController` | `/api/v1/books` | Público (GET), LIBRARIAN (escritura) |
| `LoanController` | `/api/v1/librarian/loans` | LIBRARIAN |
| `ReservationController` | `/api/v1/reservations` | READER |
| `LibrarianReservationController` | `/api/v1/librarian/reservations` | LIBRARIAN |
| `UserController` | `/api/v1/users`, `/api/v1/admin/users` | READER / ADMIN |

---

## 4. Tecnologías Java 25 aplicadas

### 4.1 Virtual Threads (JEP 425)

**Qué es:** Hilos de peso ligero gestionados por la JVM, no por el sistema operativo. Un Virtual Thread bloqueado en I/O (espera JDBC, espera SMTP) no consume un hilo del sistema operativo.

**Configuración:**
```properties
# application.properties
spring.threads.virtual.enabled=true
```

Esta única propiedad hace que Spring Boot 4 configure Tomcat 11 para crear un Virtual Thread por cada petición HTTP entrante, en lugar de usar un pool fijo.

**Por qué importa:** Un servidor con 200 hilos de plataforma puede gestionar **miles de peticiones concurrentes** con Virtual Threads si la mayoría del tiempo están esperando I/O (que es el caso habitual en aplicaciones CRUD).

**AsyncConfig.java:** Para los métodos `@Async` se usa `SimpleAsyncTaskExecutor` con `setVirtualThreads(true)`:
```java
executor.setVirtualThreads(true);
```
Esto aplica el mismo principio al servicio de notificaciones.

### 4.2 Scoped Values (JEP 487)

**Qué es:** Una alternativa a `ThreadLocal` diseñada para Virtual Threads. Un `ScopedValue<T>` es:
- **Inmutable** dentro de su scope (no se puede cambiar una vez asignado)
- **Automáticamente propagado** a los Virtual Threads hijos (p.ej. los creados con `StructuredTaskScope.fork()`)
- **Sin riesgo de fugas**: al salir del scope léxico, el valor desaparece

**Implementación en `UserContext.java`:**
```java
public static final ScopedValue<User> CURRENT = ScopedValue.newInstance();
```

**Asignación en `JwtAuthFilter.java`:**
```java
ScopedValue.runWhere(UserContext.CURRENT, user,
    () -> chain.doFilter(request, response)
);
```

Todo el código que se ejecuta dentro de `chain.doFilter()` (controladores, servicios, repositorios) puede llamar a `UserContext.CURRENT.get()` y obtener el usuario autenticado **sin pasar el usuario como parámetro** en cada función.

**Ventaja frente a ThreadLocal:** Con Virtual Threads, ThreadLocal puede causar problemas porque los Virtual Threads son multiplexados sobre pocos hilos de plataforma. Si un Thread Pool reutiliza hilos, el ThreadLocal del thread anterior podría "contaminar" la siguiente petición. Los ScopedValues no tienen este problema.

### 4.3 Structured Concurrency (JEP 505)

**Qué es:** Una API para gestionar grupos de tareas concurrentes como una unidad de trabajo. Garantiza que:
- Si el scope padre cancela, todos los hijos se cancelan
- Si un hijo falla, el scope puede cancelar los demás (ShutdownOnFailure)
- Si un hijo falla, el scope puede terminar en cuanto uno tenga resultado (ShutdownOnSuccess)

**Implementación en `NotificationService.java`:**
```java
try (var scope = new StructuredTaskScope.ShutdownOnFailure()) {
    var dbTask    = scope.fork(() -> markReservationNotified(reservation));
    var emailTask = scope.fork(() -> sendAvailabilityEmail(reservation));
    var auditTask = scope.fork(() -> logAuditEvent(loan, reservation));
    scope.join().throwIfFailed();
}
```

`scope.join()` bloquea hasta que **todas** las tareas terminen. Si alguna lanza excepción, `throwIfFailed()` la relanza en el hilo padre. Este patrón reemplaza al uso de `CompletableFuture.allOf()` con una semántica mucho más clara y con propagación automática de ScopedValues a los hilos hijos.

### 4.4 Pattern Matching en primitivos (JEP 507)

**Qué es:** Extensión del switch expression para que los patrones puedan incluir tipos primitivos (`int`, `long`, etc.) con condiciones `when`.

**Implementación en `LoanService.java`:**
```java
int sanctionDays = switch ((int) overdueDays) {
    case int d when d <= 7  -> 3;   // 1-7 días de retraso → sanción 3 días
    case int d when d <= 14 -> 7;   // 8-14 días → sanción 7 días
    default                  -> 30; // >14 días → sanción 30 días
};
```

Antes (Java 17) se hubiera escrito con if-else o ternario. El switch con patrones es más legible y exhaustivo (el compilador verifica que todos los casos estén cubiertos).

**Nota:** En Java 25 esta característica es estable y no requiere `--enable-preview`. El `pom.xml` únicamente especifica `<release>25</release>` en el compilador, sin flags adicionales.

### 4.5 Java Records para DTOs

Los Records son clases de datos inmutables. Son perfectos para DTOs porque:
- No tienen estado mutable → thread-safe por diseño
- Generan código boilerplate automáticamente
- Con `@NonNull` y `@Nullable` de JSpecify se documenta qué campos pueden ser null

---

## 5. Novedades Spring Boot 4 / Spring Security 7 aplicadas

### Jakarta EE 11
Todos los imports usan `jakarta.*` (no `javax.*`). Ejemplo:
- `jakarta.persistence.Entity` (antes `javax.persistence.Entity`)
- `jakarta.validation.constraints.NotBlank`
- `jakarta.servlet.FilterChain`

### Spring Security 7 – SecurityFilterChain funcional
No existe `WebSecurityConfigurerAdapter` (eliminado). La configuración es una cadena de lambdas:
```java
@Bean
public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    return http
        .csrf(AbstractHttpConfigurer::disable)
        .sessionManagement(s -> s.sessionCreationPolicy(STATELESS))
        .authorizeHttpRequests(auth -> auth
            .requestMatchers("/api/v1/auth/**").permitAll()
            ...
        )
        .build();
}
```

### @EnableMethodSecurity
Permite usar `@PreAuthorize` en los métodos de servicio:
```java
@PreAuthorize("hasAnyRole('ADMIN','LIBRARIAN')")
public LoanDto createLoan(Long userId, Long bookId) { ... }
```
Si un usuario con rol READER intenta acceder, Spring Security lanza `AccessDeniedException` → 403 Forbidden.

### JSpecify null-safety
Anotaciones `@NonNull` y `@Nullable` en toda la capa de servicio. El compilador (con plugins apropiados) puede detectar potenciales `NullPointerExceptions` en tiempo de compilación. Todos los parámetros de métodos públicos están anotados.

### Jackson 3
La serialización/deserialización de DTOs es transparente. Jackson 3 mapea automáticamente Records Java (los trata como objetos con constructores y getters). Configurado en `application.properties`:
```properties
spring.jackson.default-property-inclusion=non_null
```
Los campos `null` no se incluyen en la respuesta JSON, reduciendo el tamaño del payload.

### API Versioning nativo
Todas las rutas tienen prefijo `/api/v1/`. Para añadir una versión 2 en el futuro bastaría con añadir `/api/v2/` con sus propios controladores sin romper la v1.

---

## 6. Frontend Angular 19

### 6.1 Arquitectura standalone

Angular 19 elimina los `NgModule` en favor de **componentes standalone**. Cada componente declara sus propias dependencias en `imports: [...]`:

```typescript
@Component({
  selector: 'app-catalog',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './catalog.component.html'
})
```

Los proveedores globales se registran en `app.config.ts`:
```typescript
export const appConfig: ApplicationConfig = {
  providers: [
    provideRouter(routes),
    provideHttpClient(withInterceptors([authInterceptor])),
    provideAnimations()
  ]
};
```

### 6.2 Angular Signals

Los `signal<T>()` reemplazan a los `BehaviorSubject` de RxJS para el estado de los componentes. Son más simples y eficientes:

```typescript
// Antes (BehaviorSubject):
private booksSubject = new BehaviorSubject<Book[]>([]);
books$ = this.booksSubject.asObservable();

// Ahora (Signal):
books = signal<Book[]>([]);
// Actualizar:
this.books.set(newBooks);
// Actualizar con función:
this.books.update(list => [...list, newBook]);
// En plantilla: {{ books() }} (se llaman como funciones)
```

En `AuthService` se usan `signal` para el estado de sesión (`isLoggedIn`, `currentRole`, `username`). Los Guards y la Navbar leen estas señales y reaccionan automáticamente a los cambios.

```typescript
// Signal computado (se recalcula si isLoggedIn cambia):
readonly isAdmin = computed(() => this.auth.hasRole('ADMIN'));
```

### 6.3 Guards funcionales y protección de rutas

Los guards son funciones puras (no clases) que devuelven `true`, `false` o una `UrlTree` de redirección:

**`authGuard`** – Protege todas las rutas privadas:
```typescript
export const authGuard: CanActivateFn = () => {
  const auth   = inject(AuthService);
  const router = inject(Router);
  return auth.isLoggedIn() ? true : router.createUrlTree(['/login']);
};
```

**`roleGuard`** – Lee los roles permitidos del campo `data` de la ruta:
```typescript
// En app.routes.ts:
{
  path: 'admin',
  canActivate: [authGuard, roleGuard],
  data: { roles: ['ADMIN'] },
  loadComponent: () => import('./features/admin/admin.component')
}
```

Si el usuario está logueado pero no tiene el rol correcto, se le redirige al catálogo.

### 6.4 Interceptor HTTP funcional

El interceptor adjunta el JWT a todas las peticiones hacia `/api`:
```typescript
export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const token = inject(AuthService).getToken();
  if (token && req.url.startsWith('/api')) {
    return next(req.clone({
      setHeaders: { Authorization: `Bearer ${token}` }
    }));
  }
  return next(req);
};
```

El token se almacena en `localStorage` con clave `sigb_token`. Gracias al interceptor, ningún servicio necesita añadir el header manualmente.

**Proxy de desarrollo (`proxy.conf.json`):**
```json
{ "/api": { "target": "http://localhost:8080", "secure": false } }
```
Durante el desarrollo, Angular Dev Server (puerto 4200) reenvía todas las peticiones `/api/**` al backend (puerto 8080), evitando problemas de CORS.

### 6.5 Componentes por pantalla

| Ruta | Componente | Quién puede acceder |
|------|-----------|---------------------|
| `/login` | `LoginComponent` | Todos |
| `/catalog` | `CatalogComponent` | Todos (reservar requiere login) |
| `/profile` | `ProfileComponent` | Usuarios logueados |
| `/librarian` | `LibrarianComponent` | LIBRARIAN, ADMIN |
| `/admin` | `AdminComponent` | ADMIN |

Todos usan **lazy loading** (`loadComponent: () => import(...)`) para reducir el bundle inicial.

---

## 7. Flujo completo de una petición

### Caso de uso: Un lector reserva un libro

```
1. FRONTEND
   CatalogComponent.reserve(bookId)
     → LoanService.createReservation(bookId)
       → POST /api/v1/reservations?bookId=5
         → authInterceptor añade "Authorization: Bearer <jwt>"

2. RED
   HTTP POST localhost:4200/api/v1/reservations?bookId=5
     → proxy.conf.json → localhost:8080/api/v1/reservations?bookId=5

3. BACKEND – Tomcat 11 (Virtual Thread nuevo por petición)
   a) JwtAuthFilter:
      - Extrae "<jwt>" del header
      - JwtUtil.extractUsername() → "lector1"
      - JwtUtil.isTokenValid() → true
      - userRepository.findByUsername("lector1") → User{id=3, role=READER}
      - SecurityContextHolder.setAuthentication(...)
      - ScopedValue.runWhere(UserContext.CURRENT, user, () -> chain.doFilter())
        ↑ A partir de aquí, UserContext.CURRENT.get() = User{id=3} en este hilo

   b) SecurityConfig authorizeHttpRequests:
      - /api/v1/reservations → anyRequest().authenticated() → OK (usuario logueado)

   c) ReservationController.create():
      - user = UserContext.currentOrNull() → User{id=3}  (desde ScopedValue)
      - reservationService.createReservation(3, 5)

   d) ReservationService.createReservation(userId=3, bookId=5):
      - Carga User{id=3}, Book{id=5}
      - Verifica que el usuario no esté sancionado
      - Verifica que el libro NO esté disponible (si lo estuviera → error, use préstamo)
      - Verifica que no tenga ya una reserva PENDING para este libro
      - int queuePos = reservationRepository.nextQueuePosition(5) → ej: 2
      - Crea y guarda Reservation{user, book, queuePos=2, status=PENDING}

4. RESPUESTA
   ← 201 Created
   ← { id:7, userId:3, username:"lector1", bookId:5, bookTitle:"...",
        reservationDate:"...", status:"PENDING", queuePosition:2 }

5. FRONTEND
   success.set("Reserva creada. Tu posición en cola: 2")
   → ngIf muestra alerta verde
```

### Caso de uso: El bibliotecario acepta una reserva notificada

```
1. LIBRARIAN acepta la notificación
   PUT /api/v1/librarian/reservations/{id}/accept
     → LibrarianReservationController.accept(id)
     → ReservationService.acceptReservation(id)
       - Marca la reserva como FULFILLED
       - Crea un Loan para el usuario de la reserva
       - Decrementa book.availableCopies
   ← 200 OK con el LoanDto creado
```

---

## 8. Base de datos MySQL

Hibernate genera las tablas automáticamente con `spring.jpa.hibernate.ddl-auto=update`. El esquema resultante es:

```sql
CREATE TABLE users (
  id              BIGINT AUTO_INCREMENT PRIMARY KEY,
  username        VARCHAR(50)  UNIQUE NOT NULL,
  email           VARCHAR(100) UNIQUE NOT NULL,
  password        VARCHAR(255) NOT NULL,
  role            ENUM('ADMIN','LIBRARIAN','READER') NOT NULL,
  active          TINYINT(1) DEFAULT 1,
  sanctioned_until DATE NULL
);

CREATE TABLE books (
  id                BIGINT AUTO_INCREMENT PRIMARY KEY,
  title             VARCHAR(255) NOT NULL,
  author            VARCHAR(255) NOT NULL,
  isbn              VARCHAR(20)  UNIQUE,
  category          VARCHAR(100),
  description       TEXT,
  total_copies      INT NOT NULL DEFAULT 1,
  available_copies  INT NOT NULL DEFAULT 1,
  published_year    INT
);

CREATE TABLE loans (
  id          BIGINT AUTO_INCREMENT PRIMARY KEY,
  user_id     BIGINT NOT NULL REFERENCES users(id),
  book_id     BIGINT NOT NULL REFERENCES books(id),
  loan_date   DATE NOT NULL,
  due_date    DATE NOT NULL,
  return_date DATE,
  status      ENUM('ACTIVE','RETURNED','OVERDUE') NOT NULL
);

CREATE TABLE reservations (
  id               BIGINT AUTO_INCREMENT PRIMARY KEY,
  user_id          BIGINT NOT NULL REFERENCES users(id),
  book_id          BIGINT NOT NULL REFERENCES books(id),
  reservation_date DATETIME NOT NULL,
  status           ENUM('PENDING','NOTIFIED','FULFILLED','CANCELLED') NOT NULL,
  queue_position   INT NOT NULL
);
```

**`data.sql`** inserta datos de ejemplo con `INSERT IGNORE` (no falla si ya existen):
- 4 usuarios (admin, bibliotecario, lector1, lector2) con contraseña `password123`
- 8 libros de programación

**Configuración de conexión (`application.properties`):**
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/sigb_db?useSSL=false&serverTimezone=Europe/Madrid&allowPublicKeyRetrieval=true
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
spring.jpa.hibernate.ddl-auto=update
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQLDialect
```

---

## 9. Cómo ejecutar el proyecto

### Requisitos previos
- Java 25 JDK
- Maven 3.9+
- MySQL 8.x (o 9.x) — crear base de datos `sigb_db`
- Node.js 22+ y Angular CLI (`npm install -g @angular/cli@19`)

### Backend
```bash
# 1. Crear la base de datos en MySQL
mysql -u root -p -e "CREATE DATABASE sigb_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;"

# 2. Ajustar credenciales en application.properties
#    spring.datasource.username=tu_usuario
#    spring.datasource.password=tu_contraseña

# 3. Compilar y arrancar
cd sigb/backend
mvn spring-boot:run
# → Arranca en http://localhost:8080
# → Hibernate crea las tablas y carga data.sql automáticamente
```

### Frontend
```bash
cd sigb/frontend
npm install
ng serve --proxy-config proxy.conf.json
# → Arranca en http://localhost:4200
# → El proxy redirige /api → localhost:8080
```

### Credenciales de ejemplo
| Usuario | Contraseña | Rol |
|---------|-----------|-----|
| `admin` | `password123` | ADMIN |
| `bibliotecario` | `password123` | LIBRARIAN |
| `lector1` | `password123` | READER |
| `lector2` | `password123` | READER |

---

## 10. API Reference (endpoints)

### Autenticación (público)
| Método | Ruta | Body | Respuesta |
|--------|------|------|-----------|
| POST | `/api/v1/auth/register` | `{username, email, password}` | `{token, username, email, role}` |
| POST | `/api/v1/auth/login` | `{username, password}` | `{token, username, email, role}` |

### Libros (GET público, POST/PUT/DELETE requieren rol)
| Método | Ruta | Rol mínimo | Descripción |
|--------|------|-----------|-------------|
| GET | `/api/v1/books` | — | Listado completo |
| GET | `/api/v1/books/search?title=&author=&isbn=&category=&availableOnly=` | — | Búsqueda multifiltro |
| GET | `/api/v1/books/{id}` | — | Detalle |
| POST | `/api/v1/books` | LIBRARIAN | Crear libro |
| PUT | `/api/v1/books/{id}` | LIBRARIAN | Actualizar libro |
| DELETE | `/api/v1/books/{id}` | ADMIN | Eliminar libro |

### Préstamos
| Método | Ruta | Rol mínimo | Descripción |
|--------|------|-----------|-------------|
| GET | `/api/v1/librarian/loans` | LIBRARIAN | Todos los préstamos |
| GET | `/api/v1/librarian/loans/user/{id}` | LIBRARIAN | Por usuario |
| POST | `/api/v1/librarian/loans?userId=&bookId=` | LIBRARIAN | Crear préstamo |
| PUT | `/api/v1/librarian/loans/{id}/return` | LIBRARIAN | Registrar devolución |

### Reservas (lector)
| Método | Ruta | Rol mínimo | Descripción |
|--------|------|-----------|-------------|
| GET | `/api/v1/reservations/my` | READER | Mis reservas |
| GET | `/api/v1/reservations/book/{id}` | READER | Cola de un libro |
| POST | `/api/v1/reservations?bookId=` | READER | Crear reserva |
| DELETE | `/api/v1/reservations/{id}` | READER | Cancelar reserva propia |

### Gestión de reservas (bibliotecario)
| Método | Ruta | Rol mínimo | Descripción |
|--------|------|-----------|-------------|
| GET | `/api/v1/librarian/reservations/pending` | LIBRARIAN | Reservas pendientes de gestión |
| PUT | `/api/v1/librarian/reservations/{id}/accept` | LIBRARIAN | Aceptar → crea préstamo |
| PUT | `/api/v1/librarian/reservations/{id}/deny` | LIBRARIAN | Denegar → cancela reserva |

### Usuarios
| Método | Ruta | Rol mínimo | Descripción |
|--------|------|-----------|-------------|
| GET | `/api/v1/admin/users` | ADMIN | Todos los usuarios |
| GET | `/api/v1/users/me` | READER | Mi perfil |
| PUT | `/api/v1/users/me?email=&password=` | READER | Actualizar mi perfil |
| PUT | `/api/v1/admin/users/{id}/role?role=` | ADMIN | Cambiar rol |
| PUT | `/api/v1/admin/users/{id}/active?active=` | ADMIN | Activar/desactivar |

---

*Fin de la documentación técnica — v2, Junio 2026.*
