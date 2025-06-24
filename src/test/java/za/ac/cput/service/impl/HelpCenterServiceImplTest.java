package za.ac.cput.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import za.ac.cput.domain.entity.HelpCenter;
import za.ac.cput.exception.ResourceNotFoundException;
import za.ac.cput.repository.IHelpCenterRepository;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link HelpCenterServiceImpl}.
 * Tests CRUD operations and business logic for HelpCenter entities.
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT) // Lenient for unnecessary stubbings
class HelpCenterServiceImplTest {

    @Mock
    private IHelpCenterRepository helpCenterRepository;

    @InjectMocks
    private HelpCenterServiceImpl helpCenterService;

    private HelpCenter sampleHelpCenterTopic;
    private HelpCenter topicToCreate;
    private UUID commonUuid;
    private LocalDateTime fixedTime;

    @BeforeEach
    void setUp() {
        commonUuid = UUID.randomUUID();
        fixedTime = LocalDateTime.now();

        sampleHelpCenterTopic = new HelpCenter.Builder()
                .setId(1)
                .setUuid(commonUuid)
                .setTitle("How to reset password?")
                .setContent("You can reset your password by clicking the 'Forgot Password' link.")
                .setCategory("Account Management")
                .setCreatedAt(fixedTime.minusDays(1))
                .setUpdatedAt(fixedTime.minusHours(2))
                .setDeleted(false)
                .build();

        topicToCreate = new HelpCenter.Builder()
                .setTitle("Payment issues")
                .setContent("If you have payment issues, please contact support.")
                .setCategory("Billing")
                .build();
    }

    // --- Create Tests ---
    @Test
    void create_shouldSaveAndReturnTopic_withGeneratedFields() {
        HelpCenter builtByService = new HelpCenter.Builder()
                .copy(topicToCreate)
                .setUuid(UUID.randomUUID())
                .setCreatedAt(LocalDateTime.now())
                .setUpdatedAt(LocalDateTime.now())
                .setDeleted(false)
                .build();
        HelpCenter savedEntity = new HelpCenter.Builder().copy(builtByService).setId(5).build();

        when(helpCenterRepository.save(any(HelpCenter.class))).thenReturn(savedEntity);

        HelpCenter created = helpCenterService.create(topicToCreate);

        assertNotNull(created);
        assertEquals(topicToCreate.getTitle(), created.getTitle());
        assertNotNull(created.getId());
        assertNotNull(created.getUuid());
        assertNotNull(created.getCreatedAt());
        assertFalse(created.isDeleted());
        verify(helpCenterRepository).save(any(HelpCenter.class));
    }

    @Test
    void create_shouldUseProvidedUuid_ifSet() {
        UUID specificUuid = UUID.randomUUID();
        HelpCenter topicWithUuid = new HelpCenter.Builder()
                .copy(topicToCreate)
                .setUuid(specificUuid)
                .build();

        HelpCenter savedEntity = new HelpCenter.Builder().copy(topicWithUuid).setId(6).build();
        when(helpCenterRepository.save(any(HelpCenter.class))).thenReturn(savedEntity);

        HelpCenter created = helpCenterService.create(topicWithUuid);

        assertNotNull(created);
        assertEquals(specificUuid, created.getUuid());
        verify(helpCenterRepository).save(argThat(hc -> hc.getUuid().equals(specificUuid)));
    }

    // --- Read by Integer ID Tests ---
    @Test
    void readByIntegerId_shouldReturnTopic_whenFoundAndNotDeleted() {
        when(helpCenterRepository.findByIdAndDeletedFalse(1)).thenReturn(Optional.of(sampleHelpCenterTopic));
        HelpCenter found = helpCenterService.read(Integer.valueOf(1));
        assertNotNull(found);
        assertEquals(sampleHelpCenterTopic.getUuid(), found.getUuid());
        verify(helpCenterRepository).findByIdAndDeletedFalse(1);
    }

    @Test
    void readByIntegerId_shouldReturnNull_whenNotFound() {
        when(helpCenterRepository.findByIdAndDeletedFalse(99)).thenReturn(Optional.empty());
        HelpCenter found = helpCenterService.read(Integer.valueOf(99));
        assertNull(found);
        verify(helpCenterRepository).findByIdAndDeletedFalse(99);
    }

    // --- Read by UUID Tests ---
    @Test
    void readByUuid_shouldReturnTopic_whenFoundAndNotDeleted() {
        when(helpCenterRepository.findByUuidAndDeletedFalse(commonUuid)).thenReturn(Optional.of(sampleHelpCenterTopic));
        HelpCenter found = helpCenterService.read(commonUuid);
        assertNotNull(found);
        assertEquals(sampleHelpCenterTopic.getId(), found.getId());
        verify(helpCenterRepository).findByUuidAndDeletedFalse(commonUuid);
    }

    @Test
    void readByUuid_shouldReturnNull_whenNotFound() {
        UUID nonExistentUuid = UUID.randomUUID();
        when(helpCenterRepository.findByUuidAndDeletedFalse(nonExistentUuid)).thenReturn(Optional.empty());
        HelpCenter found = helpCenterService.read(nonExistentUuid);
        assertNull(found);
        verify(helpCenterRepository).findByUuidAndDeletedFalse(nonExistentUuid);
    }

    // --- Update Tests ---
    @Test
    void update_shouldUpdateAndReturnTopic_whenFoundAndNotDeleted() {
        HelpCenter updatesToApply = new HelpCenter.Builder()
                .copy(sampleHelpCenterTopic)
                .setContent("Password reset instructions have been updated for clarity.")
                .setCategory("Security")
                .build();

        HelpCenter updatedAndSavedTopic = new HelpCenter.Builder()
                .copy(updatesToApply)
                .setUpdatedAt(LocalDateTime.now())
                .build();

        when(helpCenterRepository.existsByIdAndDeletedFalse(sampleHelpCenterTopic.getId())).thenReturn(true);
        when(helpCenterRepository.save(any(HelpCenter.class))).thenReturn(updatedAndSavedTopic);

        HelpCenter updated = helpCenterService.update(updatesToApply);

        assertNotNull(updated);
        assertEquals(updatesToApply.getContent(), updated.getContent());
        assertEquals(updatesToApply.getCategory(), updated.getCategory());
        verify(helpCenterRepository).existsByIdAndDeletedFalse(sampleHelpCenterTopic.getId());
        verify(helpCenterRepository).save(updatesToApply);
    }

    /*@Test
    void update_shouldThrowIllegalArgumentException_whenIdIsNull_ifIdWereInteger() {
        HelpCenter topicWithNullIdObject = mock(HelpCenter.class); // Use mock for precise control
        when(topicWithNullIdObject.getId()).thenReturn(null); // Stub getId to return null

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            helpCenterService.update(topicWithNullIdObject);
        });
        assertEquals("HelpCenter ID cannot be null for update.", exception.getMessage());
        verify(helpCenterRepository, never()).existsByIdAndDeletedFalse(any());
        verify(helpCenterRepository, never()).save(any(HelpCenter.class));
    }*/


    @Test
    void update_shouldThrowResourceNotFoundException_whenIdIsZeroAndNotFound() {
        HelpCenter topicWithDefaultId = new HelpCenter.Builder().setTitle("T").setContent("C").build(); // ID is 0
        when(helpCenterRepository.existsByIdAndDeletedFalse(0)).thenReturn(false);

        Exception exception = assertThrows(ResourceNotFoundException.class, () -> {
            helpCenterService.update(topicWithDefaultId);
        });
        assertTrue(exception.getMessage().contains("HelpCenter topic not found with ID: 0 for update."));
        verify(helpCenterRepository).existsByIdAndDeletedFalse(0);
        verify(helpCenterRepository, never()).save(any(HelpCenter.class));
    }

    @Test
    void update_shouldThrowResourceNotFoundException_whenTopicNotFound() {
        HelpCenter nonExistentTopic = new HelpCenter.Builder()
                .setId(99)
                .setUuid(UUID.randomUUID())
                .setTitle("Non Existent Topic")
                .build();
        when(helpCenterRepository.existsByIdAndDeletedFalse(99)).thenReturn(false);

        Exception exception = assertThrows(ResourceNotFoundException.class, () -> {
            helpCenterService.update(nonExistentTopic);
        });
        assertEquals("HelpCenter topic not found with ID: 99 for update.", exception.getMessage());
        verify(helpCenterRepository).existsByIdAndDeletedFalse(99);
        verify(helpCenterRepository, never()).save(any(HelpCenter.class));
    }

    // --- Delete Tests ---
    @Test
    void delete_shouldSoftDeleteAndReturnTrue_whenFound() {
        when(helpCenterRepository.findByIdAndDeletedFalse(sampleHelpCenterTopic.getId())).thenReturn(Optional.of(sampleHelpCenterTopic));
        doAnswer(invocation -> {
            HelpCenter arg = invocation.getArgument(0);
            assertTrue(arg.isDeleted());
            return arg;
        }).when(helpCenterRepository).save(any(HelpCenter.class));

        boolean result = helpCenterService.delete(Integer.valueOf(sampleHelpCenterTopic.getId()));

        assertTrue(result);
        verify(helpCenterRepository).findByIdAndDeletedFalse(sampleHelpCenterTopic.getId());
        verify(helpCenterRepository).save(argThat(hc -> hc.isDeleted() && hc.getId() == sampleHelpCenterTopic.getId()));
    }

    @Test
    void delete_shouldReturnFalse_whenNotFound() {
        when(helpCenterRepository.findByIdAndDeletedFalse(99)).thenReturn(Optional.empty());
        boolean result = helpCenterService.delete(Integer.valueOf(99));
        assertFalse(result);
        verify(helpCenterRepository).findByIdAndDeletedFalse(99);
        verify(helpCenterRepository, never()).save(any(HelpCenter.class));
    }

    // --- GetAll Tests ---
    @Test
    void getAll_shouldReturnListOfNonDeletedTopics() {
        HelpCenter anotherTopic = new HelpCenter.Builder().setId(2).setUuid(UUID.randomUUID()).setTitle("Another Topic").build();
        List<HelpCenter> list = List.of(sampleHelpCenterTopic, anotherTopic);
        when(helpCenterRepository.findByDeletedFalse()).thenReturn(list);

        List<HelpCenter> result = helpCenterService.getAll();

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(helpCenterRepository).findByDeletedFalse();
    }

    @Test
    void getAll_shouldReturnEmptyList_whenNoNonDeletedTopicsExist() {
        when(helpCenterRepository.findByDeletedFalse()).thenReturn(Collections.emptyList());
        List<HelpCenter> result = helpCenterService.getAll();
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(helpCenterRepository).findByDeletedFalse();
    }

    // --- findByCategory Tests ---
    @Test
    void findByCategory_shouldReturnMatchingTopics_whenCategoryExists() {
        String category = "Account Management";
        HelpCenter topicInCategory = new HelpCenter.Builder().copy(sampleHelpCenterTopic).setCategory(category).build();
        List<HelpCenter> list = List.of(topicInCategory);
        when(helpCenterRepository.findByCategoryAndDeletedFalse(category)).thenReturn(list);

        List<HelpCenter> result = helpCenterService.findByCategory(category);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(category, result.get(0).getCategory());
        verify(helpCenterRepository).findByCategoryAndDeletedFalse(category);
    }

    @Test
    void findByCategory_shouldReturnEmptyList_whenCategoryNotFound() {
        String category = "NonExistentCategory";
        when(helpCenterRepository.findByCategoryAndDeletedFalse(category)).thenReturn(Collections.emptyList());
        List<HelpCenter> result = helpCenterService.findByCategory(category);
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(helpCenterRepository).findByCategoryAndDeletedFalse(category);
    }

    @Test
    void findByCategory_shouldReturnEmptyList_whenCategoryIsNull() {
        List<HelpCenter> result = helpCenterService.findByCategory(null);
        assertTrue(result.isEmpty());
        verify(helpCenterRepository, never()).findByCategoryAndDeletedFalse(anyString());
    }

    @Test
    void findByCategory_shouldReturnEmptyList_whenCategoryIsEmpty() {
        List<HelpCenter> result = helpCenterService.findByCategory("   ");
        assertTrue(result.isEmpty());
        verify(helpCenterRepository, never()).findByCategoryAndDeletedFalse(anyString());
    }


    // --- Deprecated read(String category) Test ---
    @Test
    @SuppressWarnings("deprecation")
    void readByCategoryDeprecated_shouldDelegateToFindByCategory() {
        String category = "Billing";
        HelpCenter topicInBilling = new HelpCenter.Builder().setId(3).setCategory(category).setTitle("Billing question").build();
        List<HelpCenter> expectedList = List.of(topicInBilling);

        // Mock the behavior of the method it delegates to
        when(helpCenterRepository.findByCategoryAndDeletedFalse(category)).thenReturn(expectedList);

        List<HelpCenter> result = helpCenterService.read(category); // Call deprecated method

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(topicInBilling, result.get(0));
        verify(helpCenterRepository).findByCategoryAndDeletedFalse(category); // Verify delegation target was called
    }

    // --- Deprecated getAllByCategory(String category) Test ---
    @Test
    @SuppressWarnings("deprecation")
    void getAllByCategoryDeprecated_shouldCallSpecificRepoMethod() {
        String category = "Payments";
        HelpCenter topicInPayments = new HelpCenter.Builder().setId(4).setCategory(category).setTitle("Payment question").build();
        ArrayList<HelpCenter> expectedList = new ArrayList<>(List.of(topicInPayments));

        when(helpCenterRepository.findAllByCategoryAndDeletedFalse(category)).thenReturn(expectedList);

        ArrayList<HelpCenter> result = helpCenterService.getAllByCategory(category);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(topicInPayments, result.get(0));
        verify(helpCenterRepository).findAllByCategoryAndDeletedFalse(category);
    }

    @Test
    @SuppressWarnings("deprecation")
    void getAllByCategoryDeprecated_shouldReturnEmptyList_whenCategoryIsNull() {
        ArrayList<HelpCenter> result = helpCenterService.getAllByCategory(null);
        assertTrue(result.isEmpty());
        verify(helpCenterRepository, never()).findAllByCategoryAndDeletedFalse(anyString());
    }
}