package za.ac.cput.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import za.ac.cput.domain.entity.Faq;
import za.ac.cput.exception.ResourceNotFoundException;
import za.ac.cput.repository.IFaqRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link FaqServiceImpl}.
 * Tests CRUD operations and business logic for Faq entities.
 */
@ExtendWith(MockitoExtension.class)
class FaqServiceImplTest {

    @Mock
    private IFaqRepository faqRepository;

    @InjectMocks
    private FaqServiceImpl faqService;

    private Faq sampleFaq;
    private Faq faqToCreate;
    private UUID commonUuid;
    private LocalDateTime fixedTime;

    @BeforeEach
    void setUp() {
        commonUuid = UUID.randomUUID();
        fixedTime = LocalDateTime.now();

        sampleFaq = new Faq.Builder()
                .setId(1)
                .setUuid(commonUuid)
                .setQuestion("What is the return policy?")
                .setAnswer("Returns are accepted within 30 days with a valid receipt.")
                .setCreatedAt(fixedTime.minusDays(1))
                .setUpdatedAt(fixedTime.minusHours(2))
                .setDeleted(false)
                .build();

        faqToCreate = new Faq.Builder()
                .setQuestion("How do I track my order?")
                .setAnswer("You can track your order via the link in your confirmation email.")
                // UUID, createdAt, updatedAt, deleted will be handled by service/PrePersist
                .build();
    }

    // --- Create Tests ---
    @Test
    void create_shouldSaveAndReturnFaq_withGeneratedFields() {
        Faq builtByService = new Faq.Builder()
                .copy(faqToCreate)
                .setUuid(UUID.randomUUID()) // Service generates if null
                .setCreatedAt(LocalDateTime.now()) // These would be set by @PrePersist
                .setUpdatedAt(LocalDateTime.now())
                .setDeleted(false)
                .build();
        Faq savedEntity = new Faq.Builder().copy(builtByService).setId(5).build(); // DB generates ID

        when(faqRepository.save(any(Faq.class))).thenReturn(savedEntity);

        Faq created = faqService.create(faqToCreate);

        assertNotNull(created);
        assertEquals(faqToCreate.getQuestion(), created.getQuestion());
        assertNotNull(created.getId());
        assertNotNull(created.getUuid());
        assertNotNull(created.getCreatedAt()); // Should be set by @PrePersist or service
        assertFalse(created.isDeleted());
        verify(faqRepository).save(any(Faq.class));
    }

    @Test
    void create_shouldUseProvidedUuid_ifSet() {
        UUID specificUuid = UUID.randomUUID();
        Faq faqWithUuid = new Faq.Builder()
                .copy(faqToCreate)
                .setUuid(specificUuid)
                .build();

        Faq savedEntity = new Faq.Builder().copy(faqWithUuid).setId(6).build();
        when(faqRepository.save(any(Faq.class))).thenReturn(savedEntity);

        Faq created = faqService.create(faqWithUuid);

        assertNotNull(created);
        assertEquals(specificUuid, created.getUuid());
        verify(faqRepository).save(argThat(f -> f.getUuid().equals(specificUuid)));
    }


    // --- Read by Integer ID Tests ---
    @Test
    void readByIntegerId_shouldReturnFaq_whenFoundAndNotDeleted() {
        when(faqRepository.findByIdAndDeletedFalse(1)).thenReturn(Optional.of(sampleFaq));
        Faq found = faqService.read(Integer.valueOf(1));
        assertNotNull(found);
        assertEquals(sampleFaq.getUuid(), found.getUuid());
        verify(faqRepository).findByIdAndDeletedFalse(1);
    }

    @Test
    void readByIntegerId_shouldReturnNull_whenNotFound() {
        when(faqRepository.findByIdAndDeletedFalse(99)).thenReturn(Optional.empty());
        Faq found = faqService.read(Integer.valueOf(99));
        assertNull(found);
        verify(faqRepository).findByIdAndDeletedFalse(99);
    }

    // --- Read by UUID Tests ---
    @Test
    void readByUuid_shouldReturnFaq_whenFoundAndNotDeleted() {
        when(faqRepository.findByUuidAndDeletedFalse(commonUuid)).thenReturn(Optional.of(sampleFaq));
        Faq found = faqService.read(commonUuid);
        assertNotNull(found);
        assertEquals(sampleFaq.getId(), found.getId());
        verify(faqRepository).findByUuidAndDeletedFalse(commonUuid);
    }

    @Test
    void readByUuid_shouldReturnNull_whenNotFound() {
        UUID nonExistentUuid = UUID.randomUUID();
        when(faqRepository.findByUuidAndDeletedFalse(nonExistentUuid)).thenReturn(Optional.empty());
        Faq found = faqService.read(nonExistentUuid);
        assertNull(found);
        verify(faqRepository).findByUuidAndDeletedFalse(nonExistentUuid);
    }

    // --- Update Tests ---
    @Test
    void update_shouldUpdateAndReturnFaq_whenFoundAndNotDeleted() {
        Faq updatesToApply = new Faq.Builder()
                .copy(sampleFaq) // Has ID 1 and commonUuid
                .setAnswer("Updated Answer: Returns accepted within 60 days with proof of purchase.")
                .build(); // @PreUpdate will set updatedAt

        Faq updatedAndSavedFaq = new Faq.Builder()
                .copy(updatesToApply)
                .setUpdatedAt(LocalDateTime.now()) // Simulate @PreUpdate
                .build();

        when(faqRepository.existsByIdAndDeletedFalse(sampleFaq.getId())).thenReturn(true);
        when(faqRepository.save(any(Faq.class))).thenReturn(updatedAndSavedFaq);

        Faq updated = faqService.update(updatesToApply);

        assertNotNull(updated);
        assertEquals(updatesToApply.getAnswer(), updated.getAnswer());
        verify(faqRepository).existsByIdAndDeletedFalse(sampleFaq.getId());
        verify(faqRepository).save(updatesToApply);
    }

    /*@Test
    void update_shouldThrowIllegalArgumentException_whenIdIsNull_ifIdWereInteger() {
        Faq faqWithNullIdObject = mock(Faq.class);
        when(faqWithNullIdObject.getId()).thenReturn(null); // Stub getId() to return null

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            faqService.update(faqWithNullIdObject);
        });
        assertEquals("FAQ ID cannot be null for update.", exception.getMessage());
        verify(faqRepository, never()).existsByIdAndDeletedFalse(any());
        verify(faqRepository, never()).save(any(Faq.class));
    }*/

    @Test
    void update_shouldThrowResourceNotFoundException_whenIdIsZeroAndNotFound() {
        Faq faqWithDefaultId = new Faq.Builder().setQuestion("Q").setAnswer("A").build(); // ID will be 0
        when(faqRepository.existsByIdAndDeletedFalse(0)).thenReturn(false);

        Exception exception = assertThrows(ResourceNotFoundException.class, () -> {
            faqService.update(faqWithDefaultId);
        });
        assertTrue(exception.getMessage().contains("FAQ not found with ID: 0 for update."));
        verify(faqRepository).existsByIdAndDeletedFalse(0);
        verify(faqRepository, never()).save(any(Faq.class));
    }


    @Test
    void update_shouldThrowResourceNotFoundException_whenFaqNotFound() {
        Faq nonExistentFaq = new Faq.Builder()
                .setId(99)
                .setUuid(UUID.randomUUID())
                .setQuestion("Non Existent Q")
                .build();
        when(faqRepository.existsByIdAndDeletedFalse(99)).thenReturn(false);

        Exception exception = assertThrows(ResourceNotFoundException.class, () -> {
            faqService.update(nonExistentFaq);
        });
        assertEquals("FAQ not found with ID: 99 for update.", exception.getMessage());
        verify(faqRepository).existsByIdAndDeletedFalse(99);
        verify(faqRepository, never()).save(any(Faq.class));
    }

    // --- Delete Tests ---
    @Test
    void delete_shouldSoftDeleteAndReturnTrue_whenFound() {
        when(faqRepository.findByIdAndDeletedFalse(sampleFaq.getId())).thenReturn(Optional.of(sampleFaq));
        doAnswer(invocation -> {
            Faq arg = invocation.getArgument(0);
            assertTrue(arg.isDeleted());
            return arg;
        }).when(faqRepository).save(any(Faq.class));

        boolean result = faqService.delete(Integer.valueOf(sampleFaq.getId()));

        assertTrue(result);
        verify(faqRepository).findByIdAndDeletedFalse(sampleFaq.getId());
        verify(faqRepository).save(argThat(f -> f.isDeleted() && f.getId() == sampleFaq.getId()));
    }

    @Test
    void delete_shouldReturnFalse_whenNotFound() {
        when(faqRepository.findByIdAndDeletedFalse(99)).thenReturn(Optional.empty());
        boolean result = faqService.delete(Integer.valueOf(99));
        assertFalse(result);
        verify(faqRepository).findByIdAndDeletedFalse(99);
        verify(faqRepository, never()).save(any(Faq.class));
    }

    // --- GetAll Tests ---
    @Test
    void getAll_shouldReturnListOfNonDeletedFaqs() {
        Faq anotherFaq = new Faq.Builder().setId(2).setUuid(UUID.randomUUID()).setQuestion("Another Q").build();
        List<Faq> list = List.of(sampleFaq, anotherFaq);
        when(faqRepository.findByDeletedFalse()).thenReturn(list);

        List<Faq> result = faqService.getAll();

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(faqRepository).findByDeletedFalse();
    }

    @Test
    void getAll_shouldReturnEmptyList_whenNoNonDeletedFaqsExist() {
        when(faqRepository.findByDeletedFalse()).thenReturn(Collections.emptyList());
        List<Faq> result = faqService.getAll();
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(faqRepository).findByDeletedFalse();
    }
}