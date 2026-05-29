-- ─────────────────────────────────────────────────────────────────────────────
-- SIGB – Seed de libros (75 títulos reales)
-- Ejecutar en MySQL después de que Spring Boot haya creado la tabla `books`
-- mysql -u root sigb_db < seed_books.sql
-- ─────────────────────────────────────────────────────────────────────────────

INSERT INTO books (title, author, isbn, category, description, total_copies, available_copies, published_year) VALUES

-- ── Ficción clásica ───────────────────────────────────────────────────────────
('Don Quijote de la Mancha',       'Miguel de Cervantes',       '9788420412146', 'Ficción',          'Las aventuras del ingenioso hidalgo manchego y su fiel escudero Sancho Panza.',                                           3, 3, 1605),
('Cien años de soledad',           'Gabriel García Márquez',    '9780060883287', 'Ficción',          'La historia de la familia Buendía a lo largo de siete generaciones en el pueblo de Macondo.',                            3, 3, 1967),
('1984',                           'George Orwell',             '9780451524935', 'Ficción',          'Una distopía sobre un régimen totalitario que controla la realidad mediante la manipulación del lenguaje.',              4, 4, 1949),
('Un mundo feliz',                 'Aldous Huxley',             '9780060850524', 'Ficción',          'Sociedad futurista donde la tecnología y el condicionamiento social han eliminado el sufrimiento y la libertad.',        2, 2, 1932),
('El señor de los anillos',        'J.R.R. Tolkien',            '9780618640157', 'Ficción',          'La épica historia de la Guerra del Anillo y la travesía de Frodo para destruir el Anillo Único.',                       3, 3, 1954),
('Crimen y castigo',               'Fiódor Dostoyevski',        '9780140449136', 'Ficción',          'El estudiante Raskolnikov comete un crimen y lidia con la culpa y la redención en la Rusia zarista.',                   2, 2, 1866),
('Anna Karénina',                  'Lev Tolstói',               '9780143035008', 'Ficción',          'Tragedia de una mujer de la alta sociedad rusa atrapada entre el deber y la pasión.',                                   2, 2, 1878),
('En busca del tiempo perdido',    'Marcel Proust',             '9780142437964', 'Ficción',          'Monumental obra sobre la memoria, el tiempo y la identidad narrada en siete volúmenes.',                                1, 1, 1913),
('El gran Gatsby',                 'F. Scott Fitzgerald',       '9780743273565', 'Ficción',          'La decadencia del sueño americano retratada a través de la obsesión de Gatsby por Daisy Buchanan.',                     3, 3, 1925),
('Moby Dick',                      'Herman Melville',           '9780142437247', 'Ficción',          'La obsesiva persecución del capitán Ahab a la ballena blanca a través de los mares del mundo.',                         2, 2, 1851),
('Ulises',                         'James Joyce',               '9780199535675', 'Ficción',          'Un solo día en Dublín narrado con técnica de flujo de conciencia, considerada la cumbre del modernismo literario.',      1, 1, 1922),
('La metamorfosis',                'Franz Kafka',               '9780553213690', 'Ficción',          'Gregor Samsa despierta una mañana convertido en un insecto gigantesco.',                                                3, 3, 1915),
('Orgullo y prejuicio',            'Jane Austen',               '9780141439518', 'Ficción',          'La inteligente Elizabeth Bennet y el orgulloso Mr. Darcy en la Inglaterra de la Regencia.',                             3, 3, 1813),
('Jane Eyre',                      'Charlotte Brontë',          '9780141441146', 'Ficción',          'La institutriz Jane Eyre y su atormentado empleador Rochester en la mansión Thornfield.',                               2, 2, 1847),
('Los miserables',                 'Victor Hugo',               '9780451419439', 'Ficción',          'Jean Valjean y su redención frente a la implacable persecución del inspector Javert en la Francia del siglo XIX.',      2, 2, 1862),

-- ── Ficción contemporánea ─────────────────────────────────────────────────────
('El nombre de la rosa',           'Umberto Eco',               '9780544176560', 'Ficción',          'Un monje franciscano investiga una serie de misteriosas muertes en una abadía medieval.',                                3, 3, 1980),
('La sombra del viento',           'Carlos Ruiz Zafón',         '9788408163435', 'Ficción',          'Un libro olvidado, un misterio y el Barcelona de posguerra en la primera entrega del Cementerio de los Libros Olvidados.',3, 3, 2001),
('El código Da Vinci',             'Dan Brown',                 '9780385504201', 'Ficción',          'Un asesinato en el Louvre desencadena una búsqueda de secretos ocultos durante siglos por la Iglesia.',                  4, 4, 2003),
('Harry Potter y la piedra filosofal', 'J.K. Rowling',          '9780439708180', 'Ficción',          'Un joven huérfano descubre que es mago y comienza su educación en la escuela Hogwarts.',                                5, 5, 1997),
('El juego de Ender',              'Orson Scott Card',          '9780812550702', 'Ficción',          'Un niño prodigio es reclutado para salvar a la humanidad de una invasión alienígena.',                                   2, 2, 1985),
('Dune',                           'Frank Herbert',             '9780441013593', 'Ficción',          'La política, la religión y la ecología colisionan en el desierto planeta Arrakis, único productor de la especia.',       3, 3, 1965),
('Fundación',                      'Isaac Asimov',              '9780553293357', 'Ficción',          'Un matemático desarrolla la psicohistoria para predecir y acortar la caída de la civilización galáctica.',              2, 2, 1951),
('Fahrenheit 451',                 'Ray Bradbury',              '9781451673319', 'Ficción',          'En un futuro donde los libros están prohibidos, un bombero encargado de quemarlos comienza a cuestionarse su trabajo.',   3, 3, 1953),
('El alquimista',                  'Paulo Coelho',              '9780061122415', 'Ficción',          'Un joven pastor andaluz emprende un viaje hacia Egipto en busca de un tesoro y descubre el verdadero sentido de la vida.', 4, 4, 1988),
('Crimen perfecto',                'Patricia Highsmith',        '9780393334920', 'Ficción',          'Tom Ripley, un joven sin escrúpulos, usa la falsedad y el crimen para ascender socialmente.',                            2, 2, 1955),

-- ── Historia ──────────────────────────────────────────────────────────────────
('Sapiens: De animales a dioses',  'Yuval Noah Harari',         '9780062316097', 'Historia',         'Una breve historia de la humanidad que explora cómo el Homo sapiens dominó la Tierra.',                                  4, 4, 2011),
('El origen de las especies',      'Charles Darwin',            '9780140432053', 'Historia',         'La obra fundamental de la biología evolutiva que describe la selección natural como mecanismo de evolución.',            2, 2, 1859),
('Historia de Roma',               'Theodor Mommsen',           '9780415145954', 'Historia',         'Monumental historia del Imperio Romano desde sus orígenes hasta Julio César, ganadora del Premio Nobel de Literatura.',   1, 1, 1854),
('El príncipe',                    'Nicolás Maquiavelo',        '9780140449150', 'Historia',         'Tratado político renacentista sobre cómo adquirir y mantener el poder político.',                                        3, 3, 1532),
('La guerra del Peloponeso',       'Tucídides',                 '9780140440393', 'Historia',         'Crónica del conflicto entre Atenas y Esparta narrada por uno de sus contemporáneos.',                                   2, 2, -431),
('Guns, Germs, and Steel',         'Jared Diamond',             '9780393317558', 'Historia',         'Por qué unas civilizaciones dominaron a otras: geografía, biología y tecnología como factores determinantes.',           2, 2, 1997),
('The Rise and Fall of the Third Reich', 'William L. Shirer',   '9780671728687', 'Historia',         'Historia completa de la Alemania nazi desde su origen hasta su derrota en 1945.',                                        2, 2, 1960),
('La Revolución Francesa',         'Georges Lefebvre',          '9780231085984', 'Historia',         'Análisis exhaustivo de las causas, el desarrollo y las consecuencias de la Revolución Francesa.',                       1, 1, 1951),
('Breve historia del tiempo',      'Stephen Hawking',           '9780553380163', 'Historia',         'Los grandes misterios del universo explicados de forma accesible: el Big Bang, los agujeros negros y el tiempo.',        3, 3, 1988),

-- ── Filosofía ─────────────────────────────────────────────────────────────────
('La República',                   'Platón',                    '9780872201361', 'Filosofía',        'Diálogos socráticos sobre la justicia, la política y el alma, con la famosa alegoría de la caverna.',                   2, 2, -380),
('Ética a Nicómaco',               'Aristóteles',               '9780872204645', 'Filosofía',        'Tratado fundamental sobre la virtud, la felicidad y la vida buena según la filosofía aristotélica.',                    2, 2, -350),
('Meditaciones',                   'Marco Aurelio',             '9780140449334', 'Filosofía',        'Reflexiones personales del emperador romano sobre el estoicismo, el deber y la virtud.',                                3, 3, 180),
('Crítica de la razón pura',       'Immanuel Kant',             '9780140447477', 'Filosofía',        'Análisis de los límites del conocimiento humano y los fundamentos de la razón.',                                        1, 1, 1781),
('Así habló Zaratustra',           'Friedrich Nietzsche',       '9780140441185', 'Filosofía',        'El profeta Zaratustra anuncia la muerte de Dios y proclama el concepto del superhombre.',                               2, 2, 1883),
('El ser y la nada',               'Jean-Paul Sartre',          '9780671867805', 'Filosofía',        'Obra fundacional del existencialismo que examina la conciencia, la libertad y la responsabilidad humana.',               1, 1, 1943),
('Investigaciones filosóficas',    'Ludwig Wittgenstein',       '9781405159289', 'Filosofía',        'Reflexiones sobre el lenguaje, el significado y los juegos del lenguaje que definen la filosofía analítica.',            1, 1, 1953),

-- ── Ciencia ───────────────────────────────────────────────────────────────────
('El gen egoísta',                 'Richard Dawkins',           '9780198788607', 'Ciencia',          'La evolución vista desde la perspectiva del gen: los organismos como vehículos de supervivencia genética.',              2, 2, 1976),
('Cosmos',                         'Carl Sagan',                '9780345539434', 'Ciencia',          'Un viaje por el universo que combina astronomía, física y reflexiones sobre el lugar de la humanidad en el cosmos.',      3, 3, 1980),
('El mundo y sus demonios',        'Carl Sagan',                '9780345409461', 'Ciencia',          'Defensa del pensamiento científico frente a la pseudociencia, la superstición y la irracionalidad.',                     2, 2, 1995),
('Una breve historia de casi todo','Bill Bryson',               '9780767908184', 'Ciencia',          'La historia de la ciencia moderna explicada con humor y claridad para el gran público.',                                 3, 3, 2003),
('El universo elegante',           'Brian Greene',              '9780393338102', 'Ciencia',          'Introducción a la teoría de supercuerdas y la búsqueda de una teoría unificada de la física.',                          2, 2, 1999),
('La estructura de las revoluciones científicas', 'Thomas S. Kuhn', '9780226458120', 'Ciencia',     'Cómo la ciencia avanza a través de paradigmas y revoluciones conceptuales.',                                            2, 2, 1962),

-- ── Economía y sociedad ───────────────────────────────────────────────────────
('La riqueza de las naciones',     'Adam Smith',                '9780140432084', 'Economía',         'Obra fundacional de la economía moderna que introduce los conceptos de división del trabajo y mercado libre.',            2, 2, 1776),
('El capital',                     'Karl Marx',                 '9780140445688', 'Economía',         'Análisis crítico del capitalismo, la plusvalía y las relaciones de producción en la sociedad burguesa.',                  2, 2, 1867),
('Thinking, Fast and Slow',        'Daniel Kahneman',           '9780374533557', 'Economía',         'Los dos sistemas de pensamiento humano y cómo los sesgos cognitivos afectan nuestras decisiones.',                       3, 3, 2011),
('El fin del trabajo',             'Jeremy Rifkin',             '9780874778663', 'Economía',         'Cómo la tecnología y la automatización están transformando el mercado laboral global.',                                  2, 2, 1995),
('Freakonomics',                   'Steven D. Levitt',          '9780060731335', 'Economía',         'Aplicación de la economía al análisis de fenómenos sociales inesperados con datos y humor.',                             3, 3, 2005),

-- ── Psicología ────────────────────────────────────────────────────────────────
('El hombre en busca de sentido',  'Viktor Frankl',             '9780807014271', 'Psicología',       'Experiencias en los campos de concentración nazis y la logoterapia como búsqueda de sentido vital.',                     4, 4, 1946),
('Influencia',                     'Robert B. Cialdini',        '9780062937650', 'Psicología',       'Los seis principios de la persuasión que explican por qué la gente dice sí.',                                           3, 3, 1984),
('El poder del ahora',             'Eckhart Tolle',             '9781577314806', 'Psicología',       'Guía espiritual sobre la presencia, la conciencia y la liberación del sufrimiento mental.',                             2, 2, 1997),
('Flow',                           'Mihaly Csikszentmihalyi',   '9780061339202', 'Psicología',       'La psicología de la experiencia óptima y cómo alcanzar estados de máxima concentración y satisfacción.',                2, 2, 1990),
('El cerebro del niño',            'Daniel J. Siegel',          '9780553386653', 'Psicología',       'Neurociencia aplicada a la crianza: cómo ayudar a los niños a desarrollar un cerebro integrado y equilibrado.',          2, 2, 2011),

-- ── Arte y literatura ─────────────────────────────────────────────────────────
('El arte de la guerra',           'Sun Tzu',                   '9781590302255', 'Arte',             'Tratado militar de la antigua China con lecciones universales sobre estrategia, liderazgo y conflicto.',                 3, 3, -500),
('Poética',                        'Aristóteles',               '9780140446364', 'Arte',             'Análisis de la tragedia, la épica y los principios de la imitación en la literatura griega.',                           2, 2, -335),
('El arte de amar',                'Erich Fromm',               '9780062133410', 'Arte',             'El amor no como sentimiento pasivo sino como arte que requiere conocimiento y práctica.',                                3, 3, 1956),
('Lolita',                         'Vladimir Nabokov',          '9780679723165', 'Literatura',       'La perturbadora obsesión de Humbert Humbert con una niña de doce años, narrada con prosa extraordinaria.',               2, 2, 1955),
('Pedro Páramo',                   'Juan Rulfo',                '9780802133908', 'Literatura',       'Juan Preciado viaja al pueblo fantasma de Comala en busca de su padre y encuentra solo voces de muertos.',              3, 3, 1955),
('La casa de los espíritus',       'Isabel Allende',            '9780553383805', 'Literatura',       'Saga familiar chilena a lo largo del siglo XX entretejida con el realismo mágico.',                                     2, 2, 1982),
('Ficciones',                      'Jorge Luis Borges',         '9780802130303', 'Literatura',       'Cuentos fantásticos que exploran laberintos, bibliotecas infinitas y mundos imaginarios.',                              3, 3, 1944),

-- ── Tecnología ────────────────────────────────────────────────────────────────
('Clean Code',                     'Robert C. Martin',          '9780132350884', 'Tecnología',       'Principios, patrones y prácticas para escribir código limpio, mantenible y profesional.',                                3, 3, 2008),
('The Pragmatic Programmer',       'David Thomas',              '9780135957059', 'Tecnología',       'Guía de filosofía y práctica del desarrollo de software para programadores que quieren mejorar su oficio.',              2, 2, 1999),
('Design Patterns',                'Gang of Four',              '9780201633610', 'Tecnología',       'Los 23 patrones de diseño orientado a objetos que todo desarrollador de software debería conocer.',                     2, 2, 1994),
('Introduction to Algorithms',     'Thomas H. Cormen',          '9780262033848', 'Tecnología',       'Referencia exhaustiva sobre estructuras de datos y algoritmos fundamentales en ciencias de la computación.',             2, 2, 1990),
('The Mythical Man-Month',         'Frederick P. Brooks Jr.',   '9780201835953', 'Tecnología',       'Ensayos sobre ingeniería del software y por qué añadir personas a un proyecto tardío lo retrasa más.',                  2, 2, 1975),

-- ── Biografías ────────────────────────────────────────────────────────────────
('Steve Jobs',                     'Walter Isaacson',           '9781451648539', 'Biografía',        'Biografía definitiva del cofundador de Apple basada en más de cuarenta entrevistas exclusivas.',                         3, 3, 2011),
('El diario de Ana Frank',         'Ana Frank',                 '9780553296983', 'Biografía',        'Diario de una joven judía escondida con su familia durante la ocupación nazi de Ámsterdam.',                            4, 4, 1947),
('Long Walk to Freedom',           'Nelson Mandela',            '9780316548182', 'Biografía',        'Autobiografía del líder sudafricano que luchó contra el apartheid y se convirtió en símbolo de reconciliación.',         2, 2, 1994),
('Leonardo da Vinci',              'Walter Isaacson',           '9781501139154', 'Biografía',        'La vida y la mente del genio del Renacimiento, basada en sus cuadernos y las últimas investigaciones.',                  2, 2, 2017);
