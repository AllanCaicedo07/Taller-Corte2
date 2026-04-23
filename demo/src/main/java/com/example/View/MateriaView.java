package com.example.View;

import com.example.controller.MateriaController;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import com.example.Model.Materia;

/**
 * Vista: MateriaView
 * Interfaz gráfica en Java puro (sin FXML) para gestionar Materias.
 * Operaciones: Listar, Crear, Actualizar.
 *
 * Decisión de diseño: Se construye toda la UI programáticamente
 * usando JavaFX. El controlador MateriaController es instanciado
 * aquí y se le inyectan los componentes de la vista para que
 * pueda manipularlos directamente.
 */
public class MateriaView {

    // ===================== Componentes de la vista =====================
    private TableView<Materia>            tablaMateria;
    private TableColumn<Materia, Integer> colId;
    private TableColumn<Materia, String>  colNombre;
    private TableColumn<Materia, Integer> colCreditos;

    private TextField txtNombreMateria;
    private TextField txtCreditos;
    private Label     lblMensaje;
    private Button    btnGuardar;

    private MateriaController controller;

    /**
     * Inicializa y muestra la ventana de Gestión de Materias.
     * @param stage El Stage principal de JavaFX
     */
    public void start(Stage stage) {
        BorderPane root = construirLayout();
        controller = new MateriaController(this);

        stage.setTitle("Gestión de Materias");
        stage.setScene(new Scene(root, 820, 540));
        stage.show();

        // Carga los datos al abrir la ventana
        controller.cargarMaterias();
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
        Label titulo = new Label("Gestión de Materias");
        titulo.setStyle("-fx-text-fill: white; -fx-font-size: 22px; -fx-font-weight: bold;");

        Label subtitulo = new Label("Crear, Editar y Listar Materias");
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

        // Campo Nombre Materia
        Label lblNombre = new Label("Nombre de la Materia");
        lblNombre.setStyle("-fx-text-fill: #ccccee; -fx-font-size: 12px;");
        txtNombreMateria = new TextField();
        txtNombreMateria.setPromptText("Ej: Programación II");
        aplicarEstiloCampo(txtNombreMateria);

        // Campo Créditos
        Label lblCreditos = new Label("Créditos");
        lblCreditos.setStyle("-fx-text-fill: #ccccee; -fx-font-size: 12px;");
        txtCreditos = new TextField();
        txtCreditos.setPromptText("Ej: 3");
        aplicarEstiloCampo(txtCreditos);

        // Mensaje de estado
        lblMensaje = new Label("");
        lblMensaje.setWrapText(true);
        lblMensaje.setStyle("-fx-font-size: 12px;");

        // Botones
        btnGuardar = crearBoton("Guardar", "#5b5bff");
        btnGuardar.setOnAction(e -> controller.guardarMateria());

        Button btnLimpiar = crearBoton("Limpiar", "#444466");
        btnLimpiar.setOnAction(e -> controller.limpiarFormulario());

        Button btnRecargar = crearBoton("↻ Recargar Lista", "#2d6a4f");
        btnRecargar.setOnAction(e -> controller.cargarMaterias());

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        VBox formulario = new VBox(12,
                lblTitulo,
                new Separator(),
                lblNombre, txtNombreMateria,
                lblCreditos, txtCreditos,
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

    @SuppressWarnings("unchecked")
    private VBox construirTabla() {
        Label lblTitulo = new Label("Lista de Materias");
        lblTitulo.setStyle("-fx-text-fill: #a0a0ff; -fx-font-size: 15px; -fx-font-weight: bold;");

        colId = new TableColumn<>("ID");
        colId.setPrefWidth(60);
        colId.setCellValueFactory(new PropertyValueFactory<>("idMateria"));

        colNombre = new TableColumn<>("Nombre Materia");
        colNombre.setPrefWidth(280);
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombreMateria"));

        colCreditos = new TableColumn<>("Créditos");
        colCreditos.setPrefWidth(120);
        colCreditos.setCellValueFactory(new PropertyValueFactory<>("creditos"));

        tablaMateria = new TableView<>();
        tablaMateria.getColumns().addAll(colId, colNombre, colCreditos);
        tablaMateria.setStyle("-fx-background-color: #2d2d44; -fx-table-cell-border-color: #444466;");
        tablaMateria.setPlaceholder(new Label("No hay materias registradas."));
        VBox.setVgrow(tablaMateria, Priority.ALWAYS);

        // Al seleccionar una fila, notifica al controller
        tablaMateria.getSelectionModel().selectedItemProperty().addListener(
            (obs, anterior, seleccionado) -> controller.onSeleccionTabla(seleccionado)
        );

        Label lblAyuda = new Label("💡 Haz clic en una materia de la tabla para editarla.");
        lblAyuda.setStyle("-fx-text-fill: #888899; -fx-font-size: 11px;");

        VBox contenedor = new VBox(10, lblTitulo, new Separator(), tablaMateria, lblAyuda);
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

    public TableView<Materia> getTablaMateria()     { return tablaMateria; }
    public TextField getTxtNombreMateria()          { return txtNombreMateria; }
    public TextField getTxtCreditos()               { return txtCreditos; }
    public Label getLblMensaje()                    { return lblMensaje; }
    public Button getBtnGuardar()                   { return btnGuardar; }
}
