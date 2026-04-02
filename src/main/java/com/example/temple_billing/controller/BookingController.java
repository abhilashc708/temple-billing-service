package com.example.temple_billing.controller;

import com.example.temple_billing.dto.*;
import com.example.temple_billing.entity.Receipt;
import com.example.temple_billing.security.CustomUserDetails;
import com.example.temple_billing.service.BookingService;
import com.example.temple_billing.service.ReceiptPdfService;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {

    private final BookingService bookingService;
    private final ReceiptPdfService receiptPdfService;

    public BookingController(BookingService bookingService, ReceiptPdfService receiptPdfService) {
        this.bookingService = bookingService;
        this.receiptPdfService = receiptPdfService;
    }

    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @PostMapping("/batch")
    public ReceiptResponseDTO createBatch(
            @RequestBody ReceiptCreateRequestDTO request,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        return bookingService.createBatch(request, userDetails);
    }


    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @PutMapping("/receipts/{id}")
    public ReceiptResponseDTO updateReceipt(
            @PathVariable Long id,
            @RequestBody ReceiptUpdateDTO dto,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        return bookingService.updateReceipt(id, dto, userDetails);
    }

    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @GetMapping("/receipts")
    public Page<ReceiptResponseDTO> getAllReceipts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "createdDate") String sortBy,
            @RequestParam(required = false) LocalDate startDate,
            @RequestParam(required = false) LocalDate endDate,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        return bookingService.getReceipts(
                page, size, sortBy, startDate, endDate, userDetails);
    }

    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @DeleteMapping("/receipts/{id}")
    public ResponseEntity<String> deleteReceipt(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        bookingService.deleteReceipt(id, userDetails);

        return ResponseEntity.ok("Receipt deleted successfully");
    }

    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @GetMapping("/receipts/search")
    public Page<ReceiptResponseDTO> search(
            @RequestParam(required = false) String receiptNumber,

            @RequestParam(required = false) LocalDate startDate,
            @RequestParam(required = false) LocalDate endDate,

            @RequestParam(required = false) String paymentStatus,
            @RequestParam(required = false) String paymentType,

            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {

        return bookingService.search(
                receiptNumber,
                startDate,
                endDate,
                paymentStatus,
                paymentType,
                page,
                size
        );
    }

    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @PostMapping("/receipts/report/search")
    public List<BookingReportDTO> searchBookings(
            @RequestBody BookingSearchRequest request) {

        return bookingService.searchBookings(request);
    }

    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @GetMapping("/receipts/{receiptId}/download")
    public ResponseEntity<byte[]> downloadReceipt(@PathVariable Long receiptId) {

        try {

            Receipt receipt = bookingService.getReceiptById(receiptId);

            byte[] pdfBytes = receiptPdfService.generateReceiptPdf(receipt);

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename=Receipt_" + receipt.getReceiptNumber() + ".pdf")
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(pdfBytes);

        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}