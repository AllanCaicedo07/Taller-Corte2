package com.example.View;

import com.example.controller.InscripcionCursoController;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import com.example.Model.InscripcionCurso;

/**
 * Vista: InscripcionCursoView
 * Interfaz gráfica en Java puro (sin FXML) para gestionar Inscripciones.
 * Operaciones: Listar, Crear, Actualizar.
 *
 * Decisión de diseño: Se usan ComboBox dinámicos para Estudiante y Grupo,
 * cargados desde la BD por el controller al iniciar.
 * La nota es opcional (campo vacío = null en BD) porque un estudiante
 * activo puede no tener calificación aún.
 * El estado se elige de un ComboBox fijo con las 4 opciones permitidas
 * por la BD: Activo, Retirado, Aprobado, Reprobado.
 */
public class InscripcionCursoView {

    // ===================== Componentes de la vista =====================
    private TableView<InscripcionCurso>              tablaInscripciones;
    private TableColumn<InscripcionCurso, Integer>   colId;
    private TableColumn<InscripcionCurso, Integer>   colIdEstudiante;
    private TableColumn<InscripcionCurso, Integer>   colIdGrupo;
    private TableColumn<InscripcionCurso, Double>    colNotaFinal;
    private TableColumn<InscripcionCurso, String>    colEstado;

    private ComboBox<String> cmbEstudiante;
    private ComboBox<String> cmbGrupo;
    private TextField        txtNotaFinal;
    private ComboBox<String> cmbEstado;
    private Label            lblMensaje;
    private Button           btnGuardar;

    private InscripcionCursoController controller;

    /**
     * Inicializa y muestra la ventana de Gestión de Inscripciones.
     * @param stage El Stage principal de JavaFX
     */
    public void start(Stage stage) {
        controller = new InscripcionCursoController(this);

        stage.setTitle("Gestión de Inscripciones");
        stage.setScene(new Scene(construirLayout(), 1000, 580));
        stage.show();

        // Carga ComboBox y tabla al abrir la ventana
        controller.cargarComboBoxEstudiantes();
        controller.cargarComboBoxGrupos();
        controller.configurarComboEstado();
        controller.cargarInscripciones();
    }

    /**
     * Construye el layout principal de la vista (BorderPane).
     */
    private BorderPane construirLayout() {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #1e1e2e;");
        root.setTop(construirEncabezado());
        root.setLeft(construirFormulario());
        root.setCenter(construirTabla());
        return root;
    }

    // ===================== Encabezado =====================

    private VBox construirEncabezado() {
        Label titulo = new Label("Gestión de Inscripciones");
        titulo.setStyle("-fx-text-fill: white; -fx-font-size: 22px; -fx-font-weight: bold;");

        Label subtitulo = new Label("Crear, Editar y Listar Inscripciones de Curso");
        subtitulo.setStyle("-fx-text-fill: #a0a0c0; -fx-font-size: 13px;");

        VBox encabezado = new VBox(4, titulo, subtitulo);
        encabezado.setAlignment(Pos.CENTER);
        encabezado.setStyle("-fx-background-color: #2d2d44; -fx-padding: 16;");
        return encabezado;
    }

    // ===================== Formulario =====================

    private VBox construirFormulario() {
        Label lblTitulo = new Label("Formulario");
        lblTitulo.setStyle("-fx-text-fill: #a0a0ff; -fx-font-size: 15px; -fx-font-weight: bold;");

        // ComboBox Estudiante
        Label lblEstudiante = new Label("Estudiante");
        lblEstudiante.setStyle("-fx-text-fill: #ccccee; -fx-font-size: 12px;");
        cmbEstudiante = new ComboBox<>();
        cmbEstudiante.setPromptText("Selecciona un estudiante");
        cmbEstudiante.setMaxWidth(Double.MAX_VALUE);
        aplicarEstiloCombo(cmbEstudiante);

        // ComboBox Grupo
        Label lblGrupo = new Label("Grupo");
        lblGrupo.setStyle("-fx-text-fill: #ccccee; -fx-font-size: 12px;");
        cmbGrupo = new ComboBox<>();
        cmbGrupo.setPromptText("Selecciona un grupo");
        cmbGrupo.setMaxWidth(Double.MAX_VALUE);
        aplicarEstiloCombo(cmbGrupo);

        // Campo Nota Final (opcional)
        Label lblNota = new Label("Nota Final (opcional, 0.0 - 5.0)");
        lblNota.setStyle("-fx-text-fill: #ccccee; -fx-font-size: 12px;");
        txtNotaFinal = new TextField();
        txtNotaFinal.setPromptText("Dejar vacío si aún no hay nota");
        aplicarEstiloCampo(txtNotaFinal);

        // ComboBox Estado
        Label lblEstado = new Label("Estado");
        lblEstado.setStyle("-fx-text-fill: #ccccee; -fx-font-size: 12px;");
        cmbEstado = new ComboBox<>();
        cmbEstado.setMaxWidth(Double.MAX_VALUE);
        aplicarEstiloCombo(cmbEstado);

        // Mensaje de estado
        lblMensaje = new Label("");
        lblMensaje.setWrapText(true);
        lblMensaje.setStyle("-fx-font-size: 12px;");

        // Botones
        btnGuardar = crearBoton("Guardar", "#5b5bff");
        btnGuardar.setOnAction(e -> controller.guardarInscripcion());

        Button btnLimpiar = crearBoton("Limpiar", "#444466");
        btnLimpiar.setOnAction(e -> controller.limpiarFormulario());

        Button btnRecargar = crearBoton("↻ Recargar Lista", "#2d6a4f");
        btnRecargar.setOnAction(e -> controller.cargarInscripciones());

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        VBox formulario = new VBox(12,
                lblTitulo,
                new Separator(),
                lblEstudiante, cmbEstudiante,
                lblGrupo,      cmbGrupo,
                lblNota,       txtNotaFinal,
                lblEstado,     cmbEstado,
                spacer,
                lblMensaje,
                new Separator(),
                btnGuardar, btnLimpiar, btnRecargar
        );
        formulario.setStyle("-fx-background-color: #2d2d44; -fx-padding: 24;");
        formulario.setMinWidth(300);
        return formulario;
    }

    // ===================== Tabla =====================

    private VBox construirTabla() {
        Label lblTitulo = new Label("Lista de Inscripciones");
        lblTitulo.setStyle("-fx-text-fill: #a0a0ff; -fx-font-size: 15px; -fx-font-weight: bold;");

        colId = new TableColumn<>("ID");
        colId.setPrefWidth(50);
        colId.setCellValueFactory(new PropertyValueFactory<>("idInscripcion"));

        colIdEstudiante = new TableColumn<>("ID Estudiante");
        colIdEstudiante.setPrefWidth(110);
        colIdEstudiante.setCellValueFactory(new PropertyValueFactory<>("idEstudiante"));

        colIdGrupo = new TableColumn<>("ID Grupo");
        colIdGrupo.setPrefWidth(90);
        colIdGrupo.setCellValueFactory(new PropertyValueFactory<>("idGrupo"));

        colNotaFinal = new TableColumn<>("Nota Final");
        colNotaFinal.setPrefWidth(100);
        colNotaFinal.setCellValueFactory(new PropertyValueFactory<>("notaFinal"));

        colEstado = new TableColumn<>("Estado");
        colEstado.setPrefWidth(120);
        colEstado.setCellValueFactory(new PropertyValueFactory<>("estado"));

        tablaInscripciones = new TableView<>();
        tablaInscripciones.getColumns().addAll(
            java.util.Arrays.asList(colId, colIdEstudiante, colIdGrupo, colNotaFinal, colEstado)
        );
        tablaInscripciones.setStyle(
            "-fx-background-color: #2d2d44; -fx-table-cell-border-color: #444466;"
        );
        tablaInscripciones.setPlaceholder(new Label("No hay inscripciones registradas."));
        VBox.setVgrow(tablaInscripciones, Priority.ALWAYS);

        // Al seleccionar una fila, notifica al controller
        tablaInscripciones.getSelectionModel().selectedItemProperty().addListener(
            (obs, anterior, seleccionado) -> controller.onSeleccionTabla(seleccionado)
        );

        // Leyenda de estados
        Label activo    = new Label("🟢 Activo");
        Label reprobado = new Label("🔴 Reprobado");
        Label aprobado  = new Label("🔵 Aprobado");
        Label retirado  = new Label("⚪ Retirado");
        activo.setStyle("-fx-text-fill: #27ae60; -fx-font-size: 11px;");
        reprobado.setStyle("-fx-text-fill: #e74c3c; -fx-font-size: 11px;");
        aprobado.setStyle("-fx-text-fill: #3498db; -fx-font-size: 11px;");
        retirado.setStyle("-fx-text-fill: #888899; -fx-font-size: 11px;");

        HBox leyenda = new HBox(16, activo, reprobado, aprobado, retirado);

        Label lblAyuda = new Label("💡 Haz clic en una inscripción para editarla.");
        lblAyuda.setStyle("-fx-text-fill: #888899; -fx-font-size: 11px;");

        VBox contenedor = new VBox(10,
            lblTitulo, new Separator(), tablaInscripciones, leyenda, lblAyuda
        );
        contenedor.setStyle("-fx-padding: 20; -fx-background-color: #1e1e2e;");
        return contenedor;
    }

    // ===================== Utilidades de estilo =====================

    private void aplicarEstiloCampo(TextField campo) {
        campo.setStyle(
            "-fx-background-color: #1e1e2e;" +
            "-fx-text-fill: white;" +
            "-fx-prompt-text-fill: #666688;" +
            "-fx-border-color: #444466;" +
            "-fx-border-radius: 6;" +
            "-fx-background-radius: 6;" +
            "-fx-padding: 8;"
        );
    }

    private void aplicarEstiloCombo(ComboBox<String> combo) {
        combo.setStyle(
            "-fx-background-color: #1e1e2e;" +
            "-fx-text-fill: white;" +
            "-fx-border-color: #444466;" +
            "-fx-border-radius: 6;" +
            "-fx-background-radius: 6;"
        );
    }

    private Button crearBoton(String texto, String color) {
        Button btn = new Button(texto);
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setStyle(
            "-fx-background-color: " + color + ";" +
            "-fx-text-fill: white;" +
            "-fx-font-weight: bold;" +
            "-fx-background-radius: 8;" +
            "-fx-padding: 10;" +
            "-fx-cursor: hand;"
        );
        return btn;
    }

    // ===================== Getters para el Controller =====================

    public TableView<InscripcionCurso> getTablaInscripciones() { return tablaInscripciones; }
    public ComboBox<String>            getCmbEstudiante()      { return cmbEstudiante; }
    public ComboBox<String>            getCmbGrupo()           { return cmbGrupo; }
    public TextField                   getTxtNotaFinal()       { return txtNotaFinal; }
    public ComboBox<String>            getCmbEstado()          { return cmbEstado; }
    public Label                       getLblMensaje()         { return lblMensaje; }
    public Button                      getBtnGuardar()         { return btnGuardar; }
}
