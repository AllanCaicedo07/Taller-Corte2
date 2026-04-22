package com.example.View;

import com.example.controller.GrupoController;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import com.example.Model.Grupo;

/**
 * Vista: GrupoView
 * Interfaz gráfica en Java puro (sin FXML) para gestionar Grupos.
 * Operaciones: Listar, Crear, Actualizar.
 *
 * Decisión de diseño: Se usan ComboBox dinámicos para seleccionar
 * la Materia y el Docente. Estos son cargados por el controller
 * desde la BD al iniciar la vista, evitando que el usuario ingrese
 * IDs manualmente y previniendo errores de integridad referencial.
 */
public class GrupoView {

    // ===================== Componentes de la vista =====================
    private TableView<Grupo>            tablaGrupos;
    private TableColumn<Grupo, Integer> colId;
    private TableColumn<Grupo, Integer> colIdMateria;
    private TableColumn<Grupo, Integer> colIdDocente;
    private TableColumn<Grupo, String>  colAula;
    private TableColumn<Grupo, String>  colHorario;

    private ComboBox<String> cmbMateria;
    private ComboBox<String> cmbDocente;
    private TextField        txtAula;
    private TextField        txtHorario;
    private Label            lblMensaje;
    private Button           btnGuardar;

    private GrupoController controller;

    /**
     * Inicializa y muestra la ventana de Gestión de Grupos.
     * @param stage El Stage principal de JavaFX
     */
    public void start(Stage stage) {
        controller = new GrupoController(this);

        stage.setTitle("Gestión de Grupos");
        stage.setScene(new Scene(construirLayout(), 920, 560));
        stage.show();

        // Carga ComboBox y tabla al abrir la ventana
        controller.cargarComboBoxMaterias();
        controller.cargarComboBoxDocentes();
        controller.cargarGrupos();
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
        Label titulo = new Label("Gestión de Grupos");
        titulo.setStyle("-fx-text-fill: white; -fx-font-size: 22px; -fx-font-weight: bold;");

        Label subtitulo = new Label("Crear, Editar y Listar Grupos de Clase");
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

        // ComboBox Materia
        Label lblMateria = new Label("Materia");
        lblMateria.setStyle("-fx-text-fill: #ccccee; -fx-font-size: 12px;");
        cmbMateria = new ComboBox<>();
        cmbMateria.setPromptText("Selecciona una materia");
        cmbMateria.setMaxWidth(Double.MAX_VALUE);
        aplicarEstiloCombo(cmbMateria);

        // ComboBox Docente
        Label lblDocente = new Label("Docente");
        lblDocente.setStyle("-fx-text-fill: #ccccee; -fx-font-size: 12px;");
        cmbDocente = new ComboBox<>();
        cmbDocente.setPromptText("Selecciona un docente");
        cmbDocente.setMaxWidth(Double.MAX_VALUE);
        aplicarEstiloCombo(cmbDocente);

        // Campo Aula
        Label lblAula = new Label("Aula");
        lblAula.setStyle("-fx-text-fill: #ccccee; -fx-font-size: 12px;");
        txtAula = new TextField();
        txtAula.setPromptText("Ej: Aula 101");
        aplicarEstiloCampo(txtAula);

        // Campo Horario
        Label lblHorario = new Label("Horario");
        lblHorario.setStyle("-fx-text-fill: #ccccee; -fx-font-size: 12px;");
        txtHorario = new TextField();
        txtHorario.setPromptText("Ej: Lunes y Miércoles 8:00 - 10:00");
        aplicarEstiloCampo(txtHorario);

        // Mensaje de estado
        lblMensaje = new Label("");
        lblMensaje.setWrapText(true);
        lblMensaje.setStyle("-fx-font-size: 12px;");

        // Botones
        btnGuardar = crearBoton("Guardar", "#5b5bff");
        btnGuardar.setOnAction(e -> controller.guardarGrupo());

        Button btnLimpiar = crearBoton("Limpiar", "#444466");
        btnLimpiar.setOnAction(e -> controller.limpiarFormulario());

        Button btnRecargar = crearBoton("↻ Recargar Lista", "#2d6a4f");
        btnRecargar.setOnAction(e -> controller.cargarGrupos());

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        VBox formulario = new VBox(12,
                lblTitulo,
                new Separator(),
                lblMateria,  cmbMateria,
                lblDocente,  cmbDocente,
                lblAula,     txtAula,
                lblHorario,  txtHorario,
                spacer,
                lblMensaje,
                new Separator(),
                btnGuardar, btnLimpiar, btnRecargar
        );
        formulario.setStyle("-fx-background-color: #2d2d44; -fx-padding: 24;");
        formulario.setMinWidth(290);
        return formulario;
    }

    // ===================== Tabla =====================

    private VBox construirTabla() {
        Label lblTitulo = new Label("Lista de Grupos");
        lblTitulo.setStyle("-fx-text-fill: #a0a0ff; -fx-font-size: 15px; -fx-font-weight: bold;");

        colId = new TableColumn<>("ID");
        colId.setPrefWidth(50);
        colId.setCellValueFactory(new PropertyValueFactory<>("idGrupo"));

        colIdMateria = new TableColumn<>("ID Materia");
        colIdMateria.setPrefWidth(100);
        colIdMateria.setCellValueFactory(new PropertyValueFactory<>("idMateria"));

        colIdDocente = new TableColumn<>("ID Docente");
        colIdDocente.setPrefWidth(100);
        colIdDocente.setCellValueFactory(new PropertyValueFactory<>("idDocente"));

        colAula = new TableColumn<>("Aula");
        colAula.setPrefWidth(110);
        colAula.setCellValueFactory(new PropertyValueFactory<>("aula"));

        colHorario = new TableColumn<>("Horario");
        colHorario.setPrefWidth(220);
        colHorario.setCellValueFactory(new PropertyValueFactory<>("horario"));

        tablaGrupos = new TableView<>();
        tablaGrupos.getColumns().addAll(colId, colIdMateria, colIdDocente, colAula, colHorario);
        tablaGrupos.setStyle("-fx-background-color: #2d2d44; -fx-table-cell-border-color: #444466;");
        tablaGrupos.setPlaceholder(new Label("No hay grupos registrados."));
        VBox.setVgrow(tablaGrupos, Priority.ALWAYS);

        // Al seleccionar una fila, notifica al controller
        tablaGrupos.getSelectionModel().selectedItemProperty().addListener(
            (obs, anterior, seleccionado) -> controller.onSeleccionTabla(seleccionado)
        );

        Label lblAyuda = new Label("💡 Haz clic en un grupo de la tabla para editarlo.");
        lblAyuda.setStyle("-fx-text-fill: #888899; -fx-font-size: 11px;");

        VBox contenedor = new VBox(10, lblTitulo, new Separator(), tablaGrupos, lblAyuda);
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

    public TableView<Grupo>  getTablaGrupos()  { return tablaGrupos; }
    public ComboBox<String>  getCmbMateria()   { return cmbMateria; }
    public ComboBox<String>  getCmbDocente()   { return cmbDocente; }
    public TextField         getTxtAula()      { return txtAula; }
    public TextField         getTxtHorario()   { return txtHorario; }
    public Label             getLblMensaje()   { return lblMensaje; }
    public Button            getBtnGuardar()   { return btnGuardar; }
}
