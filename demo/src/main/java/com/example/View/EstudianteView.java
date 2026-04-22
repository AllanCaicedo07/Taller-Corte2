package com.example.View;

import com.example.controller.EstudianteController;
import com.example.Model.Estudiante;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

public class EstudianteView extends JFrame {

    // ── Colores ──
    private static final Color BG_DARK       = new Color(15, 23, 42);
    private static final Color BG_CARD       = new Color(30, 41, 59);
    private static final Color BG_INPUT      = new Color(51, 65, 85);
    private static final Color ACCENT       = new Color(56, 189, 248);
    private static final Color DANGER        = new Color(239, 68, 68);
    private static final Color SUCCESS       = new Color(34, 197, 94);
    private static final Color TEXT_PRIMARY  = new Color(241, 245, 249);
    private static final Color TEXT_MUTED    = new Color(148, 163, 184);
    private static final Color BORDER_COLOR  = new Color(71, 85, 105);

    // ── Componentes ──
    private JTextField txtId, txtNombre, txtApellido, txtEmail, txtBuscar;
    private JTable tabla;
    private DefaultTableModel modeloTabla;
    private JLabel lblStatus;

    private EstudianteController controller = new EstudianteController();

    public EstudianteView() {
        setTitle("Sistema Escolar · Gestión de Estudiantes");
        setSize(950, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        // Panel raíz
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(BG_DARK);
        root.setBorder(new EmptyBorder(20, 24, 20, 24));

        root.add(crearHeader(),       BorderLayout.NORTH);
        root.add(crearPanelCentral(), BorderLayout.CENTER);
        root.add(crearStatusBar(),    BorderLayout.SOUTH);

        setContentPane(root);
        cargarTabla();
    }

    // ─────────────── HEADER ───────────────
    private JPanel crearHeader() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BG_DARK);
        panel.setBorder(new EmptyBorder(0, 0, 18, 0));

        JLabel titulo = new JLabel("Estudiantes");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 26));
        titulo.setForeground(TEXT_PRIMARY);

        JLabel subtitulo = new JLabel("Gestión del módulo de estudiantes");
        subtitulo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        subtitulo.setForeground(TEXT_MUTED);

        JPanel textos = new JPanel();
        textos.setLayout(new BoxLayout(textos, BoxLayout.Y_AXIS));
        textos.setBackground(BG_DARK);
        textos.add(titulo);
        textos.add(Box.createVerticalStrut(2));
        textos.add(subtitulo);

        // Buscador
        JPanel buscarPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        buscarPanel.setBackground(BG_DARK);
        txtBuscar = crearInput("Buscar por nombre...", 180);
        JButton btnBuscar = crearBoton("🔍 Buscar", ACCENT, BG_DARK);
        btnBuscar.addActionListener(e -> buscarEstudiante());
        JButton btnRefresh = crearBoton("↺ Todos", BG_INPUT, TEXT_PRIMARY);
        btnRefresh.addActionListener(e -> cargarTabla());
        buscarPanel.add(txtBuscar);
        buscarPanel.add(btnBuscar);
        buscarPanel.add(btnRefresh);

        panel.add(textos,     BorderLayout.WEST);
        panel.add(buscarPanel, BorderLayout.EAST);
        return panel;
    }

    // ─────────────── PANEL CENTRAL ───────────────
    private JPanel crearPanelCentral() {
        JPanel panel = new JPanel(new BorderLayout(16, 0));
        panel.setBackground(BG_DARK);

        panel.add(crearFormulario(), BorderLayout.WEST);
        panel.add(crearTabla(),      BorderLayout.CENTER);

        return panel;
    }

    // ─────────────── FORMULARIO ───────────────
    private JPanel crearFormulario() {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(BG_CARD);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR, 1),
            new EmptyBorder(20, 20, 20, 20)
        ));
        card.setPreferredSize(new Dimension(270, 0));

        JLabel lblForm = new JLabel("Datos del Estudiante");
        lblForm.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblForm.setForeground(ACCENT);
        lblForm.setAlignmentX(Component.LEFT_ALIGNMENT);

        JSeparator sep = new JSeparator();
        sep.setForeground(BORDER_COLOR);
        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));

        txtId       = crearInput("ID (auto)", 0);
        txtId.setEditable(false);
        txtId.setForeground(TEXT_MUTED);

        txtNombre   = crearInput("Ingrese nombre", 0);
        txtApellido = crearInput("Ingrese apellido", 0);
        txtEmail    = crearInput("correo@ejemplo.com", 0);

        card.add(lblForm);
        card.add(Box.createVerticalStrut(10));
        card.add(sep);
        card.add(Box.createVerticalStrut(16));
        card.add(crearCampo("ID",       txtId));
        card.add(Box.createVerticalStrut(10));
        card.add(crearCampo("Nombre",   txtNombre));
        card.add(Box.createVerticalStrut(10));
        card.add(crearCampo("Apellido", txtApellido));
        card.add(Box.createVerticalStrut(10));
        card.add(crearCampo("Email",    txtEmail));
        card.add(Box.createVerticalStrut(20));
        card.add(crearBotones());
        card.add(Box.createVerticalGlue());

        return card;
    }

    private JPanel crearCampo(String label, JTextField campo) {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBackground(BG_CARD);
        p.setAlignmentX(Component.LEFT_ALIGNMENT);
        p.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));

        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lbl.setForeground(TEXT_MUTED);
        campo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        campo.setAlignmentX(Component.LEFT_ALIGNMENT);

        p.add(lbl);
        p.add(Box.createVerticalStrut(4));
        p.add(campo);
        return p;
    }

    private JPanel crearBotones() {
        JPanel p = new JPanel(new GridLayout(2, 2, 8, 8));
        p.setBackground(BG_CARD);
        p.setAlignmentX(Component.LEFT_ALIGNMENT);
        p.setMaximumSize(new Dimension(Integer.MAX_VALUE, 90));

        JButton btnGuardar   = crearBoton("💾 Guardar",   SUCCESS,     BG_DARK);
        JButton btnActualizar = crearBoton("✏️ Actualizar", ACCENT,    BG_DARK);
        JButton btnEliminar  = crearBoton("🗑 Eliminar",   DANGER,     BG_DARK);
        JButton btnLimpiar   = crearBoton("✕ Limpiar",    BG_INPUT,   TEXT_PRIMARY);

        btnGuardar.addActionListener(e   -> guardarEstudiante());
        btnActualizar.addActionListener(e -> actualizarEstudiante());
        btnEliminar.addActionListener(e  -> eliminarEstudiante());
        btnLimpiar.addActionListener(e   -> limpiarFormulario());

        p.add(btnGuardar);
        p.add(btnActualizar);
        p.add(btnEliminar);
        p.add(btnLimpiar);
        return p;
    }

    // ─────────────── TABLA ───────────────
    private JPanel crearTabla() {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(BG_CARD);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR, 1),
            new EmptyBorder(16, 16, 16, 16)
        ));

        String[] columnas = {"ID", "Nombre", "Apellido", "Email"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        tabla = new JTable(modeloTabla);
        tabla.setBackground(BG_CARD);
        tabla.setForeground(TEXT_PRIMARY);
        tabla.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tabla.setRowHeight(38);
        tabla.setShowGrid(false);
        tabla.setIntercellSpacing(new Dimension(0, 0));
        tabla.setSelectionBackground(new Color(56, 189, 248, 40));
        tabla.setSelectionForeground(TEXT_PRIMARY);
        tabla.setFocusable(false);

        // Header
        JTableHeader header = tabla.getTableHeader();
        header.setBackground(BG_INPUT);
        header.setForeground(ACCENT);
        header.setFont(new Font("Segoe UI", Font.BOLD, 12));
        header.setBorder(BorderFactory.createEmptyBorder());
        header.setReorderingAllowed(false);

        // Alineación centrada para ID
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        centerRenderer.setBackground(BG_CARD);
        centerRenderer.setForeground(TEXT_MUTED);
        tabla.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
        tabla.getColumnModel().getColumn(0).setPreferredWidth(50);
        tabla.getColumnModel().getColumn(0).setMaxWidth(60);

        // Renderer alternado para filas
        tabla.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object v,
                    boolean sel, boolean foc, int row, int col) {
                super.getTableCellRendererComponent(t, v, sel, foc, row, col);
                setBackground(sel ? new Color(56, 189, 248, 30)
                                  : (row % 2 == 0 ? BG_CARD : new Color(40, 55, 75)));
                setForeground(col == 0 ? TEXT_MUTED : TEXT_PRIMARY);
                setFont(new Font("Segoe UI", Font.PLAIN, 13));
                setBorder(new EmptyBorder(0, 10, 0, 10));
                setHorizontalAlignment(col == 0 ? CENTER : LEFT);
                return this;
            }
        });

        // Click en fila → llena formulario
        tabla.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int fila = tabla.getSelectedRow();
                if (fila >= 0) {
                    txtId.setText(modeloTabla.getValueAt(fila, 0).toString());
                    txtNombre.setText(modeloTabla.getValueAt(fila, 1).toString());
                    txtApellido.setText(modeloTabla.getValueAt(fila, 2).toString());
                    txtEmail.setText(modeloTabla.getValueAt(fila, 3).toString());
                }
            }
        });

        JScrollPane scroll = new JScrollPane(tabla);
        scroll.setBackground(BG_CARD);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getViewport().setBackground(BG_CARD);

        card.add(scroll, BorderLayout.CENTER);
        return card;
    }

    // ─────────────── STATUS BAR ───────────────
    private JPanel crearStatusBar() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.setBackground(BG_DARK);
        panel.setBorder(new EmptyBorder(10, 0, 0, 0));

        lblStatus = new JLabel("✔ Sistema listo");
        lblStatus.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblStatus.setForeground(TEXT_MUTED);
        panel.add(lblStatus);
        return panel;
    }

    // ─────────────── HELPERS UI ───────────────
    private JTextField crearInput(String placeholder, int cols) {
        JTextField field = cols > 0 ? new JTextField(cols) : new JTextField();
        field.setBackground(BG_INPUT);
        field.setForeground(TEXT_PRIMARY);
        field.setCaretColor(ACCENT);
        field.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR, 1),
            new EmptyBorder(6, 10, 6, 10)
        ));
        field.putClientProperty("placeholder", placeholder);
        return field;
    }

    private JButton crearBoton(String texto, Color bg, Color fg) {
        JButton btn = new JButton(texto);
        btn.setBackground(bg);
        btn.setForeground(fg.equals(BG_DARK) ? Color.WHITE : fg);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setBorder(new EmptyBorder(8, 14, 8, 14));
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setOpaque(true);
        btn.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) {
                btn.setBackground(bg.brighter());
            }
            @Override public void mouseExited(MouseEvent e) {
                btn.setBackground(bg);
            }
        });
        return btn;
    }

    // ─────────────── ACCIONES ───────────────
    private void cargarTabla() {
        modeloTabla.setRowCount(0);
        List<Estudiante> lista = controller.listarEstudiantes();
        for (Estudiante e : lista) {
            modeloTabla.addRow(new Object[]{
                e.getIdEstudiante(), e.getNombre(), e.getApellido(), e.getEmail()
            });
        }
        setStatus("✔ " + lista.size() + " estudiante(s) cargados.", TEXT_MUTED);
    }

    private void guardarEstudiante() {
        String nombre   = txtNombre.getText().trim();
        String apellido = txtApellido.getText().trim();
        String email    = txtEmail.getText().trim();

        if (nombre.isEmpty() || apellido.isEmpty() || email.isEmpty()) {
            setStatus("⚠ Completa todos los campos.", DANGER);
            return;
        }
        boolean ok = controller.crearEstudiante(nombre, apellido, email);
        if (ok) {
            setStatus("✔ Estudiante guardado correctamente.", SUCCESS);
            limpiarFormulario();
            cargarTabla();
        } else {
            setStatus("✖ No se pudo guardar el estudiante.", DANGER);
        }
    }

    private void actualizarEstudiante() {
        if (txtId.getText().isEmpty()) {
            setStatus("⚠ Selecciona un estudiante de la tabla.", DANGER);
            return;
        }
        int id = Integer.parseInt(txtId.getText());
        boolean ok = controller.actualizarEstudiante(
            id,
            txtNombre.getText().trim(),
            txtApellido.getText().trim(),
            txtEmail.getText().trim()
        );
        if (ok) {
            setStatus("✔ Estudiante actualizado.", SUCCESS);
            limpiarFormulario();
            cargarTabla();
        } else {
            setStatus("✖ No se pudo actualizar.", DANGER);
        }
    }

    private void eliminarEstudiante() {
        if (txtId.getText().isEmpty()) {
            setStatus("⚠ Selecciona un estudiante de la tabla.", DANGER);
            return;
        }
        int opcion = JOptionPane.showConfirmDialog(this,
            "¿Eliminar el estudiante con ID " + txtId.getText() + "?",
            "Confirmar eliminación",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE
        );
        if (opcion == JOptionPane.YES_OPTION) {
            int id = Integer.parseInt(txtId.getText());
            boolean ok = controller.eliminarEstudiante(id);
            if (ok) {
                setStatus("✔ Estudiante eliminado.", SUCCESS);
                limpiarFormulario();
                cargarTabla();
            } else {
                setStatus("✖ No se pudo eliminar.", DANGER);
            }
        }
    }

    private void buscarEstudiante() {
        String texto = txtBuscar.getText().trim().toLowerCase();
        modeloTabla.setRowCount(0);
        List<Estudiante> lista = controller.listarEstudiantes();
        for (Estudiante e : lista) {
            if (e.getNombreCompleto().toLowerCase().contains(texto)
                    || e.getEmail().toLowerCase().contains(texto)) {
                modeloTabla.addRow(new Object[]{
                    e.getIdEstudiante(), e.getNombre(), e.getApellido(), e.getEmail()
                });
            }
        }
        setStatus("🔍 " + modeloTabla.getRowCount() + " resultado(s) encontrados.", ACCENT);
    }

    private void limpiarFormulario() {
        txtId.setText("");
        txtNombre.setText("");
        txtApellido.setText("");
        txtEmail.setText("");
        tabla.clearSelection();
    }

    private void setStatus(String mensaje, Color color) {
        lblStatus.setText(mensaje);
        lblStatus.setForeground(color);
    }

    // ─────────────── MAIN ───────────────
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}

        SwingUtilities.invokeLater(() -> {
            new EstudianteView().setVisible(true);
        });
    }
}
