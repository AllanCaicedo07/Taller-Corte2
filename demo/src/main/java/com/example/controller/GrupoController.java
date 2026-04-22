package com.example.controller;

import com.example.Model.Grupo;
import com.example.View.GrupoView;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.*;

import java.sql.*;
import java.util.*;

/**
 * Controlador: GrupoController
 * Maneja la lógica entre la vista GrupoView
 * y la base de datos PostgreSQL en Neon mediante JDBC.
 */
public class GrupoController {

    // ===================== Conexión Neon =====================
    private static final String URL      = "jdbc:postgresql://<host>.neon.tech/<dbname>?sslmode=require";
    private static final String USER     = "tu_usuario";
    private static final String PASSWORD = "tu_contraseña";

    // ===================== Componentes de la vista =====================
    private TableView<Grupo>   tablaGrupos;
    private ComboBox<String>   cmbMateria;
    private ComboBox<String>   cmbDocente;
    private TextField          txtAula;
    private TextField          txtHorario;
    private Label              lblMensaje;
    private Button             btnGuardar;

    // ===================== Estado interno =====================
    private Grupo grupoSeleccionado = null;
    private ObservableList<String>  materiaItems  = FXCollections.observableArrayList();
    private ObservableList<String>  docenteItems  = FXCollections.observableArrayList();
    private Map<String, Integer>    mapaMaterias  = new LinkedHashMap<>();
    private Map<String, Integer>    mapaDocentes  = new LinkedHashMap<>();

    // ===================== Constructor =====================

    public GrupoController(GrupoView vista) {
        this.tablaGrupos = vista.getTablaGrupos();
        this.cmbMateria = vista.getCmbMateria();
        this.cmbDocente = vista.getCmbDocente();
        this.txtAula = vista.getTxtAula();
        this.txtHorario = vista.getTxtHorario();
        this.lblMensaje = vista.getLblMensaje();
        this.btnGuardar = vista.getBtnGuardar();
    }

    // ===================== Cargar ComboBox =====================

    /**
     * Carga los nombres de las materias en el ComboBox.
     */
    public void cargarComboBoxMaterias() {
        mapaMaterias.clear();
        materiaItems.clear();
        String sql = "SELECT id_materia, nombre_materia FROM Materia ORDER BY id_materia";

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                String etiqueta = rs.getInt("id_materia") + " - " + rs.getString("nombre_materia");
                mapaMaterias.put(etiqueta, rs.getInt("id_materia"));
                materiaItems.add(etiqueta);
            }
            cmbMateria.setItems(materiaItems);

        } catch (SQLException e) {
            mostrarMensaje("❌ Error al cargar materias: " + e.getMessage(), true);
        }
    }

    /**
     * Carga los nombres de los docentes en el ComboBox.
     */
    public void cargarComboBoxDocentes() {
        mapaDocentes.clear();
        docenteItems.clear();
        String sql = "SELECT id_docente, nombre FROM Docente ORDER BY id_docente";

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                String etiqueta = rs.getInt("id_docente") + " - " + rs.getString("nombre");
                mapaDocentes.put(etiqueta, rs.getInt("id_docente"));
                docenteItems.add(etiqueta);
            }
            cmbDocente.setItems(docenteItems);

        } catch (SQLException e) {
            mostrarMensaje("❌ Error al cargar docentes: " + e.getMessage(), true);
        }
    }

    // ===================== Operaciones CRUD =====================

    /**
     * Carga todos los grupos desde la base de datos.
     */
    public void cargarGrupos() {
        List<Grupo> grupos = new ArrayList<>();
        String sql = "SELECT id_grupo, id_materia, id_docente, aula, horario FROM Grupo ORDER BY id_grupo";

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                grupos.add(new Grupo(
                    rs.getInt("id_grupo"),
                    rs.getInt("id_materia"),
                    rs.getInt("id_docente"),
                    rs.getString("aula"),
                    rs.getString("horario")
                ));
            }
            ObservableList<Grupo> lista = FXCollections.observableArrayList(grupos);
            tablaGrupos.setItems(lista);
            mostrarMensaje("✅ " + grupos.size() + " grupo(s) cargado(s).", false);

        } catch (SQLException e) {
            mostrarMensaje("❌ Error al cargar grupos: " + e.getMessage(), true);
        }
    }

    /**
     * Decide si crear o actualizar según si hay grupo seleccionado.
     */
    public void guardarGrupo() {
        if (!validarCampos()) return;

        if (grupoSeleccionado == null) {
            crearGrupo();
        } else {
            actualizarGrupo();
        }
    }

    /**
     * Inserta un nuevo grupo en la base de datos.
     */
    private void crearGrupo() {
        String sql = "INSERT INTO Grupo (id_materia, id_docente, aula, horario) VALUES (?, ?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, mapaMaterias.get(cmbMateria.getValue()));
            ps.setInt(2, mapaDocentes.get(cmbDocente.getValue()));
            ps.setString(3, txtAula.getText().trim());
            ps.setString(4, txtHorario.getText().trim());
            ps.executeUpdate();

            mostrarMensaje("✅ Grupo creado correctamente.", false);
            limpiarFormulario();
            cargarGrupos();

        } catch (SQLException e) {
            mostrarMensaje("❌ Error al crear grupo: " + e.getMessage(), true);
        }
    }

    /**
     * Actualiza los datos del grupo seleccionado.
     */
    private void actualizarGrupo() {
        String sql = "UPDATE Grupo SET id_materia = ?, id_docente = ?, aula = ?, horario = ? WHERE id_grupo = ?";

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, mapaMaterias.get(cmbMateria.getValue()));
            ps.setInt(2, mapaDocentes.get(cmbDocente.getValue()));
            ps.setString(3, txtAula.getText().trim());
            ps.setString(4, txtHorario.getText().trim());
            ps.setInt(5, grupoSeleccionado.getIdGrupo());
            ps.executeUpdate();

            mostrarMensaje("✅ Grupo actualizado correctamente.", false);
            limpiarFormulario();
            cargarGrupos();

        } catch (SQLException e) {
            mostrarMensaje("❌ Error al actualizar grupo: " + e.getMessage(), true);
        }
    }

    /**
     * Se ejecuta cuando el usuario selecciona un grupo en la tabla.
     */
    public void onSeleccionTabla(Grupo seleccionado) {
        if (seleccionado != null) {
            grupoSeleccionado = seleccionado;
            mapaMaterias.forEach((k, v) -> {
                if (v == seleccionado.getIdMateria()) cmbMateria.setValue(k);
            });
            mapaDocentes.forEach((k, v) -> {
                if (v == seleccionado.getIdDocente()) cmbDocente.setValue(k);
            });
            txtAula.setText(seleccionado.getAula());
            txtHorario.setText(seleccionado.getHorario());
            btnGuardar.setText("Actualizar");
            lblMensaje.setText("Editando Grupo ID: " + seleccionado.getIdGrupo());
        }
    }

    // ===================== Utilidades =====================

    private boolean validarCampos() {
        if (cmbMateria.getValue() == null || cmbDocente.getValue() == null ||
            txtAula.getText().trim().isEmpty() || txtHorario.getText().trim().isEmpty()) {
            mostrarMensaje("⚠️ Todos los campos son obligatorios.", true);
            return false;
        }
        return true;
    }

    /**
     * Limpia el formulario y regresa al modo de creación.
     */
    public void limpiarFormulario() {
        cmbMateria.setValue(null);
        cmbDocente.setValue(null);
        txtAula.clear();
        txtHorario.clear();
        grupoSeleccionado = null;
        btnGuardar.setText("Guardar");
        tablaGrupos.getSelectionModel().clearSelection();
        lblMensaje.setText("");
    }

    /**
     * Muestra un mensaje en el label.
     */
    private void mostrarMensaje(String mensaje, boolean esError) {
        lblMensaje.setText(mensaje);
        lblMensaje.setStyle(esError
            ? "-fx-text-fill: #e74c3c; -fx-font-weight: bold;"
            : "-fx-text-fill: #27ae60; -fx-font-weight: bold;");
    }
}
