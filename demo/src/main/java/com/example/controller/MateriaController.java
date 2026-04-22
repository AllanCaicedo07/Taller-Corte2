package com.example.controller;

import com.example.DB.ConexionDB;
import com.example.Model.Materia;
import com.example.View.MateriaView;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Controlador: MateriaController
 * Maneja la lógica entre la vista MateriaView
 * y la base de datos mediante ConexionDB (lee config.properties).
 *
 * CORRECCIÓN: Se eliminaron las credenciales hardcodeadas y se
 * reemplazaron por ConexionDB.getConexion(), igual que el resto
 * de controladores del proyecto.
 */
public class MateriaController {

    // ===================== Componentes de la vista =====================
    private TableView<Materia> tablaMateria;
    private TextField          txtNombreMateria;
    private TextField          txtCreditos;
    private Label              lblMensaje;
    private Button             btnGuardar;

    // ===================== Estado interno =====================
    private Materia materiaSeleccionada = null;

    // ===================== Constructor =====================

    public MateriaController(MateriaView vista) {
        this.tablaMateria     = vista.getTablaMateria();
        this.txtNombreMateria = vista.getTxtNombreMateria();
        this.txtCreditos      = vista.getTxtCreditos();
        this.lblMensaje       = vista.getLblMensaje();
        this.btnGuardar       = vista.getBtnGuardar();
    }

    // ===================== Operaciones CRUD =====================

    /**
     * Carga todas las materias desde la base de datos.
     */
    public void cargarMaterias() {
        List<Materia> materias = new ArrayList<>();
        String sql = "SELECT id_materia, nombre_materia, creditos FROM Materia ORDER BY id_materia";

        try (Connection conn = ConexionDB.getConexion();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                materias.add(new Materia(
                    rs.getInt("id_materia"),
                    rs.getString("nombre_materia"),
                    rs.getInt("creditos")
                ));
            }
            ObservableList<Materia> lista = FXCollections.observableArrayList(materias);
            tablaMateria.setItems(lista);
            mostrarMensaje("✅ " + materias.size() + " materia(s) cargada(s).", false);

        } catch (SQLException e) {
            mostrarMensaje("❌ Error al cargar materias: " + e.getMessage(), true);
        }
    }

    /**
     * Decide si crear o actualizar según si hay materia seleccionada.
     */
    public void guardarMateria() {
        if (!validarCampos()) return;

        if (materiaSeleccionada == null) {
            crearMateria();
        } else {
            actualizarMateria();
        }
    }

    /**
     * Inserta una nueva materia en la base de datos.
     */
    private void crearMateria() {
        String sql = "INSERT INTO Materia (nombre_materia, creditos) VALUES (?, ?)";

        try (Connection conn = ConexionDB.getConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, txtNombreMateria.getText().trim());
            ps.setInt(2, Integer.parseInt(txtCreditos.getText().trim()));
            ps.executeUpdate();

            mostrarMensaje("✅ Materia creada correctamente.", false);
            limpiarFormulario();
            cargarMaterias();

        } catch (SQLException e) {
            mostrarMensaje("❌ Error al crear materia: " + e.getMessage(), true);
        }
    }

    /**
     * Actualiza los datos de la materia seleccionada.
     */
    private void actualizarMateria() {
        String sql = "UPDATE Materia SET nombre_materia = ?, creditos = ? WHERE id_materia = ?";

        try (Connection conn = ConexionDB.getConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, txtNombreMateria.getText().trim());
            ps.setInt(2, Integer.parseInt(txtCreditos.getText().trim()));
            ps.setInt(3, materiaSeleccionada.getIdMateria());
            ps.executeUpdate();

            mostrarMensaje("✅ Materia actualizada correctamente.", false);
            limpiarFormulario();
            cargarMaterias();

        } catch (SQLException e) {
            mostrarMensaje("❌ Error al actualizar materia: " + e.getMessage(), true);
        }
    }

    /**
     * Se ejecuta cuando el usuario selecciona una materia en la tabla.
     */
    public void onSeleccionTabla(Materia seleccionada) {
        if (seleccionada != null) {
            materiaSeleccionada = seleccionada;
            txtNombreMateria.setText(seleccionada.getNombreMateria());
            txtCreditos.setText(String.valueOf(seleccionada.getCreditos()));
            btnGuardar.setText("Actualizar");
            if (lblMensaje != null) lblMensaje.setText("Editando: " + seleccionada.getNombreMateria());
        }
    }

    // ===================== Utilidades =====================

    /**
     * Valida que los campos no estén vacíos y que los créditos
     * sean un número entero positivo mayor a 0.
     */
    private boolean validarCampos() {
        if (txtNombreMateria.getText().trim().isEmpty() ||
            txtCreditos.getText().trim().isEmpty()) {
            mostrarMensaje("⚠️ Todos los campos son obligatorios.", true);
            return false;
        }
        try {
            int creditos = Integer.parseInt(txtCreditos.getText().trim());
            if (creditos <= 0) {
                mostrarMensaje("⚠️ Los créditos deben ser un número mayor a 0.", true);
                return false;
            }
        } catch (NumberFormatException e) {
            mostrarMensaje("⚠️ Los créditos deben ser un número entero.", true);
            return false;
        }
        return true;
    }

    /**
     * Limpia el formulario y regresa al modo de creación.
     */
    public void limpiarFormulario() {
        txtNombreMateria.clear();
        txtCreditos.clear();
        materiaSeleccionada = null;
        btnGuardar.setText("Guardar");
        tablaMateria.getSelectionModel().clearSelection();
        if (lblMensaje != null) lblMensaje.setText("");
    }

    /**
     * Muestra un mensaje en el label de estado.
     */
    private void mostrarMensaje(String mensaje, boolean esError) {
        if (lblMensaje != null) {
            if (lblMensaje != null) lblMensaje.setText(mensaje);
            lblMensaje.setStyle(esError
                ? "-fx-text-fill: #e74c3c; -fx-font-weight: bold;"
                : "-fx-text-fill: #27ae60; -fx-font-weight: bold;");
        }
    }
}
