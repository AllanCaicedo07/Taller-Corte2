package com.example.dao;

import com.example.DB.ConexionDB;
import com.example.Model.Estudiante;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EstudianteDao {

    public boolean crearEstudiante(Estudiante estudiante) {
        String sql = "INSERT INTO estudiante (nombre, apellido, email) VALUES (?, ?, ?)";
        try (Connection con = ConexionDB.getConexion();
                PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, estudiante.getNombre());
            ps.setString(2, estudiante.getApellido());
            ps.setString(3, estudiante.getEmail());
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error al crear estudiante: " + e.getMessage());
            return false;
        }
    }

    public List<Estudiante> listarEstudiantes() {
        List<Estudiante> lista = new ArrayList<>();
        String sql = "SELECT * FROM estudiante";
        try (Connection con = ConexionDB.getConexion();
                Statement st = con.createStatement();
                ResultSet rs = st.executeQuery(sql)) {

            while (rs.next())
                lista.add(mapear(rs));

        } catch (SQLException e) {
            System.err.println("Error al listar estudiantes: " + e.getMessage());
        }
        return lista;
    }

    public Estudiante buscarPorId(int id) {
        String sql = "SELECT * FROM estudiante WHERE id_estudiante = ?";
        try (Connection con = ConexionDB.getConexion();
                PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next())
                return mapear(rs);

        } catch (SQLException e) {
            System.err.println("Error al buscar estudiante: " + e.getMessage());
        }
        return null;
    }

    public boolean actualizarEstudiante(Estudiante estudiante) {
        String sql = "UPDATE estudiante SET nombre = ?, apellido = ?, email = ? WHERE id_estudiante = ?";
        try (Connection con = ConexionDB.getConexion();
                PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, estudiante.getNombre());
            ps.setString(2, estudiante.getApellido());
            ps.setString(3, estudiante.getEmail());
            ps.setInt(4, estudiante.getIdEstudiante());
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error al actualizar estudiante: " + e.getMessage());
            return false;
        }
    }

    public boolean eliminarEstudiante(int id) {
        String sql = "DELETE FROM estudiante WHERE id_estudiante = ?";
        try (Connection con = ConexionDB.getConexion();
                PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error al eliminar estudiante: " + e.getMessage());
            return false;
        }
    }

    private Estudiante mapear(ResultSet rs) throws SQLException {
        return new Estudiante(
                rs.getInt("id_estudiante"),
                rs.getString("nombre"),
                rs.getString("apellido"),
                rs.getString("email"));
    }
}