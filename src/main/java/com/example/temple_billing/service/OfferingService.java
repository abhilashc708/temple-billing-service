package com.example.temple_billing.service;

import com.example.temple_billing.dto.OfferingRequestDTO;
import com.example.temple_billing.dto.OfferingResponseDTO;
import com.example.temple_billing.entity.Offering;
import com.example.temple_billing.entity.User;
import com.example.temple_billing.repository.OfferingRepository;
import com.example.temple_billing.security.CustomUserDetails;
import com.example.temple_billing.utility.OfferingSpecification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class OfferingService {

    private final OfferingRepository offeringRepository;

    public OfferingService(OfferingRepository offeringRepository) {
        this.offeringRepository = offeringRepository;
    }

    // ==============================
    // CREATE
    // ==============================
    public OfferingResponseDTO create(
            OfferingRequestDTO dto,
            CustomUserDetails userDetails) {

        //validateAdmin(userDetails);

        Offering offering = Offering.builder()
                .offeringEnglish(dto.getOfferingEnglish())
                .offeringMalayalam(dto.getOfferingMalayalam())
                .offeringType(dto.getOfferingType())
                .offeringGod(dto.getOfferingGod())
                .price(dto.getPrice())
                .noOfPooja(dto.getNoOfPooja())
                .status(dto.getStatus())
                .remarks(dto.getRemarks())
                .createdDate(LocalDateTime.now())
                .createdBy(User.builder()
                        .id(userDetails.getUserId())
                        .build())
                .build();

        offeringRepository.save(offering);

        return mapToDTO(offering);
    }

    // ==============================
    // UPDATE
    // ==============================
    public OfferingResponseDTO update(
            Long id,
            OfferingRequestDTO dto,
            CustomUserDetails userDetails) {

        // validateAdmin(userDetails);

        Offering offering = offeringRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Offering not found"));

        offering.setOfferingEnglish(dto.getOfferingEnglish());
        offering.setOfferingMalayalam(dto.getOfferingMalayalam());
        offering.setOfferingType(dto.getOfferingType());
        offering.setOfferingGod(dto.getOfferingGod());
        offering.setPrice(dto.getPrice());
        offering.setNoOfPooja(dto.getNoOfPooja());
        offering.setStatus(dto.getStatus());
        offering.setRemarks(dto.getRemarks());

        offering.setModifiedDate(LocalDateTime.now());
        offering.setModifiedBy(User.builder()
                .id(userDetails.getUserId())
                .build());

        offeringRepository.save(offering);

        return mapToDTO(offering);
    }

    // ==============================
    // GET ALL (Pagination + Sorting)
    // ==============================
    public Page<OfferingResponseDTO> getAll(
            int page,
            int size,
            String sortBy,
            CustomUserDetails userDetails) {

        Pageable pageable =
                PageRequest.of(page, size, Sort.by(sortBy).descending());

        return offeringRepository.findAll(pageable)
                .map(this::mapToDTO);
    }

    public Page<OfferingResponseDTO> getAllByStatus(
            int page,
            int size,
            String sortBy,
            CustomUserDetails userDetails) {

        Pageable pageable =
                PageRequest.of(page, size, Sort.by(sortBy).descending());

        return offeringRepository
                .findByStatus("ACTIVE", pageable)
                .map(this::mapToDTO);
    }

    // ==============================
    // DELETE
    // ==============================
    public void delete(Long id, CustomUserDetails userDetails) {

        // validateAdmin(userDetails);

        Offering offering = offeringRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Offering not found"));

        offeringRepository.delete(offering);
    }

    public Page<OfferingResponseDTO> search(
            String offeringEnglish,
            int page,
            int size
    ) {

        Pageable pageable = PageRequest.of(
                page, size,
                Sort.by("createdDate").descending());

        var spec = OfferingSpecification.search(
                offeringEnglish);

        Page<Offering> offeringList =
                offeringRepository.findAll(spec, pageable);

        return offeringList.map(this::mapToDTO);
    }


    // ==============================
    // VALIDATE ADMIN
    // ==============================
//    private void validateAdmin(CustomUserDetails userDetails) {
//        if (!userDetails.getRole().equals("ADMIN")) {
//            throw new RuntimeException("Access denied. Admin only.");
//        }
//    }

    // ==============================
    // MAP TO DTO
    // ==============================
    private OfferingResponseDTO mapToDTO(Offering offering) {

        return OfferingResponseDTO.builder()
                .id(offering.getId())
                .offeringEnglish(offering.getOfferingEnglish())
                .offeringMalayalam(offering.getOfferingMalayalam())
                .offeringType(offering.getOfferingType())
                .offeringGod(offering.getOfferingGod())
                .price(offering.getPrice())
                .noOfPooja(offering.getNoOfPooja())
                .status(offering.getStatus())
                .remarks(offering.getRemarks())
                .createdBy(offering.getCreatedBy().getUsername())
                .createdDate(offering.getCreatedDate())
                .modifiedBy(
                        offering.getModifiedBy() != null ?
                                offering.getModifiedBy().getUsername() : null)
                .modifiedDate(offering.getModifiedDate())
                .build();
    }
}
