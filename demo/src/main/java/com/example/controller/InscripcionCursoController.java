package com.example.controller;

import com.example.Model.InscripcionCurso;
import com.example.View.InscripcionCursoView;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.*;

import com.example.DB.ConexionDB;
import java.sql.*;
import java.util.*;

/**
 * Controlador: InscripcionCursoController
 * Maneja la lógica entre la vista InscripcionCursoView
 * y la base de datos PostgreSQL en Neon mediante JDBC.
 */
public class InscripcionCursoController {

    // ===================== Conexión Neon =====================
    // Conexión centralizada via ConexionDB
    
    

    // ===================== Componentes de la vista =====================
    private TableView<InscripcionCurso>  tablaInscripciones;
    private ComboBox<String>             cmbEstudiante;
    private ComboBox<String>             cmbGrupo;
    private TextField                    txtNotaFinal;
    private ComboBox<String>             cmbEstado;
    private Label                        lblMensaje;
    private Button                       btnGuardar;

    // ===================== Estado interno =====================
    private InscripcionCurso inscripcionSeleccionada = null;
    private ObservableList<String>       estudianteItems = FXCollections.observableArrayList();
    private ObservableList<String>       grupoItems      = FXCollections.observableArrayList();
    private Map<String, Integer>         mapaEstudiantes = new LinkedHashMap<>();
    private Map<String, Integer>         mapaGrupos      = new LinkedHashMap<>();

    // ===================== Constructor =====================

    public InscripcionCursoController(InscripcionCursoView vista) {
        this.tablaInscripciones = vista.getTablaInscripciones();
        this.cmbEstudiante = vista.getCmbEstudiante();
        this.cmbGrupo = vista.getCmbGrupo();
        this.txtNotaFinal = vista.getTxtNotaFinal();
        this.cmbEstado = vista.getCmbEstado();
        this.lblMensaje = vista.getLblMensaje();
        this.btnGuardar = vista.getBtnGuardar();
    }

    // ===================== Configuración =====================

    /**
     * Configura las opciones de estado en el ComboBox.
     */
    public void configurarComboEstado() {
        cmbEstado.setItems(FXCollections.observableArrayList(
            "Activo", "Retirado", "Aprobado", "Reprobado"
        ));
        cmbEstado.setValue("Activo");
    }

    /**
     * Carga los estudiantes en el ComboBox.
     */
    public void cargarComboBoxEstudiantes() {
        mapaEstudiantes.clear();
        estudianteItems.clear();
        String sql = "SELECT id_estudiante, nombre, apellido FROM Estudiante ORDER BY id_estudiante";

        try (Connection conn = ConexionDB.getConexion();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                String etiqueta = rs.getInt("id_estudiante") + " - "
                        + rs.getString("nombre") + " " + rs.getString("apellido");
                mapaEstudiantes.put(etiqueta, rs.getInt("id_estudiante"));
                estudianteItems.add(etiqueta);
            }
            cmbEstudiante.setItems(estudianteItems);

        } catch (SQLException e) {
            mostrarMensaje("❌ Error al cargar estudiantes: " + e.getMessage(), true);
        }
    }

    /**
     * Carga los grupos disponibles en el ComboBox.
     */
    public void cargarComboBoxGrupos() {
        mapaGrupos.clear();
        grupoItems.clear();
        String sql = "SELECT g.id_grupo, m.nombre_materia, g.aula, g.horario " +
                     "FROM Grupo g JOIN Materia m ON g.id_materia = m.id_materia " +
                     "ORDER BY g.id_grupo";

        try (Connection conn = ConexionDB.getConexion();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                String etiqueta = rs.getInt("id_grupo") + " - "
                        + rs.getString("nombre_materia") + " | "
                        + rs.getString("aula") + " | "
                        + rs.getString("horario");
                mapaGrupos.put(etiqueta, rs.getInt("id_grupo"));
                grupoItems.add(etiqueta);
            }
            cmbGrupo.setItems(grupoItems);

        } catch (SQLException e) {
            mostrarMensaje("❌ Error al cargar grupos: " + e.getMessage(), true);
        }
    }

    // ===================== Operaciones CRUD =====================

    /**
     * Carga todas las inscripciones desde la base de datos.
     */
    public void cargarInscripciones() {
        List<InscripcionCurso> inscripciones = new ArrayList<>();
        String sql = "SELECT id_inscripcion, id_estudiante, id_grupo, nota_final, estado " +
                     "FROM Inscripcion_Curso ORDER BY id_inscripcion";

        try (Connection conn = ConexionDB.getConexion();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Float nota = rs.getObject("nota_final") != null
                        ? rs.getFloat("nota_final") : null;

                inscripciones.add(new InscripcionCurso(
                    rs.getInt("id_inscripcion"),
                    rs.getInt("id_estudiante"),
                    rs.getInt("id_grupo"),
                    nota,
                    rs.getString("estado")
                ));
            }
            ObservableList<InscripcionCurso> lista = FXCollections.observableArrayList(inscripciones);
            tablaInscripciones.setItems(lista);
            mostrarMensaje("✅ " + inscripciones.size() + " inscripción(es) cargada(s).", false);

        } catch (SQLException e) {
            mostrarMensaje("❌ Error al cargar inscripciones: " + e.getMessage(), true);
        }
    }

    /**
     * Decide si crear o actualizar según si hay inscripción seleccionada.
     */
    public void guardarInscripcion() {
        if (!validarCampos()) return;

        if (inscripcionSeleccionada == null) {
            crearInscripcion();
        } else {
            actualizarInscripcion();
        }
    }

    /**
     * Inserta una nueva inscripción en la base de datos.
     */
    private void crearInscripcion() {
        String sql = "INSERT INTO Inscripcion_Curso (id_estudiante, id_grupo, nota_final, estado) " +
                     "VALUES (?, ?, ?, ?)";

        try (Connection conn = ConexionDB.getConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, mapaEstudiantes.get(cmbEstudiante.getValue()));
            ps.setInt(2, mapaGrupos.get(cmbGrupo.getValue()));

            if (txtNotaFinal.getText().trim().isEmpty()) {
                ps.setNull(3, Types.FLOAT);
            } else {
                ps.setDouble(3, Double.parseDouble(txtNotaFinal.getText().trim()));
            }
            ps.setString(4, cmbEstado.getValue());
            ps.executeUpdate();

            mostrarMensaje("✅ Inscripción creada correctamente.", false);
            limpiarFormulario();
            cargarInscripciones();

        } catch (SQLException e) {
            if (e.getMessage().contains("uq_inscripcion")) {
                mostrarMensaje("❌ El estudiante ya está inscrito en ese grupo.", true);
            } else {
                mostrarMensaje("❌ Error al crear inscripción: " + e.getMessage(), true);
            }
        }
    }

    /**
     * Actualiza nota y estado de la inscripción seleccionada.
     */
    private void actualizarInscripcion() {
        String sql = "UPDATE Inscripcion_Curso SET id_estudiante = ?, id_grupo = ?, " +
                     "nota_final = ?, estado = ? WHERE id_inscripcion = ?";

        try (Connection conn = ConexionDB.getConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, mapaEstudiantes.get(cmbEstudiante.getValue()));
            ps.setInt(2, mapaGrupos.get(cmbGrupo.getValue()));

            if (txtNotaFinal.getText().trim().isEmpty()) {
                ps.setNull(3, Types.FLOAT);
            } else {
                ps.setDouble(3, Double.parseDouble(txtNotaFinal.getText().trim()));
            }
            ps.setString(4, cmbEstado.getValue());
            ps.setInt(5, inscripcionSeleccionada.getIdInscripcion());
            ps.executeUpdate();

            mostrarMensaje("✅ Inscripción actualizada correctamente.", false);
            limpiarFormulario();
            cargarInscripciones();

        } catch (SQLException e) {
            mostrarMensaje("❌ Error al actualizar inscripción: " + e.getMessage(), true);
        }
    }

    /**
     * Se ejecuta cuando el usuario selecciona una inscripción en la tabla.
     */
    public void onSeleccionTabla(InscripcionCurso seleccionada) {
        if (seleccionada != null) {
            inscripcionSeleccionada = seleccionada;

            mapaEstudiantes.forEach((k, v) -> {
                if (v == seleccionada.getIdEstudiante()) cmbEstudiante.setValue(k);
            });
            mapaGrupos.forEach((k, v) -> {
                if (v == seleccionada.getIdGrupo()) cmbGrupo.setValue(k);
            });

            txtNotaFinal.setText(seleccionada.getNotaFinal() != null
                ? String.valueOf(seleccionada.getNotaFinal()) : "");

            cmbEstado.setValue(seleccionada.getEstado());
            btnGuardar.setText("Actualizar");
            if (lblMensaje != null) lblMensaje.setText("Editando Inscripción ID: " + seleccionada.getIdInscripcion());
        }
    }

    // ===================== Utilidades =====================

    /**
     * Valida que los campos obligatorios estén completos.
     */
    private boolean validarCampos() {
        if (cmbEstudiante.getValue() == null || cmbGrupo.getValue() == null
                || cmbEstado.getValue() == null) {
            mostrarMensaje("⚠️ Estudiante, Grupo y Estado son obligatorios.", true);
            return false;
        }
        if (!txtNotaFinal.getText().trim().isEmpty()) {
            try {
                double nota = Double.parseDouble(txtNotaFinal.getText().trim());
                if (nota < 0.0 || nota > 5.0) {
                    mostrarMensaje("⚠️ La nota debe estar entre 0.0 y 5.0.", true);
                    return false;
                }
            } catch (NumberFormatException e) {
                mostrarMensaje("⚠️ La nota debe ser un número decimal (Ej: 3.5).", true);
                return false;
            }
        }
        return true;
    }

    /**
     * Limpia el formulario y regresa al modo de creación.
     */
    public void limpiarFormulario() {
        cmbEstudiante.setValue(null);
        cmbGrupo.setValue(null);
        txtNotaFinal.clear();
        cmbEstado.setValue("Activo");
        inscripcionSeleccionada = null;
        btnGuardar.setText("Guardar");
        tablaInscripciones.getSelectionModel().clearSelection();
        if (lblMensaje != null) lblMensaje.setText("");
    }

    /**
     * Muestra un mensaje en el label.
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
