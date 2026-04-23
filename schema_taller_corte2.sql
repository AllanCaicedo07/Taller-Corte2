-- ============================================================
-- Schema: Taller Corte 2
-- Base de datos: PostgreSQL (Neon)
-- Descripción: Script de creación de tablas basado en el
--              diagrama de clases del taller corte 2.
-- ============================================================
-- 1. Crear el schema
CREATE SCHEMA IF NOT EXISTS "Taller corte 2";

-- 2. Establecerlo como schema activo para esta sesión
SET search_path TO "Taller corte 2";
-- -------------------------------------------------------
-- Tabla: Materia
-- Almacena las materias disponibles en el sistema.
-- -------------------------------------------------------
CREATE TABLE Materia (
    id_materia      SERIAL PRIMARY KEY,
    nombre_materia  VARCHAR(100) NOT NULL,
    creditos        INT          NOT NULL CHECK (creditos > 0)
);

-- -------------------------------------------------------
-- Tabla: Docente
-- Almacena la información de los docentes.
-- -------------------------------------------------------
CREATE TABLE Docente (
    id_docente    SERIAL PRIMARY KEY,
    nombre        VARCHAR(100) NOT NULL,
    especialidad  VARCHAR(100) NOT NULL
);

-- -------------------------------------------------------
-- Tabla: Grupo
-- Representa un grupo de clase que se dicta de una materia
-- impartida por un docente en un aula y horario específico.
-- Relación: 1 Materia -> * Grupos, 1 Docente -> * Grupos
-- -------------------------------------------------------
CREATE TABLE Grupo (
    id_grupo    SERIAL PRIMARY KEY,
    id_materia  INT          NOT NULL,
    id_docente  INT          NOT NULL,
    aula        VARCHAR(50)  NOT NULL,
    horario     VARCHAR(100) NOT NULL,

    CONSTRAINT fk_grupo_materia
        FOREIGN KEY (id_materia) REFERENCES Materia(id_materia)
        ON DELETE CASCADE,

    CONSTRAINT fk_grupo_docente
        FOREIGN KEY (id_docente) REFERENCES Docente(id_docente)
        ON DELETE CASCADE
);

-- -------------------------------------------------------
-- Tabla: Estudiante
-- Almacena la información personal de los estudiantes.
-- -------------------------------------------------------
CREATE TABLE Estudiante (
    id_estudiante  SERIAL PRIMARY KEY,
    nombre         VARCHAR(100) NOT NULL,
    apellido       VARCHAR(100) NOT NULL,
    email          VARCHAR(150) NOT NULL UNIQUE
);

-- -------------------------------------------------------
-- Tabla: Inscripcion_Curso
-- Relaciona a un estudiante con un grupo (inscripción).
-- Almacena la nota final y el estado de la inscripción.
-- Relación: 1 Estudiante -> * Inscripciones, 1 Grupo -> * Inscripciones
-- -------------------------------------------------------
CREATE TABLE Inscripcion_Curso (
    id_inscripcion  SERIAL PRIMARY KEY,
    id_estudiante   INT          NOT NULL,
    id_grupo        INT          NOT NULL,
    nota_final      FLOAT        CHECK (nota_final >= 0.0 AND nota_final <= 5.0),
    estado          VARCHAR(50)  NOT NULL DEFAULT 'Activo'
                    CHECK (estado IN ('Activo', 'Retirado', 'Aprobado', 'Reprobado')),

    CONSTRAINT fk_inscripcion_estudiante
        FOREIGN KEY (id_estudiante) REFERENCES Estudiante(id_estudiante)
        ON DELETE CASCADE,

    CONSTRAINT fk_inscripcion_grupo
        FOREIGN KEY (id_grupo) REFERENCES Grupo(id_grupo)
        ON DELETE CASCADE,

    -- Un estudiante no puede inscribirse dos veces al mismo grupo
    CONSTRAINT uq_inscripcion UNIQUE (id_estudiante, id_grupo)
);

-- ============================================================
-- Datos de prueba
-- ============================================================

-- Materias
INSERT INTO Materia (nombre_materia, creditos) VALUES
    ('Programación II',     3),
    ('Base de Datos',       4),
    ('Cálculo Diferencial', 3);

-- Docentes
INSERT INTO Docente (nombre, especialidad) VALUES
    ('Carlos Pérez',  'Ingeniería de Software'),
    ('Laura Gómez',   'Bases de Datos'),
    ('Andrés Torres', 'Matemáticas');

-- Grupos
INSERT INTO Grupo (id_materia, id_docente, aula, horario) VALUES
    (1, 1, 'Aula 101', 'Lunes y Miércoles 8:00 - 10:00'),
    (2, 2, 'Aula 202', 'Martes y Jueves 10:00 - 12:00'),
    (3, 3, 'Aula 303', 'Viernes 14:00 - 17:00');

-- Estudiantes
INSERT INTO Estudiante (nombre, apellido, email) VALUES
    ('Juan',    'Martínez', 'juan.martinez@email.com'),
    ('María',   'López',    'maria.lopez@email.com'),
    ('Pedro',   'Ramírez',  'pedro.ramirez@email.com'),
    ('Sofía',   'Castro',   'sofia.castro@email.com');

-- Inscripciones
INSERT INTO Inscripcion_Curso (id_estudiante, id_grupo, nota_final, estado) VALUES
    (1, 1, 4.5,  'Aprobado'),
    (2, 1, 3.2,  'Aprobado'),
    (3, 2, NULL, 'Activo'),
    (4, 2, NULL, 'Activo'),
    (1, 3, 2.8,  'Reprobado'),
    (2, 3, NULL, 'Retirado');
