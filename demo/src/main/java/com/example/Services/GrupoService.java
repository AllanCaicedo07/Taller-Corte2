package com.example.Services;

import com.example.dao.GrupoDao;
import com.example.Model.Grupo;

import java.util.List;

public class GrupoService {

    private final GrupoDao grupoDao = new GrupoDao();

    public boolean registrarGrupo(int idMateria, int idDocente, String aula, String horario) {
        if (aula == null || aula.isBlank()) {
            System.out.println("El aula no puede estar vacía.");
            return false;
        }
        if (horario == null || horario.isBlank()) {
            System.out.println("El horario no puede estar vacío.");
            return false;
        }
        Grupo grupo = new Grupo(0, idMateria, idDocente, aula.trim(), horario.trim());
        return grupoDao.crearGrupo(grupo);
    }

    public List<Grupo> obtenerTodosLosGrupos() {
        return grupoDao.listarGrupos();
    }

    public Grupo obtenerGrupoPorId(int id) {
        return grupoDao.buscarPorId(id);
    }

    public boolean modificarGrupo(int id, int idMateria, int idDocente, String aula, String horario) {
        Grupo existente = grupoDao.buscarPorId(id);
        if (existente == null) {
            System.out.println("No existe un grupo con el ID " + id);
            return false;
        }
        existente.setIdMateria(idMateria);
        existente.setIdDocente(idDocente);
        existente.setAula(aula.trim());
        existente.setHorario(horario.trim());
        return grupoDao.actualizarGrupo(existente);
    }

    public boolean eliminarGrupo(int id) {
        return grupoDao.eliminarGrupo(id);
    }
}
