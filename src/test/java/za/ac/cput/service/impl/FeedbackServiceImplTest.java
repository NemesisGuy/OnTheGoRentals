package za.ac.cput.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import za.ac.cput.domain.entity.Feedback;
import za.ac.cput.exception.ResourceNotFoundException;
import za.ac.cput.repository.FeedbackRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link FeedbackServiceImpl}.
 * Tests CRUD operations and business logic for Feedback entities.
 */
@ExtendWith(MockitoExtension.class)
class FeedbackServiceImplTest {

    @Mock
    private FeedbackRepository feedbackRepository; // Corrected interface

    @InjectMocks
    private FeedbackServiceImpl feedbackService;

    private Feedback sampleFeedback;
    private Feedback feedbackToCreate;
    private UUID commonUuid;
    private LocalDateTime fixedTime;

    @BeforeEach
    void setUp() {
        commonUuid = UUID.randomUUID();
        fixedTime = LocalDateTime.now();

        sampleFeedback = new Feedback.Builder()
                .setId(1)
                .setUuid(commonUuid)
                .setName("John Doe")
                .setComment("Great service!")
                .setCreatedAt(fixedTime.minusDays(1))
                .setUpdatedAt(fixedTime.minusHours(2))
                .setDeleted(false)
                .build();

        feedbackToCreate = new Feedback.Builder()
                .setName("Jane Smith")
                .setComment("Very helpful staff.")
                // UUID, createdAt, updatedAt, deleted will be handled by service/PrePersist
                .build();
    }

    // --- Create Tests ---
    @Test
    void create_shouldSaveAndReturnFeedback_withGeneratedFields() {
        Feedback builtByService = new Feedback.Builder()
                .copy(feedbackToCreate)
                .setUuid(UUID.randomUUID())
                .setCreatedAt(LocalDateTime.now())
                .setUpdatedAt(LocalDateTime.now())
                .setDeleted(false)
                .build();
        Feedback savedEntity = new Feedback.Builder().copy(builtByService).setId(5).build();

        when(feedbackRepository.save(any(Feedback.class))).thenReturn(savedEntity);

        Feedback created = feedbackService.create(feedbackToCreate);

        assertNotNull(created);
        assertEquals(feedbackToCreate.getName(), created.getName());
        assertNotNull(created.getId());
        assertNotNull(created.getUuid());
        assertNotNull(created.getCreatedAt());
        assertFalse(created.isDeleted());
        verify(feedbackRepository).save(any(Feedback.class));
    }

    @Test
    void create_shouldUseProvidedUuid_ifSet() {
        UUID specificUuid = UUID.randomUUID();
        Feedback feedbackWithUuid = new Feedback.Builder()
                .copy(feedbackToCreate)
                .setUuid(specificUuid)
                .build();

        Feedback savedEntity = new Feedback.Builder().copy(feedbackWithUuid).setId(6).build();
        when(feedbackRepository.save(any(Feedback.class))).thenReturn(savedEntity);

        Feedback created = feedbackService.create(feedbackWithUuid);

        assertNotNull(created);
        assertEquals(specificUuid, created.getUuid());
        verify(feedbackRepository).save(argThat(f -> f.getUuid().equals(specificUuid)));
    }

    // --- Read by Integer ID Tests ---
    @Test
    void readByIntegerId_shouldReturnFeedback_whenFoundAndNotDeleted() {
        when(feedbackRepository.findByIdAndDeletedFalse(1)).thenReturn(Optional.of(sampleFeedback));
        Feedback found = feedbackService.read(Integer.valueOf(1));
        assertNotNull(found);
        assertEquals(sampleFeedback.getUuid(), found.getUuid());
        verify(feedbackRepository).findByIdAndDeletedFalse(1);
    }

    @Test
    void readByIntegerId_shouldReturnNull_whenNotFound() {
        when(feedbackRepository.findByIdAndDeletedFalse(99)).thenReturn(Optional.empty());
        Feedback found = feedbackService.read(Integer.valueOf(99));
        assertNull(found);
        verify(feedbackRepository).findByIdAndDeletedFalse(99);
    }

    // --- Read by UUID Tests ---
    @Test
    void readByUuid_shouldReturnFeedback_whenFoundAndNotDeleted() {
        when(feedbackRepository.findByUuidAndDeletedFalse(commonUuid)).thenReturn(Optional.of(sampleFeedback));
        Feedback found = feedbackService.read(commonUuid);
        assertNotNull(found);
        assertEquals(sampleFeedback.getId(), found.getId());
        verify(feedbackRepository).findByUuidAndDeletedFalse(commonUuid);
    }

    @Test
    void readByUuid_shouldReturnNull_whenNotFound() {
        UUID nonExistentUuid = UUID.randomUUID();
        when(feedbackRepository.findByUuidAndDeletedFalse(nonExistentUuid)).thenReturn(Optional.empty());
        Feedback found = feedbackService.read(nonExistentUuid);
        assertNull(found);
        verify(feedbackRepository).findByUuidAndDeletedFalse(nonExistentUuid);
    }

    // --- Update Tests ---
    @Test
    void update_shouldUpdateAndReturnFeedback_whenFoundAndNotDeleted() {
        Feedback updatesToApply = new Feedback.Builder()
                .copy(sampleFeedback)
                .setComment("Actually, the service was outstanding!")
                .build();

        Feedback updatedAndSavedFeedback = new Feedback.Builder()
                .copy(updatesToApply)
                .setUpdatedAt(LocalDateTime.now())
                .build();

        when(feedbackRepository.existsByIdAndDeletedFalse(sampleFeedback.getId())).thenReturn(true);
        when(feedbackRepository.save(any(Feedback.class))).thenReturn(updatedAndSavedFeedback);

        Feedback updated = feedbackService.update(updatesToApply);

        assertNotNull(updated);
        assertEquals(updatesToApply.getComment(), updated.getComment());
        verify(feedbackRepository).existsByIdAndDeletedFalse(sampleFeedback.getId());
        verify(feedbackRepository).save(updatesToApply);
    }

   /* @Test
    void update_shouldThrowIllegalArgumentException_whenIdIsNull_ifIdWereInteger() {
        Feedback feedbackWithNullIdObject = mock(Feedback.class);
        when(feedbackWithNullIdObject.getId()).thenReturn(null);

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            feedbackService.update(feedbackWithNullIdObject);
        });
        assertEquals("Feedback ID cannot be null for update.", exception.getMessage());
        verify(feedbackRepository, never()).existsByIdAndDeletedFalse(any());
        verify(feedbackRepository, never()).save(any(Feedback.class));
    }*/

    @Test
    void update_shouldThrowResourceNotFoundException_whenIdIsZeroAndNotFound() {
        Feedback feedbackWithDefaultId = new Feedback.Builder().setName("N").setComment("C").build(); // ID is 0
        when(feedbackRepository.existsByIdAndDeletedFalse(0)).thenReturn(false);

        Exception exception = assertThrows(ResourceNotFoundException.class, () -> {
            feedbackService.update(feedbackWithDefaultId);
        });
        assertTrue(exception.getMessage().contains("Feedback not found with ID: 0 for update."));
        verify(feedbackRepository).existsByIdAndDeletedFalse(0);
        verify(feedbackRepository, never()).save(any(Feedback.class));
    }


    @Test
    void update_shouldThrowResourceNotFoundException_whenFeedbackNotFound() {
        Feedback nonExistentFeedback = new Feedback.Builder()
                .setId(99)
                .setUuid(UUID.randomUUID())
                .setName("Non Existent")
                .build();
        when(feedbackRepository.existsByIdAndDeletedFalse(99)).thenReturn(false);

        Exception exception = assertThrows(ResourceNotFoundException.class, () -> {
            feedbackService.update(nonExistentFeedback);
        });
        assertEquals("Feedback not found with ID: 99 for update.", exception.getMessage());
        verify(feedbackRepository).existsByIdAndDeletedFalse(99);
        verify(feedbackRepository, never()).save(any(Feedback.class));
    }

    // --- Delete Tests ---
    @Test
    void delete_shouldSoftDeleteAndReturnTrue_whenFound() {
        when(feedbackRepository.findByIdAndDeletedFalse(sampleFeedback.getId())).thenReturn(Optional.of(sampleFeedback));
        doAnswer(invocation -> {
            Feedback arg = invocation.getArgument(0);
            assertTrue(arg.isDeleted());
            return arg;
        }).when(feedbackRepository).save(any(Feedback.class));

        boolean result = feedbackService.delete(Integer.valueOf(sampleFeedback.getId()));

        assertTrue(result);
        verify(feedbackRepository).findByIdAndDeletedFalse(sampleFeedback.getId());
        verify(feedbackRepository).save(argThat(f -> f.isDeleted() && f.getId() == sampleFeedback.getId()));
    }

    @Test
    void delete_shouldReturnFalse_whenNotFound() {
        when(feedbackRepository.findByIdAndDeletedFalse(99)).thenReturn(Optional.empty());
        boolean result = feedbackService.delete(Integer.valueOf(99));
        assertFalse(result);
        verify(feedbackRepository).findByIdAndDeletedFalse(99);
        verify(feedbackRepository, never()).save(any(Feedback.class));
    }

    // --- GetAll Tests ---
    @Test
    void getAll_shouldReturnListOfNonDeletedFeedbacks() {
        Feedback anotherFeedback = new Feedback.Builder().setId(2).setUuid(UUID.randomUUID()).setName("Another").build();
        List<Feedback> list = List.of(sampleFeedback, anotherFeedback);
        when(feedbackRepository.findByDeletedFalse()).thenReturn(list);

        List<Feedback> result = feedbackService.getAll();

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(feedbackRepository).findByDeletedFalse();
    }

    @Test
    void getAll_shouldReturnEmptyList_whenNoNonDeletedFeedbacksExist() {
        when(feedbackRepository.findByDeletedFalse()).thenReturn(Collections.emptyList());
        List<Feedback> result = feedbackService.getAll();
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(feedbackRepository).findByDeletedFalse();
    }
}