package expendiobebidas.controlador;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.Document;
import expendiobebidas.modelo.dao.ReportesDAO;
import expendiobebidas.modelo.pojo.ReporteVentaPorFecha;
import expendiobebidas.vista.ReporteFechas;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionListener;
import java.io.FileOutputStream;
import java.time.LocalDate;
import java.util.List;
import javax.swing.event.DocumentListener;
import javax.swing.event.UndoableEditListener;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Element;
import javax.swing.text.Position;
import javax.swing.text.Segment;

public class ReporteFechaController {
    private ReporteFechas vista;

    public ReporteFechaController(ReporteFechas vista) {
        this.vista = vista;
        agregarListeners();
    }

    private void agregarListeners() {
        vista.getBtnBuscar().addActionListener(e -> buscarVentas());
        vista.getBtnExportar().addActionListener(e -> exportarTablaPDF());
    }

    private void buscarVentas() {
        try {
            String fechaInicioStr = vista.getTfFechaInicio().getText();
            String fechaFinStr = vista.getTfFechaFin().getText();

            LocalDate fechaInicio = LocalDate.parse(fechaInicioStr);
            LocalDate fechaFin = LocalDate.parse(fechaFinStr);

            List<ReporteVentaPorFecha> ventas = ReportesDAO.obtenerVentasEntreFechas(fechaInicio, fechaFin);

            DefaultTableModel modelo = (DefaultTableModel) vista.getTablaVentas().getModel();
            modelo.setRowCount(0);

            for (ReporteVentaPorFecha venta : ventas) {
                modelo.addRow(new Object[]{
                    venta.getFolio(),
                    venta.getFecha(),
                    venta.getDescuento(),
                    venta.getCliente()
                });
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(vista, "Error al buscar ventas: " + e.getMessage());
        }
    }

    private void exportarTablaPDF() {
        try {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Guardar reporte como PDF");
            if (fileChooser.showSaveDialog(vista) == JFileChooser.APPROVE_OPTION) {
                String filePath = fileChooser.getSelectedFile().getAbsolutePath();
                if (!filePath.toLowerCase().endsWith(".pdf")) {
                    filePath += ".pdf";
                }

                com.itextpdf.text.Document documento = new com.itextpdf.text.Document();
                PdfWriter.getInstance(documento, new FileOutputStream(filePath));
                documento.open();

                documento.add(new Paragraph("Reporte de ventas por fechas", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18)));
                documento.add(new Paragraph("Desde: " + vista.getTfFechaInicio().getText() +
                        "  Hasta: " + vista.getTfFechaFin().getText()));
                documento.add(new Paragraph(" "));

                PdfPTable tabla = new PdfPTable(4);
                tabla.setWidths(new float[]{2, 2, 2, 3});

                String[] columnas = {"Folio", "Fecha", "Descuento", "Cliente"};
                for (String col : columnas) {
                    PdfPCell header = new PdfPCell(new Phrase(col));
                    header.setBackgroundColor(BaseColor.LIGHT_GRAY);
                    tabla.addCell(header);
                }

                DefaultTableModel modelo = (DefaultTableModel) vista.getTablaVentas().getModel();
                int filas = modelo.getRowCount();
                int columnasCount = modelo.getColumnCount();

                for (int i = 0; i < filas; i++) {
                    for (int j = 0; j < columnasCount; j++) {
                        Object valor = modelo.getValueAt(i, j);
                        tabla.addCell(valor != null ? valor.toString() : "");
                    }
                }

                documento.add(tabla);
                documento.close();

                JOptionPane.showMessageDialog(vista, "Reporte exportado exitosamente a:\n" + filePath);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(vista, "Error al exportar a PDF: " + e.getMessage());
        }
    }
}
