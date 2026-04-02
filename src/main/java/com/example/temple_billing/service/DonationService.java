package com.example.temple_billing.service;

import com.example.temple_billing.dto.DonationRequestDTO;
import com.example.temple_billing.dto.DonationResponseDTO;
import com.example.temple_billing.dto.DonationSearchRequest;
import com.example.temple_billing.entity.Donation;
import com.example.temple_billing.entity.PaymentStatus;
import com.example.temple_billing.entity.User;
import com.example.temple_billing.repository.DonationRepository;
import com.example.temple_billing.security.CustomUserDetails;
import com.example.temple_billing.utility.DonationSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DonationService {

    private final DonationRepository donationRepository;

    // =========================
    // CREATE (ADMIN + USER)
    // =========================
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    public DonationResponseDTO create(
            DonationRequestDTO dto,
            CustomUserDetails userDetails) {

        Donation donation = Donation.builder()
                .donationDate(dto.getDonationDate())
                .paymentType(dto.getPaymentType())
                .paymentStatus(PaymentStatus.valueOf(dto.getPaymentStatus()))
                .receiptBookNo(dto.getReceiptBookNo())
                .receiptNo(dto.getReceiptNo())
                .devoteeName(dto.getDevoteeName())
                .address(dto.getAddress())
                .phoneNo(dto.getPhoneNo())
                .amount(dto.getAmount())
                .donateFor(dto.getDonateFor())
                .remarks(dto.getRemarks())
                .createdDate(LocalDateTime.now())
                .createdBy(User.builder()
                        .id(userDetails.getUserId())
                        .build())
                .build();

        donationRepository.save(donation);
        return mapToDTO(donation);
    }

    // =========================
    // GET ALL
    // ADMIN → all
    // USER → own only
    // =========================
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    public Page<DonationResponseDTO> getAll(
            int page,
            int size,
            String sortBy,
            CustomUserDetails userDetails) {

        Pageable pageable =
                PageRequest.of(page, size, Sort.by(sortBy).descending());

        Page<Donation> donations;

        if (userDetails.getAuthorities()
                .stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {

            donations = donationRepository.findAll(pageable);

        } else {

            donations = donationRepository
                    .findByCreatedBy_Id(userDetails.getUserId(), pageable);
        }

        return donations.map(this::mapToDTO);
    }

    // =========================
    // UPDATE (Ownership check)
    // =========================
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    public DonationResponseDTO update(
            Long id,
            DonationRequestDTO dto,
            CustomUserDetails userDetails) {

        Donation donation = donationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Donation not found"));

        validateOwnership(donation, userDetails);

        donation.setDonationDate(dto.getDonationDate());
        donation.setPaymentType(dto.getPaymentType());
        donation.setPaymentStatus(PaymentStatus.valueOf(dto.getPaymentStatus()));
        donation.setReceiptBookNo(dto.getReceiptBookNo());
        donation.setReceiptNo(dto.getReceiptNo());
        donation.setDevoteeName(dto.getDevoteeName());
        donation.setAddress(dto.getAddress());
        donation.setPhoneNo(dto.getPhoneNo());
        donation.setAmount(dto.getAmount());
        donation.setDonateFor(dto.getDonateFor());
        donation.setRemarks(dto.getRemarks());
        donation.setModifiedDate(LocalDateTime.now());
        donation.setModifiedBy(User.builder()
                .id(userDetails.getUserId())
                .build());

        donationRepository.save(donation);

        return mapToDTO(donation);
    }

    // =========================
    // DELETE
    // =========================
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    public void delete(Long id, CustomUserDetails userDetails) {

        Donation donation = donationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Donation not found"));

        validateOwnership(donation, userDetails);

        donationRepository.delete(donation);
    }

    private void validateOwnership(Donation donation,
                                   CustomUserDetails userDetails) {

        boolean isAdmin = userDetails.getAuthorities()
                .stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        if (!isAdmin &&
                !donation.getCreatedBy().getId()
                        .equals(userDetails.getUserId())) {

            throw new RuntimeException("Access Denied");
        }
    }

    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    public Page<DonationResponseDTO> search(
            String devoteeName,
            String paymentStatus,
            String paymentType,
            LocalDate donationFrom,
            LocalDate donationTo,
            String donateFor,
            String receiptBookNo,
            int page,
            int size
    ) {

        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by("createdDate").descending());

        Specification<Donation> spec =
                DonationSpecification.search(
                        devoteeName,
                        paymentStatus,
                        paymentType,
                        donationFrom,
                        donationTo,
                        donateFor,
                        receiptBookNo);

        Page<Donation> result =
                donationRepository.findAll(spec, pageable);

        return result.map(this::mapToDTO);
    }


    public List<DonationResponseDTO> donationReport(DonationSearchRequest request) {

        Specification<Donation> spec = DonationSpecification.search(request);

        List<Donation> donationSearchList =
                donationRepository.findAll(spec, Sort.by("createdDate").descending());

        return donationSearchList.stream()
                .map(this::mapToDTO)
                .toList();
    }

    private DonationResponseDTO mapToDTO(Donation donation) {

        return DonationResponseDTO.builder()
                .id(donation.getId())
                .donationDate(donation.getDonationDate())
                .paymentType(donation.getPaymentType())
                .paymentStatus(String.valueOf(donation.getPaymentStatus()))
                .receiptBookNo(donation.getReceiptBookNo())
                .receiptNo(donation.getReceiptNo())
                .devoteeName(donation.getDevoteeName())
                .address(donation.getAddress())
                .phoneNo(donation.getPhoneNo())
                .amount(donation.getAmount())
                .donateFor(donation.getDonateFor())
                .remarks(donation.getRemarks())
                .createdBy(donation.getCreatedBy().getUsername())
                .createdDate(donation.getCreatedDate())
                .modifiedBy(
                        donation.getModifiedBy() != null ?
                                donation.getModifiedBy().getUsername() : null)
                .modifiedDate(donation.getModifiedDate())
                .build();
    }
}
