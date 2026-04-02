package com.example.temple_billing.service;

import com.example.temple_billing.dto.*;
import com.example.temple_billing.entity.Booking;
import com.example.temple_billing.entity.PaymentStatus;
import com.example.temple_billing.entity.Receipt;
import com.example.temple_billing.entity.User;
import com.example.temple_billing.repository.BookingRepository;
import com.example.temple_billing.repository.ReceiptRepository;
import com.example.temple_billing.security.CustomUserDetails;
import com.example.temple_billing.utility.BookingSpecification;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class BookingService {

    private final BookingRepository bookingRepository;
    private final ReceiptRepository receiptRepository;

    public BookingService(BookingRepository bookingRepository, ReceiptRepository receiptRepository) {
        this.bookingRepository = bookingRepository;
        this.receiptRepository = receiptRepository;
    }

    // =============================
    // CREATE BOOKING
    // =============================
    @Transactional
    public ReceiptResponseDTO createBatch(
            ReceiptCreateRequestDTO request,
            CustomUserDetails userDetails) {

        // 🔥 Calculate total
        double totalAmount = request.getBookings()
                .stream()
                .mapToDouble(c -> c.getAmount() * c.getQuantity())
                .sum();

        // 🔥 Create Receipt first
        Receipt receipt = Receipt.builder()
                .receiptNumber(generateReceiptNumber())
                .createdDate(LocalDateTime.now())
                .paymentType(request.getPaymentType())
                .paymentStatus(PaymentStatus.valueOf(request.getPaymentStatus()))
                .phoneNumber(request.getPhoneNumber())
                .totalAmount(totalAmount)
                .user(User.builder().id(userDetails.getUserId()).build())
                .build();

        receiptRepository.save(receipt);

        // 🔥 Save bookings
        List<Booking> bookingList = new ArrayList<>();

        for (BookingRequestDTO dto : request.getBookings()) {
            Booking booking = Booking.builder()
                    .bookingDate(dto.getBookingDate())
                    .vazhipadu(dto.getVazhipadu())
                    .quantity(dto.getQuantity())
                    .amount(dto.getAmount())
                    .devoteeName(dto.getDevoteeName())
                    .birthStar(dto.getBirthStar())
                    .createdDate(LocalDateTime.now())
                    .receipt(receipt)
                    .build();

            bookingList.add(booking);
        }

        bookingRepository.saveAll(bookingList);

        receipt.setBookings(bookingList);

        return mapReceiptToDTO(receipt);
    }

    // =============================
    // UPDATE BOOKING
    // =============================
    @Transactional
    public ReceiptResponseDTO updateReceipt(
            Long id,
            ReceiptUpdateDTO dto,
            CustomUserDetails userDetails) {

        Receipt receipt = receiptRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Receipt not found"));

        validateReceiptOwnership(receipt, userDetails);

        receipt.setPaymentType(dto.getPaymentType());
        receipt.setPaymentStatus(PaymentStatus.valueOf(dto.getPaymentStatus()));
        receipt.setPhoneNumber(dto.getPhoneNumber());

        receiptRepository.save(receipt);

        return mapReceiptToDTO(receipt);
    }

    // =============================
    // GET BOOKINGS (Pagination + Sorting + Date Filter)
    // =============================

    public Page<ReceiptResponseDTO> getReceipts(
            int page,
            int size,
            String sortBy,
            LocalDate startDate,
            LocalDate endDate,
            CustomUserDetails userDetails) {

        Pageable pageable =
                PageRequest.of(page, size, Sort.by(sortBy).descending());

        Page<Receipt> result;

        boolean isAdmin = userDetails.getRole().equals("ADMIN");

        if (startDate != null && endDate != null) {

            if (isAdmin) {
                result = receiptRepository
                        .findByCreatedDateBetween(
                                startDate.atStartOfDay(),
                                endDate.atTime(23, 59, 59),
                                pageable);
            } else {
                result = receiptRepository
                        .findByUser_IdAndCreatedDateBetween(
                                userDetails.getUserId(),
                                startDate.atStartOfDay(),
                                endDate.atTime(23, 59, 59),
                                pageable);
            }

        } else {

            if (isAdmin) {
                result = receiptRepository.findAll(pageable);
            } else {
                result = receiptRepository
                        .findByUser_Id(userDetails.getUserId(), pageable);
            }
        }

        return result.map(this::mapReceiptToDTO);
    }

    // =============================
    // DELETE BOOKING
    // =============================

    @Transactional
    public void deleteReceipt(
            Long id,
            CustomUserDetails userDetails) {

        Receipt receipt = receiptRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Receipt not found"));

        validateReceiptOwnership(receipt, userDetails);

        if (receipt.getPaymentStatus() == PaymentStatus.SUCCESS) {
            throw new RuntimeException("Cannot delete successful receipt");
        }

        receiptRepository.delete(receipt);
    }

    public Page<ReceiptResponseDTO> search(
            String receiptNumber,
            LocalDate createdFrom,
            LocalDate createdTo,
            String paymentStatus,
            String paymentType,
            int page,
            int size
    ) {

        Pageable pageable = PageRequest.of(
                page, size,
                Sort.by("createdDate").descending());

        // Convert LocalDate → LocalDateTime
        LocalDateTime createdFromDT =
                createdFrom != null ? createdFrom.atStartOfDay() : null;

        LocalDateTime createdToDT =
                createdTo != null ? createdTo.atTime(23, 59, 59) : null;

        var spec = BookingSpecification.search(
                receiptNumber,
                createdFromDT,
                createdToDT,
                paymentStatus,
                paymentType);

        Page<Receipt> recieptsList =
                receiptRepository.findAll(spec, pageable);

        return recieptsList.map(this::mapReceiptToDTO);
    }

    // =============================
    // ENTITY → DTO
    // =============================
    private BookingResponseDTO mapToDTO(Booking booking) {

        return BookingResponseDTO.builder()
                .id(booking.getId())
                .bookingDate(booking.getBookingDate())
                .vazhipadu(booking.getVazhipadu())
                .quantity(booking.getQuantity())
                .amount(booking.getAmount())
                .devoteeName(booking.getDevoteeName())
                .birthStar(booking.getBirthStar())
                .createdDate(booking.getCreatedDate())
                .build();
    }

    private void validateReceiptOwnership(
            Receipt receipt,
            CustomUserDetails userDetails) {

        boolean isAdmin = userDetails.getRole().equals("ADMIN");

        if (!isAdmin &&
                !receipt.getUser().getId()
                        .equals(userDetails.getUserId())) {

            throw new RuntimeException("Unauthorized access");
        }
    }

private String generateReceiptNumber() {

    String todayDate = LocalDate.now()
            .format(DateTimeFormatter.ofPattern("yyyyMMdd"));

    LocalDate today = LocalDate.now();

    Optional<Receipt> lastReceiptOpt =
            receiptRepository.findTopByCreatedDateBetweenOrderByIdDesc(
                    today.atStartOfDay(),
                    today.atTime(23, 59, 59)
            );

    int nextNumber = 1;

    if (lastReceiptOpt.isPresent()) {
        String lastReceiptNumber = lastReceiptOpt.get().getReceiptNumber();

        String lastCounter =
                lastReceiptNumber.substring(lastReceiptNumber.length() - 3);

        nextNumber = Integer.parseInt(lastCounter) + 1;
    }

    return "MKT" + todayDate + String.format("%03d", nextNumber);
}

    private PaymentStatus determineReceiptStatus(List<BookingRequestDTO> dtoList) {

        boolean allSuccess = dtoList.stream()
                .allMatch(dto -> dto.getPaymentStatus().equals("SUCCESS"));

        boolean allFailed = dtoList.stream()
                .allMatch(dto -> dto.getPaymentStatus().equals("FAILED"));

        if (allSuccess) return PaymentStatus.SUCCESS;
        if (allFailed) return PaymentStatus.FAILED;

        return PaymentStatus.PARTIAL;
    }

    private ReceiptResponseDTO mapReceiptToDTO(Receipt receipt) {

        List<BookingResponseDTO> bookingDTOs = receipt.getBookings()
                .stream()
                .map(this::mapBookingToDTO)
                .toList();

        return ReceiptResponseDTO.builder()
                .id(receipt.getId())
                .receiptNumber(receipt.getReceiptNumber())
                .createdDate(receipt.getCreatedDate())
                .totalAmount(receipt.getTotalAmount())
                .paymentStatus(receipt.getPaymentStatus().name())
                .paymentType(receipt.getPaymentType())
                .phoneNumber(receipt.getPhoneNumber())
                .createdBy(String.valueOf(receipt.getUser().getId()))
                .bookings(bookingDTOs)
                .build();
    }

    private BookingResponseDTO mapBookingToDTO(Booking booking) {

        return BookingResponseDTO.builder()
                .id(booking.getId())
                .bookingDate(booking.getBookingDate())
                .vazhipadu(booking.getVazhipadu())
                .quantity(booking.getQuantity())
                .amount(booking.getAmount())
                .devoteeName(booking.getDevoteeName())
                .birthStar(booking.getBirthStar())
                .createdDate(booking.getCreatedDate())
                .build();
    }


    public Receipt getReceiptById(Long receiptId) {
        return receiptRepository.findById(receiptId)
                .orElseThrow(() -> new RuntimeException("Receipt not found"));
    }


    public List<BookingReportDTO> searchBookings(BookingSearchRequest request) {

        Specification<Receipt> spec =
                BookingSpecification.search(request);

        List<Receipt> receipts =
                receiptRepository.findAll(spec, Sort.by("createdDate").descending());

        List<BookingReportDTO> report = new ArrayList<>();

        for (Receipt receipt : receipts) {

            for (Booking booking : receipt.getBookings()) {

                // apply booking-level filter
                if (request.getBirthStar() != null &&
                        !booking.getBirthStar().equalsIgnoreCase(request.getBirthStar())) {
                    continue;
                }

                report.add(new BookingReportDTO(
                        receipt.getReceiptNumber(),
                        receipt.getCreatedDate(),
                        receipt.getPaymentStatus().name(),
                        receipt.getPaymentType(),

                        booking.getDevoteeName(),
                        booking.getBirthStar(),
                        booking.getVazhipadu(),
                        booking.getBookingDate(),

                        booking.getQuantity(),
                        booking.getAmount()
                ));
            }
        }

        return report;
    }
}