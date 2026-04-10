package com.example.temple_billing.controller;

import com.example.temple_billing.dto.*;
import com.example.temple_billing.entity.Receipt;
import com.example.temple_billing.security.CustomUserDetails;
import com.example.temple_billing.service.BookingService;
import com.example.temple_billing.service.ReceiptPdfService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static final Logger logger = LoggerFactory.getLogger(BookingController.class);

    private final BookingService bookingService;
    private final ReceiptPdfService receiptPdfService;

    public BookingController(BookingService bookingService, ReceiptPdfService receiptPdfService) {
        this.bookingService = bookingService;
        this.receiptPdfService = receiptPdfService;
    }

    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @PostMapping("/batch")
    public ReceiptResponseDTO createBatch(
            @RequestBody @Valid ReceiptCreateRequestDTO request,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        logger.info("Creating batch receipt for user: {}", userDetails.getUsername());
        ReceiptResponseDTO response = bookingService.createBatch(request, userDetails);
        logger.info("Batch receipt created successfully with ID: {}", response.getId());
        return response;
    }

    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @PutMapping("/receipts/{id}")
    public ReceiptResponseDTO updateReceipt(
            @PathVariable Long id,
            @RequestBody @Valid ReceiptUpdateDTO dto,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        logger.info("Updating receipt ID: {} by user: {}", id, userDetails.getUsername());
        ReceiptResponseDTO response = bookingService.updateReceipt(id, dto, userDetails);
        logger.info("Receipt ID: {} updated successfully", id);
        return response;
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
        logger.debug("Fetching receipts for user: {} - Page: {}, Size: {}", userDetails.getUsername(), page, size);
        return bookingService.getReceipts(
                page, size, sortBy, startDate, endDate, userDetails);
    }

    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @DeleteMapping("/receipts/{id}")
    public ResponseEntity<String> deleteReceipt(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        logger.info("Deleting receipt ID: {} by user: {}", id, userDetails.getUsername());
        bookingService.deleteReceipt(id, userDetails);
        logger.info("Receipt ID: {} deleted successfully", id);
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
        logger.debug("Searching receipts - Number: {}, Status: {}, Type: {}", receiptNumber, paymentStatus, paymentType);
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
        logger.debug("Generating booking report");
        return bookingService.searchBookings(request);
    }

    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @GetMapping("/receipts/{receiptId}/download")
    public ResponseEntity<byte[]> downloadReceipt(@PathVariable Long receiptId) {
        logger.info("Downloading receipt PDF - ID: {}", receiptId);

        try {
            Receipt receipt = bookingService.getReceiptById(receiptId);
            byte[] pdfBytes = receiptPdfService.generateReceiptPdf(receipt);
            logger.info("Receipt PDF generated successfully for ID: {}", receiptId);

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename=Receipt_" + receipt.getReceiptNumber() + ".pdf")
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(pdfBytes);

        } catch (Exception e) {
            logger.error("Error generating PDF for receipt ID: {}", receiptId, e);
            return ResponseEntity.internalServerError().build();
        }
    }
}