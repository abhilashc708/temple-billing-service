package com.example.temple_billing.service;

import com.example.temple_billing.entity.Booking;
import com.example.temple_billing.entity.Receipt;
import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;

@Service
public class ReceiptPdfService {

    public byte[] generateReceiptPdf(Receipt receipt) throws Exception {

        Document document = new Document(PageSize.A4);
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        PdfWriter.getInstance(document, out);
        document.open();

        Font titleFont = new Font(Font.HELVETICA, 16, Font.BOLD);
        Font normalFont = new Font(Font.HELVETICA, 12);

        // Temple Title
        Paragraph title = new Paragraph("Mankurussi Bhagavathi Temple", titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        document.add(title);

        document.add(new Paragraph("Phone: 9072017303", normalFont));
        document.add(new Paragraph(" "));

        document.add(new Paragraph(
                "Receipt No: " + receipt.getReceiptNumber() +
                        " / " + receipt.getPaymentType()
        ));

        document.add(new Paragraph("Date: " + receipt.getCreatedDate()));
        document.add(new Paragraph(" "));

        // Table
        PdfPTable table = new PdfPTable(6);
        table.setWidthPercentage(100);

        table.addCell("Name");
        table.addCell("Vazhipadu");
        table.addCell("Date");
        table.addCell("Qty");
        table.addCell("Amount");
        table.addCell("Total");

        for (Booking b : receipt.getBookings()) {

            table.addCell(b.getDevoteeName() + "\n" + b.getBirthStar());
            table.addCell(b.getVazhipadu());
            table.addCell(b.getBookingDate().toString());
            table.addCell(String.valueOf(b.getQuantity()));
            table.addCell("Rs. " + b.getAmount());
            table.addCell("Rs. " + (b.getAmount() * b.getQuantity()));
        }

        document.add(table);

        document.add(new Paragraph(" "));
        document.add(new Paragraph("Grand Total: Rs. " + receipt.getTotalAmount(), titleFont));

        document.close();

        return out.toByteArray();
    }
}
