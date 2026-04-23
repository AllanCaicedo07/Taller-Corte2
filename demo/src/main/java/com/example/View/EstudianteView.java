package com.example.View;

import com.example.controller.EstudianteController;
import com.example.Model.Estudiante;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.Stage;

/**
 * Vista: EstudianteView
 * Reescrita en JavaFX puro para ser compatible con el resto del proyecto.
 * Operaciones: Listar, Crear, Actualizar, Eliminar.
 */
public class EstudianteView {

    // ===================== Componentes =====================
    private TableView<Estudiante>            tablaEstudiantes;
    private TableColumn<Estudiante, Integer> colId;
    private TableColumn<Estudiante, String>  colNombre;
    private TableColumn<Estudiante, String>  colApellido;
    private TableColumn<Estudiante, String>  colEmail;

    private TextField txtNombre;
    private TextField txtApellido;
    private TextField txtEmail;
    private Label     lblMensaje;
    private Button    btnGuardar;
    private Button    btnEliminar;

    private EstudianteController controller;
    private Estudiante estudianteSeleccionado = null;

    public void start(Stage stage) {
        controller = new EstudianteController();

        stage.setTitle("Gestión de Estudiantes");
        stage.setScene(new Scene(construirLayout(), 900, 560));
        stage.show();

        cargarEstudiantes();
    }

    // ===================== Layout =====================

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
        Label titulo = new Label("Gestión de Estudiantes");
        titulo.setStyle("-fx-text-fill: white; -fx-font-size: 22px; -fx-font-weight: bold;");

        Label subtitulo = new Label("Crear, Editar, Eliminar y Listar Estudiantes");
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

        Label lblNombre = new Label("Nombre");
        lblNombre.setStyle("-fx-text-fill: #ccccee; -fx-font-size: 12px;");
        txtNombre = new TextField();
        txtNombre.setPromptText("Ej: Juan");
        aplicarEstiloCampo(txtNombre);

        Label lblApellido = new Label("Apellido");
        lblApellido.setStyle("-fx-text-fill: #ccccee; -fx-font-size: 12px;");
        txtApellido = new TextField();
        txtApellido.setPromptText("Ej: Martínez");
        aplicarEstiloCampo(txtApellido);

        Label lblEmail = new Label("Email");
        lblEmail.setStyle("-fx-text-fill: #ccccee; -fx-font-size: 12px;");
        txtEmail = new TextField();
        txtEmail.setPromptText("Ej: juan@email.com");
        aplicarEstiloCampo(txtEmail);

        lblMensaje = new Label("");
        lblMensaje.setWrapText(true);
        lblMensaje.setStyle("-fx-font-size: 12px;");

        btnGuardar = crearBoton("Guardar", "#5b5bff");
        btnGuardar.setOnAction(e -> guardarEstudiante());

        btnEliminar = crearBoton("Eliminar", "#e74c3c");
        btnEliminar.setOnAction(e -> eliminarEstudiante());
        btnEliminar.setDisable(true);

        Button btnLimpiar = crearBoton("Limpiar", "#444466");
        btnLimpiar.setOnAction(e -> limpiarFormulario());

        Button btnRecargar = crearBoton("↻ Recargar Lista", "#2d6a4f");
        btnRecargar.setOnAction(e -> cargarEstudiantes());

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        VBox formulario = new VBox(12,
            lblTitulo, new Separator(),
            lblNombre,   txtNombre,
            lblApellido, txtApellido,
            lblEmail,    txtEmail,
            spacer,
            lblMensaje, new Separator(),
            btnGuardar, btnEliminar, btnLimpiar, btnRecargar
        );
        formulario.setStyle("-fx-background-color: #2d2d44; -fx-padding: 24;");
        formulario.setMinWidth(260);
        return formulario;
    }

    // ===================== Tabla =====================

    @SuppressWarnings("unchecked")
    private VBox construirTabla() {
        Label lblTitulo = new Label("Lista de Estudiantes");
        lblTitulo.setStyle("-fx-text-fill: #a0a0ff; -fx-font-size: 15px; -fx-font-weight: bold;");

        colId = new TableColumn<>("ID");
        colId.setPrefWidth(55);
        colId.setCellValueFactory(new PropertyValueFactory<>("idEstudiante"));

        colNombre = new TableColumn<>("Nombre");
        colNombre.setPrefWidth(180);
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));

        colApellido = new TableColumn<>("Apellido");
        colApellido.setPrefWidth(180);
        colApellido.setCellValueFactory(new PropertyValueFactory<>("apellido"));

        colEmail = new TableColumn<>("Email");
        colEmail.setPrefWidth(240);
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));

        tablaEstudiantes = new TableView<>();
        tablaEstudiantes.getColumns().addAll(colId, colNombre, colApellido, colEmail);
        tablaEstudiantes.setStyle("-fx-background-color: #2d2d44; -fx-table-cell-border-color: #444466;");
        tablaEstudiantes.setPlaceholder(new Label("No hay estudiantes registrados."));
        VBox.setVgrow(tablaEstudiantes, Priority.ALWAYS);

        tablaEstudiantes.getSelectionModel().selectedItemProperty().addListener(
            (obs, anterior, seleccionado) -> onSeleccionTabla(seleccionado)
        );

        Label lblAyuda = new Label("💡 Haz clic en un estudiante para editarlo o eliminarlo.");
        lblAyuda.setStyle("-fx-text-fill: #888899; -fx-font-size: 11px;");

        VBox contenedor = new VBox(10, lblTitulo, new Separator(), tablaEstudiantes, lblAyuda);
        contenedor.setStyle("-fx-padding: 20; -fx-background-color: #1e1e2e;");
        return contenedor;
    }

    // ===================== Lógica =====================

    private void cargarEstudiantes() {
        ObservableList<Estudiante> lista =
            FXCollections.observableArrayList(controller.listarEstudiantes());
        tablaEstudiantes.setItems(lista);
        mostrarMensaje("✅ " + lista.size() + " estudiante(s) cargado(s).", false);
    }

    private void guardarEstudiante() {
        String nombre   = txtNombre.getText().trim();
        String apellido = txtApellido.getText().trim();
        String email    = txtEmail.getText().trim();

        if (nombre.isEmpty() || apellido.isEmpty() || email.isEmpty()) {
            mostrarMensaje("⚠️ Todos los campos son obligatorios.", true);
            return;
        }

        if (estudianteSeleccionado == null) {
            boolean ok = controller.crearEstudiante(nombre, apellido, email);
            mostrarMensaje(ok ? "✅ Estudiante creado." : "❌ Error al crear.", !ok);
        } else {
            boolean ok = controller.actualizarEstudiante(
                estudianteSeleccionado.getIdEstudiante(), nombre, apellido, email);
            mostrarMensaje(ok ? "✅ Estudiante actualizado." : "❌ Error al actualizar.", !ok);
        }

        limpiarFormulario();
        cargarEstudiantes();
    }

    private void eliminarEstudiante() {
        if (estudianteSeleccionado == null) return;

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
            "¿Eliminar a " + estudianteSeleccionado.getNombreCompleto() + "?",
            ButtonType.YES, ButtonType.NO);
        confirm.setTitle("Confirmar eliminación");
        confirm.showAndWait().ifPresent(respuesta -> {
            if (respuesta == ButtonType.YES) {
                boolean ok = controller.eliminarEstudiante(
                    estudianteSeleccionado.getIdEstudiante());
                mostrarMensaje(ok ? "✅ Estudiante eliminado." : "❌ Error al eliminar.", !ok);
                limpiarFormulario();
                cargarEstudiantes();
            }
        });
    }

    private void onSeleccionTabla(Estudiante seleccionado) {
        if (seleccionado != null) {
            estudianteSeleccionado = seleccionado;
            txtNombre.setText(seleccionado.getNombre());
            txtApellido.setText(seleccionado.getApellido());
            txtEmail.setText(seleccionado.getEmail());
            btnGuardar.setText("Actualizar");
            btnEliminar.setDisable(false);
            mostrarMensaje("Editando: " + seleccionado.getNombreCompleto(), false);
        }
    }

    private void limpiarFormulario() {
        txtNombre.clear();
        txtApellido.clear();
        txtEmail.clear();
        estudianteSeleccionado = null;
        btnGuardar.setText("Guardar");
        btnEliminar.setDisable(true);
        tablaEstudiantes.getSelectionModel().clearSelection();
        if (lblMensaje != null) lblMensaje.setText("");
    }

    private void mostrarMensaje(String mensaje, boolean esError) {
        if (lblMensaje != null) {
            lblMensaje.setText(mensaje);
            lblMensaje.setStyle(esError
                ? "-fx-text-fill: #e74c3c; -fx-font-weight: bold;"
                : "-fx-text-fill: #27ae60; -fx-font-weight: bold;");
        }
    }

    // ===================== Estilos =====================

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
}
