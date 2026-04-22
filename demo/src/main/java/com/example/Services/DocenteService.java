package com.example.Services;

import com.example.dao.DocenteDao;
import com.example.Model.Docente;

import java.util.List;

public class DocenteService {

    private final DocenteDao docenteDao = new DocenteDao();

    public boolean registrarDocente(String nombre, String especialidad) {
        if (nombre == null || nombre.isBlank()) {
            System.out.println("El nombre del docente no puede estar vacío.");
            return false;
        }
        if (especialidad == null || especialidad.isBlank()) {
            System.out.println("La especialidad no puede estar vacía.");
            return false;
        }
        Docente docente = new Docente(0, nombre.trim(), especialidad.trim());
        return docenteDao.crearDocente(docente);
    }

    public List<Docente> obtenerTodosLosDocentes() {
        return docenteDao.listarDocentes();
    }

    public Docente obtenerDocentePorId(int id) {
        return docenteDao.buscarPorId(id);
    }

    public boolean modificarDocente(int id, String nombre, String especialidad) {
        Docente existente = docenteDao.buscarPorId(id);
        if (existente == null) {
            System.out.println("No existe un docente con el ID " + id);
            return false;
        }
        existente.setNombre(nombre.trim());
        existente.setEspecialidad(especialidad.trim());
        return docenteDao.actualizarDocente(existente);
    }

    public boolean eliminarDocente(int id) {
        return docenteDao.eliminarDocente(id);
    }
}
