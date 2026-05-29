-- ─────────────────────────────────────────────────────────────────────────
-- SIGB – Datos iniciales de ejemplo
-- Se ejecuta automáticamente en el arranque (spring.jpa.hibernate.ddl-auto=update)
-- ─────────────────────────────────────────────────────────────────────────

-- Contraseñas: todas son "password123" cifradas con BCrypt (rounds=10)
INSERT IGNORE INTO users (username, email, password, role, active)
VALUES
  ('admin',         'admin@sigb.es',     '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LnmBQDWTBhO', 'ADMIN',     true),
  ('bibliotecario', 'bib@sigb.es',       '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LnmBQDWTBhO', 'LIBRARIAN', true),
  ('lector1',       'lector1@sigb.es',   '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LnmBQDWTBhO', 'READER',    true),
  ('lector2',       'lector2@sigb.es',   '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LnmBQDWTBhO', 'READER',    true);

INSERT IGNORE INTO books (title, author, isbn, category, description, total_copies, available_copies, published_year)
VALUES
  ('Clean Code',                   'Robert C. Martin', '9780132350884', 'Programación', 'Guía para escribir código limpio y mantenible.',    3, 3, 2008),
  ('The Pragmatic Programmer',     'David Thomas',     '9780201616224', 'Programación', 'Consejos prácticos para el programador moderno.',   2, 2, 2019),
  ('Design Patterns',              'Gang of Four',     '9780201633610', 'Arquitectura', 'Patrones de diseño orientados a objetos.',          2, 2, 1994),
  ('Domain-Driven Design',         'Eric Evans',       '9780321125217', 'Arquitectura', 'Diseño orientado al dominio del problema.',         1, 1, 2003),
  ('Effective Java',               'Joshua Bloch',     '9780134685991', 'Java',         'Mejores prácticas para programadores Java.',        2, 2, 2018),
  ('Spring in Action',             'Craig Walls',      '9781617294945', 'Java',         'Guía completa del framework Spring.',               2, 2, 2022),
  ('JavaScript: The Good Parts',   'Douglas Crockford','9780596517748', 'JavaScript',   'Las partes buenas de JavaScript.',                  3, 3, 2008),
  ('You Don''t Know JS',           'Kyle Simpson',     '9781491924464', 'JavaScript',   'Serie sobre los mecanismos internos de JS.',        2, 2, 2015);
