package com.example.temple_billing.service;

import com.example.temple_billing.dto.OfferingRequestDTO;
import com.example.temple_billing.dto.OfferingResponseDTO;
import com.example.temple_billing.entity.Offering;
import com.example.temple_billing.entity.User;
import com.example.temple_billing.repository.OfferingRepository;
import com.example.temple_billing.security.CustomUserDetails;
import com.example.temple_billing.utility.OfferingSpecification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class OfferingService {

    private static final Logger logger = LoggerFactory.getLogger(OfferingService.class);

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

        logger.info("Creating offering: {} (English: {}) by user: {}", 
                dto.getOfferingMalayalam(), dto.getOfferingEnglish(), userDetails.getUsername());

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
        logger.info("Offering created successfully with ID: {} and price: {}", offering.getId(), dto.getPrice());
        return mapToDTO(offering);
    }

    // ==============================
    // UPDATE
    // ==============================
    public OfferingResponseDTO update(
            Long id,
            OfferingRequestDTO dto,
            CustomUserDetails userDetails) {

        logger.info("Updating offering ID: {} by user: {}", id, userDetails.getUsername());

        // validateAdmin(userDetails);

        Offering offering = offeringRepository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("Offering not found - ID: {}", id);
                    return new RuntimeException("Offering not found");
                });

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
        logger.info("Offering ID: {} updated successfully", id);
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

        logger.debug("Fetching all offerings - page: {}, size: {}, sortBy: {}", page, size, sortBy);

        Pageable pageable =
                PageRequest.of(page, size, Sort.by(sortBy).descending());

        Page<OfferingResponseDTO> result = offeringRepository.findAll(pageable)
                .map(this::mapToDTO);

        logger.info("Retrieved {} offerings", result.getTotalElements());
        return result;
    }

    public Page<OfferingResponseDTO> getAllByStatus(
            int page,
            int size,
            String sortBy,
            CustomUserDetails userDetails) {

        logger.debug("Fetching active offerings - page: {}, size: {}, sortBy: {}", page, size, sortBy);

        Pageable pageable =
                PageRequest.of(page, size, Sort.by(sortBy).descending());

        Page<OfferingResponseDTO> result = offeringRepository
                .findByStatus("ACTIVE", pageable)
                .map(this::mapToDTO);

        logger.info("Retrieved {} active offerings", result.getTotalElements());
        return result;
    }

    // ==============================
    // DELETE
    // ==============================
    public void delete(Long id, CustomUserDetails userDetails) {

        logger.info("Deleting offering ID: {} by user: {}", id, userDetails.getUsername());

        // validateAdmin(userDetails);

        Offering offering = offeringRepository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("Offering not found for deletion - ID: {}", id);
                    return new RuntimeException("Offering not found");
                });

        offeringRepository.delete(offering);
        logger.info("Offering ID: {} deleted successfully", id);
    }

    public Page<OfferingResponseDTO> search(
            String offeringEnglish,
            int page,
            int size
    ) {

        logger.debug("Searching offerings - name: {}, page: {}, size: {}", offeringEnglish, page, size);

        Pageable pageable = PageRequest.of(
                page, size,
                Sort.by("createdDate").descending());

        var spec = OfferingSpecification.search(
                offeringEnglish);

        Page<Offering> offeringList =
                offeringRepository.findAll(spec, pageable);

        logger.info("Offering search returned {} results", offeringList.getTotalElements());
        return offeringList.map(this::mapToDTO);
    }

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
