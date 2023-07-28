package kaznarah.reservation_chambre_hotel.utils;

import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class ExportDoc {
    public static String toCapitalize(String str){
        if(str == null || str.isEmpty()) return str;
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }

    public static String getFilePath(String fileTo, String fileExt){
        Stage stage = new Stage();
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("EXPORTATION FICHIER");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter(fileTo, fileExt));

        File file = fileChooser.showSaveDialog(stage);
        if(file != null){
            return file.getAbsolutePath();
        }
        return null;
    }

    public static<T> void exportToPDF(TableView<T> tableView, String filePath) {

        try {
            PDDocument document = new PDDocument();
            PDPage page = new PDPage();
            document.addPage(page);

            PDPageContentStream contentStream = new PDPageContentStream(document, page);
            PDFont font = PDType1Font.HELVETICA_BOLD;
            int fontSize = 12;
            float leading = 1.5f * fontSize;

            PDRectangle pageSize = page.getMediaBox();
            float margin = 50;
            float startX = margin;
            float startY = pageSize.getHeight() - margin;
            float width = pageSize.getWidth() - 2 * margin;

            contentStream.setFont(font, fontSize);

            // Create header row
            float textX = startX;
            float textY = startY;
            for (TableColumn<T, ?> column : tableView.getColumns()) {
                String columnHeader = column.getText();
                contentStream.beginText();
                contentStream.newLineAtOffset(textX, textY);
                contentStream.showText(columnHeader);
                contentStream.endText();

                textX += width / tableView.getColumns().size();
            }

            // Create data rows
            for (T item : tableView.getItems()) {
                textY -= leading;
                textX = startX;

                for (TableColumn<T, ?> column : tableView.getColumns()) {
                    Object cellValue = column.getCellObservableValue(item).getValue();
                    String cellText = cellValue != null ? cellValue.toString() : "";

                    contentStream.beginText();
                    contentStream.newLineAtOffset(textX, textY);
                    contentStream.showText(cellText);
                    contentStream.endText();

                    textX += width / tableView.getColumns().size();
                }
            }

            contentStream.close();
            document.save(filePath);
            document.close();

            System.out.println("PDF file exported successfully.");
            MessageBox.showInfo(Alert.AlertType.INFORMATION, "Fichier PDF exporté avec succès","Export PDF");
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public static<T> void exportToExcel(TableView<T> tableView, String filePath) {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Sheet1");

            ObservableList<TableColumn<T, ?>> columns = tableView.getColumns();
            ObservableList<T> items = tableView.getItems();

            // Create header row
            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < columns.size(); i++) {
                TableColumn<T, ?> column = columns.get(i);
                headerRow.createCell(i).setCellValue(column.getText());
            }

            // Populate data rows
            for (int row = 0; row < items.size(); row++) {
                Row dataRow = sheet.createRow(row + 1);
                T item = items.get(row);
                for (int col = 0; col < columns.size(); col++) {
                    TableColumn<T, ?> column = columns.get(col);
                    // Adjust the following line based on your model class and data extraction
                    Object cellValue = column.getCellObservableValue(item).getValue();
                    dataRow.createCell(col).setCellValue(cellValue != null ? cellValue.toString() : "");
                }
            }

            // Auto-size columns
            for (int i = 0; i < columns.size(); i++) {
                sheet.autoSizeColumn(i);
            }

            // Save workbook to file
            try (FileOutputStream fileOut = new FileOutputStream(filePath)) {
                workbook.write(fileOut);
                System.out.println("Excel file exported successfully.");
                MessageBox.showInfo(Alert.AlertType.INFORMATION, "Fichier Excel exporté avec succès", "Export EXCEL");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
