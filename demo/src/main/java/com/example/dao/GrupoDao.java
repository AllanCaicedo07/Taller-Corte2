package com.example.dao;

import com.example.DB.ConexionDB;
import com.example.Model.Grupo;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class GrupoDao {

    public boolean crearGrupo(Grupo grupo) {
        String sql = "INSERT INTO grupo (id_materia, id_docente, aula, horario) VALUES (?, ?, ?, ?)";
        try (Connection con = ConexionDB.getConexion();
                PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, grupo.getIdMateria());
            ps.setInt(2, grupo.getIdDocente());
            ps.setString(3, grupo.getAula());
            ps.setString(4, grupo.getHorario());
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error al crear grupo: " + e.getMessage());
            return false;
        }
    }

    public List<Grupo> listarGrupos() {
        List<Grupo> lista = new ArrayList<>();
        String sql = "SELECT * FROM grupo";
        try (Connection con = ConexionDB.getConexion();
                Statement st = con.createStatement();
                ResultSet rs = st.executeQuery(sql)) {

            while (rs.next())
                lista.add(mapear(rs));

        } catch (SQLException e) {
            System.err.println("Error al listar grupos: " + e.getMessage());
        }
        return lista;
    }

    public Grupo buscarPorId(int id) {
        String sql = "SELECT * FROM grupo WHERE id_grupo = ?";
        try (Connection con = ConexionDB.getConexion();
                PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next())
                return mapear(rs);

        } catch (SQLException e) {
            System.err.println("Error al buscar grupo: " + e.getMessage());
        }
        return null;
    }

    public boolean actualizarGrupo(Grupo grupo) {
        String sql = "UPDATE grupo SET id_materia = ?, id_docente = ?, aula = ?, horario = ? WHERE id_grupo = ?";
        try (Connection con = ConexionDB.getConexion();
                PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, grupo.getIdMateria());
            ps.setInt(2, grupo.getIdDocente());
            ps.setString(3, grupo.getAula());
            ps.setString(4, grupo.getHorario());
            ps.setInt(5, grupo.getIdGrupo());
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error al actualizar grupo: " + e.getMessage());
            return false;
        }
    }

    public boolean eliminarGrupo(int id) {
        String sql = "DELETE FROM grupo WHERE id_grupo = ?";
        try (Connection con = ConexionDB.getConexion();
                PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error al eliminar grupo: " + e.getMessage());
            return false;
        }
    }

    private Grupo mapear(ResultSet rs) throws SQLException {
        return new Grupo(
                rs.getInt("id_grupo"),
                rs.getInt("id_materia"),
                rs.getInt("id_docente"),
                rs.getString("aula"),
                rs.getString("horario"));
    }
}