package com.example.Services;

import com.example.dao.InscripcionCursoDao;
import com.example.Model.InscripcionCurso;

import java.util.List;

public class InscripcionCursoService {

    private final InscripcionCursoDao inscripcionDao = new InscripcionCursoDao();

    public boolean inscribirEstudiante(int idEstudiante, int idGrupo) {
        // Al inscribir, la nota comienza en 0 y el estado es ACTIVO
        InscripcionCurso inscripcion = new InscripcionCurso(0, idEstudiante, idGrupo, 0.0f, "ACTIVO");
        return inscripcionDao.crearInscripcion(inscripcion);
    }

    public boolean registrarNota(int idInscripcion, float nota) {
        if (nota < 0 || nota > 5) {
            System.out.println("La nota debe estar entre 0.0 y 5.0");
            return false;
        }
        InscripcionCurso inscripcion = inscripcionDao.buscarPorId(idInscripcion);
        if (inscripcion == null) {
            System.out.println("No existe la inscripción con ID " + idInscripcion);
            return false;
        }
        inscripcion.setNotaFinal(nota);
        inscripcion.setEstado(nota >= 3.0f ? "APROBADO" : "REPROBADO");
        return inscripcionDao.actualizarInscripcion(inscripcion);
    }

    public List<InscripcionCurso> obtenerTodasLasInscripciones() {
        return inscripcionDao.listarInscripciones();
    }

    public List<InscripcionCurso> obtenerInscripcionesPorEstudiante(int idEstudiante) {
        return inscripcionDao.listarPorEstudiante(idEstudiante);
    }

    public InscripcionCurso obtenerInscripcionPorId(int id) {
        return inscripcionDao.buscarPorId(id);
    }

    public boolean cancelarInscripcion(int idInscripcion) {
        InscripcionCurso inscripcion = inscripcionDao.buscarPorId(idInscripcion);
        if (inscripcion == null) {
            System.out.println("No existe la inscripción con ID " + idInscripcion);
            return false;
        }
        inscripcion.setEstado("CANCELADO");
        return inscripcionDao.actualizarInscripcion(inscripcion);
    }

    public boolean eliminarInscripcion(int id) {
        return inscripcionDao.eliminarInscripcion(id);
    }
}
