package com.example.dao;

import com.example.DB.ConexionDB;
import com.example.Model.InscripcionCurso;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class InscripcionCursoDao {

    public boolean crearInscripcion(InscripcionCurso inscripcion) {
        String sql = "INSERT INTO inscripcion_curso (id_estudiante, id_grupo, nota_final, estado) VALUES (?, ?, ?, ?)";
        try (Connection con = ConexionDB.getConexion();
                PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, inscripcion.getIdEstudiante());
            ps.setInt(2, inscripcion.getIdGrupo());
            ps.setFloat(3, inscripcion.getNotaFinal());
            ps.setString(4, inscripcion.getEstado());
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error al crear inscripción: " + e.getMessage());
            return false;
        }
    }

    public List<InscripcionCurso> listarInscripciones() {
        List<InscripcionCurso> lista = new ArrayList<>();
        String sql = "SELECT * FROM inscripcion_curso";
        try (Connection con = ConexionDB.getConexion();
                Statement st = con.createStatement();
                ResultSet rs = st.executeQuery(sql)) {

            while (rs.next())
                lista.add(mapear(rs));

        } catch (SQLException e) {
            System.err.println("Error al listar inscripciones: " + e.getMessage());
        }
        return lista;
    }

    public InscripcionCurso buscarPorId(int id) {
        String sql = "SELECT * FROM inscripcion_curso WHERE id_inscripcion = ?";
        try (Connection con = ConexionDB.getConexion();
                PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next())
                return mapear(rs);

        } catch (SQLException e) {
            System.err.println("Error al buscar inscripción: " + e.getMessage());
        }
        return null;
    }

    public List<InscripcionCurso> listarPorEstudiante(int idEstudiante) {
        List<InscripcionCurso> lista = new ArrayList<>();
        String sql = "SELECT * FROM inscripcion_curso WHERE id_estudiante = ?";
        try (Connection con = ConexionDB.getConexion();
                PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idEstudiante);
            ResultSet rs = ps.executeQuery();
            while (rs.next())
                lista.add(mapear(rs));

        } catch (SQLException e) {
            System.err.println("Error al listar inscripciones por estudiante: " + e.getMessage());
        }
        return lista;
    }

    public boolean actualizarInscripcion(InscripcionCurso inscripcion) {
        String sql = "UPDATE inscripcion_curso SET id_estudiante = ?, id_grupo = ?, nota_final = ?, estado = ? WHERE id_inscripcion = ?";
        try (Connection con = ConexionDB.getConexion();
                PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, inscripcion.getIdEstudiante());
            ps.setInt(2, inscripcion.getIdGrupo());
            ps.setFloat(3, inscripcion.getNotaFinal());
            ps.setString(4, inscripcion.getEstado());
            ps.setInt(5, inscripcion.getIdInscripcion());
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error al actualizar inscripción: " + e.getMessage());
            return false;
        }
    }

    public boolean eliminarInscripcion(int id) {
        String sql = "DELETE FROM inscripcion_curso WHERE id_inscripcion = ?";
        try (Connection con = ConexionDB.getConexion();
                PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error al eliminar inscripción: " + e.getMessage());
            return false;
        }
    }

    private InscripcionCurso mapear(ResultSet rs) throws SQLException {
        return new InscripcionCurso(
                rs.getInt("id_inscripcion"),
                rs.getInt("id_estudiante"),
                rs.getInt("id_grupo"),
                rs.getFloat("nota_final"),
                rs.getString("estado"));
    }
}
