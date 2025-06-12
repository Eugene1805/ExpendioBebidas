package expendiobebidas.utils;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import java.awt.Component;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.io.FileOutputStream;

/**
 *
 * @author meler
 */
public class PDFExporter {

    public static void exportarTabla(JTable tabla, String tituloReporte, String[] encabezadosExtras, String nombreArchivoPDF, Component parentComponent) {
        try {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Guardar reporte como PDF");
            if (fileChooser.showSaveDialog(parentComponent) == JFileChooser.APPROVE_OPTION) {
                String filePath = fileChooser.getSelectedFile().getAbsolutePath();
                if (!filePath.toLowerCase().endsWith(".pdf")) {
                    filePath += ".pdf";
                }

                Document documento = new Document();
                PdfWriter.getInstance(documento, new FileOutputStream(filePath));
                documento.open();

                // Título del reporte
                documento.add(new Paragraph(tituloReporte, FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18)));
                documento.add(new Paragraph(" "));

                // Encabezados extra (como rango de fechas u otros)
                if (encabezadosExtras != null) {
                    for (String linea : encabezadosExtras) {
                        documento.add(new Paragraph(linea));
                    }
                    documento.add(new Paragraph(" "));
                }

                // Construcción de la tabla PDF
                DefaultTableModel modelo = (DefaultTableModel) tabla.getModel();
                int numColumnas = modelo.getColumnCount();
                PdfPTable tablaPDF = new PdfPTable(numColumnas);

                for (int i = 0; i < numColumnas; i++) {
                    PdfPCell celda = new PdfPCell(new Phrase(modelo.getColumnName(i)));
                    celda.setBackgroundColor(BaseColor.LIGHT_GRAY);
                    tablaPDF.addCell(celda);
                }

                int numFilas = modelo.getRowCount();
                for (int fila = 0; fila < numFilas; fila++) {
                    for (int col = 0; col < numColumnas; col++) {
                        Object valor = modelo.getValueAt(fila, col);
                        tablaPDF.addCell(valor != null ? valor.toString() : "");
                    }
                }

                documento.add(tablaPDF);
                documento.close();

                JOptionPane.showMessageDialog(parentComponent, "Reporte exportado exitosamente:\n" + filePath);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(parentComponent, "Error al exportar a PDF:\n" + e.getMessage());
        }
    }
}
