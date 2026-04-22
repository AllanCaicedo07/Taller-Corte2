package com.example.dao;

import com.example.DB.ConexionDB;
import com.example.Model.Materia;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MateriaDao {

    // ── Crear ────────────────────────────────────────────────────────────────
    public boolean crearMateria(Materia materia) {
        String sql = "INSERT INTO materia (nombre_materia, creditos) VALUES (?, ?)";
        try (Connection con = ConexionDB.getConexion();
                PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, materia.getNombreMateria());
            ps.setInt(2, materia.getCreditos());
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error al crear materia: " + e.getMessage());
            return false;
        }
    }

    // ── Listar todos ─────────────────────────────────────────────────────────
    public List<Materia> listarMaterias() {
        List<Materia> lista = new ArrayList<>();
        String sql = "SELECT * FROM materia";
        try (Connection con = ConexionDB.getConexion();
                Statement st = con.createStatement();
                ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                lista.add(mapear(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error al listar materias: " + e.getMessage());
        }
        return lista;
    }

    // ── Buscar por ID ─────────────────────────────────────────────────────────
    public Materia buscarPorId(int id) {
        String sql = "SELECT * FROM materia WHERE id_materia = ?";
        try (Connection con = ConexionDB.getConexion();
                PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next())
                return mapear(rs);

        } catch (SQLException e) {
            System.err.println("Error al buscar materia: " + e.getMessage());
        }
        return null;
    }

    // ── Actualizar ────────────────────────────────────────────────────────────
    public boolean actualizarMateria(Materia materia) {
        String sql = "UPDATE materia SET nombre_materia = ?, creditos = ? WHERE id_materia = ?";
        try (Connection con = ConexionDB.getConexion();
                PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, materia.getNombreMateria());
            ps.setInt(2, materia.getCreditos());
            ps.setInt(3, materia.getIdMateria());
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error al actualizar materia: " + e.getMessage());
            return false;
        }
    }

    // ── Eliminar ──────────────────────────────────────────────────────────────
    public boolean eliminarMateria(int id) {
        String sql = "DELETE FROM materia WHERE id_materia = ?";
        try (Connection con = ConexionDB.getConexion();
                PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error al eliminar materia: " + e.getMessage());
            return false;
        }
    }

    // ── Mapear ResultSet → Materia ────────────────────────────────────────────
    private Materia mapear(ResultSet rs) throws SQLException {
        return new Materia(
                rs.getInt("id_materia"),
                rs.getString("nombre_materia"),
                rs.getInt("creditos"));
    }
}
