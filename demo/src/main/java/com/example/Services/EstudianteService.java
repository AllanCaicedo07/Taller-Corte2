package com.example.Services;

import com.example.dao.EstudianteDao;
import com.example.Model.Estudiante;

import java.util.List;

public class EstudianteService {

    private final EstudianteDao estudianteDao = new EstudianteDao();

    public boolean registrarEstudiante(String nombre, String apellido, String email) {
        if (nombre == null || nombre.isBlank()) {
            System.out.println("El nombre del estudiante no puede estar vacío.");
            return false;
        }
        if (apellido == null || apellido.isBlank()) {
            System.out.println("El apellido del estudiante no puede estar vacío.");
            return false;
        }
        if (email == null || email.isBlank()) {
            System.out.println("El email del estudiante no puede estar vacío.");
            return false;
        }
        Estudiante estudiante = new Estudiante(0, nombre.trim(), apellido.trim(), email.trim());
        return estudianteDao.crearEstudiante(estudiante);
    }

    public List<Estudiante> obtenerTodosLosEstudiantes() {
        return estudianteDao.listarEstudiantes();
    }

    public Estudiante obtenerEstudiantePorId(int id) {
        return estudianteDao.buscarPorId(id);
    }

    public boolean modificarEstudiante(int id, String nombre, String apellido, String email) {
        Estudiante existente = estudianteDao.buscarPorId(id);
        if (existente == null) {
            System.out.println("No existe un estudiante con el ID " + id);
            return false;
        }
        existente.setNombre(nombre.trim());
        existente.setApellido(apellido.trim());
        existente.setEmail(email.trim());
        return estudianteDao.actualizarEstudiante(existente);
    }

    public boolean eliminarEstudiante(int id) {
        return estudianteDao.eliminarEstudiante(id);
    }
}
