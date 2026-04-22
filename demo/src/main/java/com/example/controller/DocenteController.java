package com.example.controller;

import com.example.Model.Docente;
import com.example.View.DocenteView;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.*;

import com.example.DB.ConexionDB;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Controlador: DocenteController
 * Maneja la lógica de negocio entre la vista DocenteView
 * y la base de datos PostgreSQL en Neon mediante JDBC.
 */
public class DocenteController {

    // ===================== URL de conexión a Neon =====================
    // Conexión centralizada via ConexionDB
    
    

    // ===================== Componentes de la vista =====================
    private TableView<Docente>       tablaDocentes;
    private TextField                txtNombre;
    private TextField                txtEspecialidad;
    private Label                    lblMensaje;
    private Button                   btnGuardar;

    // ===================== Estado interno =====================
    private Docente docenteSeleccionado = null;

    // ===================== Constructor =====================

    public DocenteController(DocenteView vista) {
        this.tablaDocentes = vista.getTablaDocentes();
        this.txtNombre = vista.getTxtNombre();
        this.txtEspecialidad = vista.getTxtEspecialidad();
        this.lblMensaje = vista.getLblMensaje();
        this.btnGuardar = vista.getBtnGuardar();
    }

    // ===================== Operaciones CRUD =====================

    /**
     * Carga todos los docentes desde la base de datos.
     */
    public void cargarDocentes() {
        List<Docente> docentes = new ArrayList<>();
        String sql = "SELECT id_docente, nombre, especialidad FROM Docente ORDER BY id_docente";

        try (Connection conn = ConexionDB.getConexion();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                docentes.add(new Docente(
                    rs.getInt("id_docente"),
                    rs.getString("nombre"),
                    rs.getString("especialidad")
                ));
            }
            ObservableList<Docente> lista = FXCollections.observableArrayList(docentes);
            tablaDocentes.setItems(lista);
            mostrarMensaje("✅ " + docentes.size() + " docente(s) cargado(s).", false);

        } catch (SQLException e) {
            mostrarMensaje("❌ Error al cargar docentes: " + e.getMessage(), true);
        }
    }

    /**
     * Decide si crear o actualizar un docente.
     */
    public void guardarDocente() {
        if (!validarCampos()) return;

        if (docenteSeleccionado == null) {
            crearDocente();
        } else {
            actualizarDocente();
        }
    }

    /**
     * Inserta un nuevo docente en la base de datos.
     */
    private void crearDocente() {
        String sql = "INSERT INTO Docente (nombre, especialidad) VALUES (?, ?)";

        try (Connection conn = ConexionDB.getConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, txtNombre.getText().trim());
            ps.setString(2, txtEspecialidad.getText().trim());
            ps.executeUpdate();

            mostrarMensaje("✅ Docente creado correctamente.", false);
            limpiarFormulario();
            cargarDocentes();

        } catch (SQLException e) {
            mostrarMensaje("❌ Error al crear docente: " + e.getMessage(), true);
        }
    }

    /**
     * Actualiza los datos del docente seleccionado.
     */
    private void actualizarDocente() {
        String sql = "UPDATE Docente SET nombre = ?, especialidad = ? WHERE id_docente = ?";

        try (Connection conn = ConexionDB.getConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, txtNombre.getText().trim());
            ps.setString(2, txtEspecialidad.getText().trim());
            ps.setInt(3, docenteSeleccionado.getIdDocente());
            ps.executeUpdate();

            mostrarMensaje("✅ Docente actualizado correctamente.", false);
            limpiarFormulario();
            cargarDocentes();

        } catch (SQLException e) {
            mostrarMensaje("❌ Error al actualizar docente: " + e.getMessage(), true);
        }
    }

    /**
     * Se ejecuta cuando el usuario selecciona un docente en la tabla.
     */
    public void onSeleccionTabla(Docente seleccionado) {
        if (seleccionado != null) {
            docenteSeleccionado = seleccionado;
            txtNombre.setText(seleccionado.getNombre());
            txtEspecialidad.setText(seleccionado.getEspecialidad());
            btnGuardar.setText("Actualizar");
            if (lblMensaje != null) lblMensaje.setText("Editando: " + seleccionado.getNombre());
        }
    }

    // ===================== Utilidades =====================

    /**
     * Valida que los campos no estén vacíos.
     */
    private boolean validarCampos() {
        if (txtNombre.getText().trim().isEmpty() ||
            txtEspecialidad.getText().trim().isEmpty()) {
            mostrarMensaje("⚠️ Todos los campos son obligatorios.", true);
            return false;
        }
        return true;
    }

    /**
     * Limpia el formulario y regresa al modo de creación.
     */
    public void limpiarFormulario() {
        txtNombre.clear();
        txtEspecialidad.clear();
        docenteSeleccionado = null;
        btnGuardar.setText("Guardar");
        tablaDocentes.getSelectionModel().clearSelection();
        if (lblMensaje != null) lblMensaje.setText("");
    }

    /**
     * Muestra un mensaje en el label.
     */
    private void mostrarMensaje(String mensaje, boolean esError) {
        if (lblMensaje != null) {
            lblMensaje.setText(mensaje);
            lblMensaje.setStyle(esError
            ? "-fx-text-fill: #e74c3c; -fx-font-weight: bold;"
            : "-fx-text-fill: #27ae60; -fx-font-weight: bold;");
    }
}
}
