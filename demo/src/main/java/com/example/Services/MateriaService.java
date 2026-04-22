package com.example.Services;

import com.example.dao.MateriaDao;
import com.example.Model.Materia;

import java.util.List;

public class MateriaService {

    private final MateriaDao materiaDao = new MateriaDao();

    public boolean registrarMateria(String nombreMateria, int creditos) {
        if (nombreMateria == null || nombreMateria.isBlank()) {
            System.out.println("El nombre de la materia no puede estar vacío.");
            return false;
        }
        if (creditos <= 0) {
            System.out.println("Los créditos deben ser un número positivo.");
            return false;
        }
        Materia materia = new Materia(0, nombreMateria.trim(), creditos);
        return materiaDao.crearMateria(materia);
    }

    public List<Materia> obtenerTodasLasMaterias() {
        return materiaDao.listarMaterias();
    }

    public Materia obtenerMateriaPorId(int id) {
        return materiaDao.buscarPorId(id);
    }

    public boolean modificarMateria(int id, String nombreMateria, int creditos) {
        Materia existente = materiaDao.buscarPorId(id);
        if (existente == null) {
            System.out.println("No existe una materia con el ID " + id);
            return false;
        }
        existente.setNombreMateria(nombreMateria.trim());
        existente.setCreditos(creditos);
        return materiaDao.actualizarMateria(existente);
    }

    public boolean eliminarMateria(int id) {
        return materiaDao.eliminarMateria(id);
    }
}
