package com.example.dao;

import com.example.DB.ConexionDB;
import com.example.Model.Docente;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DocenteDao {

    public boolean crearDocente(Docente docente) {
        String sql = "INSERT INTO docente (nombre, especialidad) VALUES (?, ?)";
        try (Connection con = ConexionDB.getConexion();
                PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, docente.getNombre());
            ps.setString(2, docente.getEspecialidad());
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error al crear docente: " + e.getMessage());
            return false;
        }
    }

    public List<Docente> listarDocentes() {
        List<Docente> lista = new ArrayList<>();
        String sql = "SELECT * FROM docente";
        try (Connection con = ConexionDB.getConexion();
                Statement st = con.createStatement();
                ResultSet rs = st.executeQuery(sql)) {

            while (rs.next())
                lista.add(mapear(rs));

        } catch (SQLException e) {
            System.err.println("Error al listar docentes: " + e.getMessage());
        }
        return lista;
    }

    public Docente buscarPorId(int id) {
        String sql = "SELECT * FROM docente WHERE id_docente = ?";
        try (Connection con = ConexionDB.getConexion();
                PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next())
                return mapear(rs);

        } catch (SQLException e) {
            System.err.println("Error al buscar docente: " + e.getMessage());
        }
        return null;
    }

    public boolean actualizarDocente(Docente docente) {
        String sql = "UPDATE docente SET nombre = ?, especialidad = ? WHERE id_docente = ?";
        try (Connection con = ConexionDB.getConexion();
                PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, docente.getNombre());
            ps.setString(2, docente.getEspecialidad());
            ps.setInt(3, docente.getIdDocente());
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error al actualizar docente: " + e.getMessage());
            return false;
        }
    }

    public boolean eliminarDocente(int id) {
        String sql = "DELETE FROM docente WHERE id_docente = ?";
        try (Connection con = ConexionDB.getConexion();
                PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error al eliminar docente: " + e.getMessage());
            return false;
        }
    }

    private Docente mapear(ResultSet rs) throws SQLException {
        return new Docente(
                rs.getInt("id_docente"),
                rs.getString("nombre"),
                rs.getString("especialidad"));
    }
}
