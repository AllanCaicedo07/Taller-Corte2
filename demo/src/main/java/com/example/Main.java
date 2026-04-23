package com.example;

import com.example.DB.ConexionDB;
import com.example.View.DocenteView;
import com.example.View.EstudianteView;
import com.example.View.GrupoView;
import com.example.View.InscripcionCursoView;
import com.example.View.MateriaView;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.stage.Stage;

/**
 * Punto de entrada principal de la aplicación.
 *
 * Extiende javafx.application.Application para poder lanzar
 * la interfaz gráfica con JavaFX.
 *
 * Al iniciar muestra un menú principal con acceso a cada módulo:
 *   - Materias
 *   - Docentes
 *   - Grupos
 *   - Estudiantes
 *   - Inscripciones
 *
 * Cada botón abre la vista correspondiente en una ventana nueva (Stage).
 * La conexión a la BD se verifica al arrancar; si falla, se muestra
 * el error y la aplicación continúa (las vistas mostrarán el error
 * individualmente cuando intenten consultar la BD).
 */
public class Main extends Application {

    // =========================================================
    // MÉTODO MAIN – Punto de entrada de la JVM
    // =========================================================

    /**
     * La JVM llama a este método primero.
     * Cuando JavaFX está en el classpath (vía Maven) se puede llamar
     * directamente a launch(); en Java 11+ también funciona sin que
     * la clase extienda Application en el main, pero extenderla es
     * la forma estándar y compatible con todos los entornos.
     */
    public static void main(String[] args) {
        launch(args);
    }

    // =========================================================
    // MÉTODO START – Arranca el hilo de la UI de JavaFX
    // =========================================================

    @Override
    public void start(Stage stagePrincipal) {

        // 1. Verificar conexión a la base de datos al arrancar
        verificarConexionDB();

        // 2. Construir y mostrar el menú principal
        stagePrincipal.setTitle("Sistema de Gestión Académica – Taller Corte 2");
        stagePrincipal.setScene(new Scene(construirMenuPrincipal(), 520, 580));
        stagePrincipal.setResizable(false);
        stagePrincipal.show();
    }

    // =========================================================
    // MENÚ PRINCIPAL
    // =========================================================

    /**
     * Construye el layout del menú principal.
     * Cada botón abre el módulo correspondiente en un Stage nuevo.
     */
    private VBox construirMenuPrincipal() {

        // ── Encabezado ──────────────────────────────────────────
        Label lblTitulo = new Label("Sistema de Gestión Académica");
        lblTitulo.setStyle(
            "-fx-text-fill: white;" +
            "-fx-font-size: 24px;" +
            "-fx-font-weight: bold;"
        );

        Label lblSubtitulo = new Label("Taller Corte 2  •  Selecciona un módulo");
        lblSubtitulo.setStyle(
            "-fx-text-fill: #a0a0c0;" +
            "-fx-font-size: 13px;"
        );

        VBox encabezado = new VBox(6, lblTitulo, lblSubtitulo);
        encabezado.setAlignment(Pos.CENTER);
        encabezado.setStyle("-fx-background-color: #2d2d44; -fx-padding: 28;");

        // ── Botones de módulos ───────────────────────────────────
        Button btnMaterias      = crearBotonModulo("  Gestionar Materias",      "#5b5bff");
        Button btnDocentes      = crearBotonModulo("  Gestionar Docentes",      "#7b5bff");
        Button btnGrupos        = crearBotonModulo("  Gestionar Grupos",         "#2d6a9f");
        Button btnEstudiantes   = crearBotonModulo("  Gestionar Estudiantes",    "#2d9a6f");
        Button btnInscripciones = crearBotonModulo(" Gestionar Inscripciones",  "#9a5b2d");

        // Acciones de cada botón ─────────────────────────────────

        btnMaterias.setOnAction(e -> {
            Stage stage = new Stage();
            new MateriaView().start(stage);
        });

        btnDocentes.setOnAction(e -> {
            Stage stage = new Stage();
            new DocenteView().start(stage);
        });

        btnGrupos.setOnAction(e -> {
            Stage stage = new Stage();
            new GrupoView().start(stage);
        });

        btnEstudiantes.setOnAction(e -> {
            Stage stage = new Stage();
            new EstudianteView().start(stage);
        });

        btnInscripciones.setOnAction(e -> {
            Stage stage = new Stage();
            new InscripcionCursoView().start(stage);
        });

        // ── Pie de página ────────────────────────────────────────
        Label lblPie = new Label("Conectado a PostgreSQL  •  config.properties");
        lblPie.setStyle("-fx-text-fill: #555577; -fx-font-size: 11px;");

        // ── Layout principal ─────────────────────────────────────
        VBox botones = new VBox(14,
            btnMaterias,
            btnDocentes,
            btnGrupos,
            btnEstudiantes,
            btnInscripciones
        );
        botones.setAlignment(Pos.CENTER);
        botones.setPadding(new Insets(30, 50, 20, 50));

        VBox root = new VBox(encabezado, botones, lblPie);
        root.setAlignment(Pos.TOP_CENTER);
        root.setSpacing(10);
        root.setStyle("-fx-background-color: #1e1e2e;");
        VBox.setMargin(lblPie, new Insets(0, 0, 16, 0));

        return root;
    }

    // =========================================================
    // HELPERS
    // =========================================================

    /**
     * Crea un botón con estilo uniforme para el menú principal
     * @return       Button configurado y estilizado.
     */
    private Button crearBotonModulo(String texto, String color) {
        Button btn = new Button(texto);
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setPrefHeight(52);
        btn.setStyle(
            "-fx-background-color: " + color + ";" +
            "-fx-text-fill: white;" +
            "-fx-font-size: 15px;" +
            "-fx-font-weight: bold;" +
            "-fx-background-radius: 10;" +
            "-fx-cursor: hand;"
        );

        // Efecto hover: oscurece ligeramente el botón
        btn.setOnMouseEntered(e -> btn.setStyle(
            "-fx-background-color: derive(" + color + ", -15%);" +
            "-fx-text-fill: white;" +
            "-fx-font-size: 15px;" +
            "-fx-font-weight: bold;" +
            "-fx-background-radius: 10;" +
            "-fx-cursor: hand;"
        ));
        btn.setOnMouseExited(e -> btn.setStyle(
            "-fx-background-color: " + color + ";" +
            "-fx-text-fill: white;" +
            "-fx-font-size: 15px;" +
            "-fx-font-weight: bold;" +
            "-fx-background-radius: 10;" +
            "-fx-cursor: hand;"
        ));

        return btn;
    }

    /**
     * Verifica la conexión a la base de datos al iniciar la aplicación.
     * Si falla, imprime el error en consola pero NO detiene la aplicación
     * (cada módulo manejará su propio error de conexión).
     */
    private void verificarConexionDB() {
        try {
            ConexionDB.getConexion();
            System.out.println("[Main] Conexión a la base de datos: OK ✓");
        } catch (Exception e) {
            System.err.println("[Main] ⚠ No se pudo conectar a la base de datos al iniciar:");
            System.err.println("       " + e.getMessage());
            System.err.println("[Main] Verifica config.properties y que el servidor esté activo.");
        }
    }

    // =========================================================
    // CIERRE DE LA APLICACIÓN
    // =========================================================

    /**
     * JavaFX llama a stop() cuando todas las ventanas se cierran.
     * Aquí cerramos la conexión a la BD de forma limpia.
     */
    @Override
    public void stop() {
        ConexionDB.cerrar();
        System.out.println("[Main] Aplicación cerrada correctamente.");
    }
}
