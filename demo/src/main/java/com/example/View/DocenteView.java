package com.example.View;

import com.example.controller.DocenteController;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import com.example.Model.Docente;

/**
 * Vista: DocenteView
 * Interfaz gráfica en Java puro (sin FXML) para gestionar Docentes.
 * Operaciones: Listar, Crear, Actualizar.
 *
 * Decisión de diseño: Se construye toda la UI programáticamente
 * usando JavaFX. El controlador DocenteController es instanciado
 * aquí y se le inyectan los componentes de la vista mediante
 * getters para que pueda manipularlos directamente.
 */
public class DocenteView {

    // ===================== Componentes de la vista =====================
    private TableView<Docente>            tablaDocentes;
    private TableColumn<Docente, Integer> colId;
    private TableColumn<Docente, String>  colNombre;
    private TableColumn<Docente, String>  colEspecialidad;

    private TextField txtNombre;
    private TextField txtEspecialidad;
    private Label     lblMensaje;
    private Button    btnGuardar;

    private DocenteController controller;

    /**
     * Inicializa y muestra la ventana de Gestión de Docentes.
     * @param stage El Stage principal de JavaFX
     */
    public void start(Stage stage) {
        controller = new DocenteController(this);

        stage.setTitle("Gestión de Docentes");
        stage.setScene(new Scene(construirLayout(), 820, 540));
        stage.show();

        // Carga los datos al abrir la ventana
        controller.cargarDocentes();
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
        Label titulo = new Label("Gestión de Docentes");
        titulo.setStyle("-fx-text-fill: white; -fx-font-size: 22px; -fx-font-weight: bold;");

        Label subtitulo = new Label("Crear, Editar y Listar Docentes");
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

        // Campo Nombre
        Label lblNombre = new Label("Nombre del Docente");
        lblNombre.setStyle("-fx-text-fill: #ccccee; -fx-font-size: 12px;");
        txtNombre = new TextField();
        txtNombre.setPromptText("Ej: Carlos Pérez");
        aplicarEstiloCampo(txtNombre);

        // Campo Especialidad
        Label lblEspecialidad = new Label("Especialidad");
        lblEspecialidad.setStyle("-fx-text-fill: #ccccee; -fx-font-size: 12px;");
        txtEspecialidad = new TextField();
        txtEspecialidad.setPromptText("Ej: Ingeniería de Software");
        aplicarEstiloCampo(txtEspecialidad);

        // Mensaje de estado
        lblMensaje = new Label("");
        lblMensaje.setWrapText(true);
        lblMensaje.setStyle("-fx-font-size: 12px;");

        // Botones
        btnGuardar = crearBoton("Guardar", "#5b5bff");
        btnGuardar.setOnAction(e -> controller.guardarDocente());

        Button btnLimpiar = crearBoton("Limpiar", "#444466");
        btnLimpiar.setOnAction(e -> controller.limpiarFormulario());

        Button btnRecargar = crearBoton("↻ Recargar Lista", "#2d6a4f");
        btnRecargar.setOnAction(e -> controller.cargarDocentes());

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        VBox formulario = new VBox(12,
                lblTitulo,
                new Separator(),
                lblNombre,       txtNombre,
                lblEspecialidad, txtEspecialidad,
                spacer,
                lblMensaje,
                new Separator(),
                btnGuardar, btnLimpiar, btnRecargar
        );
        formulario.setStyle("-fx-background-color: #2d2d44; -fx-padding: 24;");
        formulario.setMinWidth(260);
        return formulario;
    }

    // ===================== Tabla =====================

    private VBox construirTabla() {
        Label lblTitulo = new Label("Lista de Docentes");
        lblTitulo.setStyle("-fx-text-fill: #a0a0ff; -fx-font-size: 15px; -fx-font-weight: bold;");

        colId = new TableColumn<>("ID");
        colId.setPrefWidth(60);
        colId.setCellValueFactory(new PropertyValueFactory<>("idDocente"));

        colNombre = new TableColumn<>("Nombre");
        colNombre.setPrefWidth(220);
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));

        colEspecialidad = new TableColumn<>("Especialidad");
        colEspecialidad.setPrefWidth(250);
        colEspecialidad.setCellValueFactory(new PropertyValueFactory<>("especialidad"));

        tablaDocentes = new TableView<>();
        tablaDocentes.getColumns().addAll(colId, colNombre, colEspecialidad);
        tablaDocentes.setStyle(
            "-fx-background-color: #2d2d44; -fx-table-cell-border-color: #444466;"
        );
        tablaDocentes.setPlaceholder(new Label("No hay docentes registrados."));
        VBox.setVgrow(tablaDocentes, Priority.ALWAYS);

        // Al seleccionar una fila notifica al controller
        tablaDocentes.getSelectionModel().selectedItemProperty().addListener(
            (obs, anterior, seleccionado) -> controller.onSeleccionTabla(seleccionado)
        );

        Label lblAyuda = new Label("💡 Haz clic en un docente de la tabla para editarlo.");
        lblAyuda.setStyle("-fx-text-fill: #888899; -fx-font-size: 11px;");

        VBox contenedor = new VBox(10, lblTitulo, new Separator(), tablaDocentes, lblAyuda);
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

    public TableView<Docente> getTablaDocentes()   { return tablaDocentes; }
    public TextField          getTxtNombre()        { return txtNombre; }
    public TextField          getTxtEspecialidad()  { return txtEspecialidad; }
    public Label              getLblMensaje()        { return lblMensaje; }
    public Button             getBtnGuardar()        { return btnGuardar; }
}
