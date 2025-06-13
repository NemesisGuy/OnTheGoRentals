# Architectural Update: Abstract File Storage System
## =========================================================
## 1. Overview

This document outlines the recent architectural refactoring of the application's file storage mechanism. The system has been upgraded from a rigid, local filesystem-based implementation to a flexible, abstract, and profile-driven architecture.

The primary goal of this change is to decouple the application's business logic from the underlying storage technology. This makes the system more scalable, maintainable, testable, and production-ready, allowing us to easily switch between different storage backends (e.g., local disk for development, MinIO for production) without changing any application code.

## 2. Key Architectural Changes

The monolithic `FileStorageService` has been broken down into specialized components following the **Separation of Concerns** principle.

### 2.1. New Service Interfaces

Two new interfaces now define the contracts for storage operations:

* **`IFileStorageService` (The Core "Hands")**: A lean interface for fundamental, low-level file operations. Its methods are generic enough to be implemented by any storage provider.
    - `save(MultipartFile file, String directory)`
    - `loadAsResource(String key)`
    - `delete(String key)`
    - `fileExists(String key)`
    - `getUrl(String key)`

* **`IStorageManagementService` (The High-Level "Brains")**: An interface for business logic, data integrity checks, and statistical reporting. This service *uses* an instance of `IFileStorageService` to interact with the storage system.
    - `findOrphanedFiles()`
    - `findBrokenImageLinks()`
    - `getFileSystemStats()`
    - `getStorageUsagePerFolder()`

### 2.2. Service Implementations & Spring Profiles

We have two primary implementations of the core `IFileStorageService`, controlled by Spring Profiles:

* **`LocalFileStorageService`**:
    - Implements `IFileStorageService` using `java.nio.file.Path` to store files on the local disk.
    - Activated by the Spring Profile: `storage-local`.

* **`MinioStorageService`**:
    - Implements `IFileStorageService` using the official MinIO Java SDK to store files in an S3-compatible object storage bucket.
    - Activated by the Spring Profile: `storage-minio`.

### 2.3. Mapper Refactoring

To support dynamic URL generation (especially pre-signed URLs from MinIO), mappers that produce DTOs with image URLs can no longer be purely static.

* The `UserMapper` (and subsequently `BookingMapper`, `RentalMapper`, etc.) now requires an `IFileStorageService` instance to be passed to its `toDto` and `toDtoList` methods.
* This is achieved by injecting `IFileStorageService` into the controllers and passing it down to the static mapper methods at runtime. This maintains the stateless nature of the mappers while providing them with the necessary context.

**Example: The New Mapper Call Pattern**

```java
// Before:
UserResponseDTO dto = UserMapper.toDto(user);

// After (in a Controller):
@Autowired
private IFileStorageService fileStorageService;
// ...
UserResponseDTO dto = UserMapper.toDto(user, fileStorageService);
```
3. How to Configure the Storage System
   Switching between storage backends is now controlled entirely by the active Spring Profile.

application.properties
Edit the spring.profiles.active property in your main application.properties file to select the desired storage system. 
Only one profile should be active at a time.


```
# ===================================================================
# STORAGE CONFIGURATION (THE MASTER SWITCH)
# ===================================================================

# --- OPTION 1: Local File Storage (for easy development) ---
# Files will be saved in a local 'uploads' directory by default.
spring.profiles.active=storage-local
app.storage.base-dir=uploads


# --- OPTION 2: MinIO Object Storage (for production or staging) ---
# To use this, comment out the line above and uncomment the lines below.
#
# spring.profiles.active=storage-minio
#
# # MinIO Server Connection Details
# minio.url=http://127.0.0.1:9002
# minio.access.key=minioadmin
# minio.secret.key=minioadmin
# minio.bucket.name=car-rental-app

```

4. New Feature: Multiple Car Image Uploads
   The admin functionality has been enhanced to allow uploading multiple images for a single car in one request.

Endpoint: POST /api/v1/admin/cars/{carUuid}/images

Request: multipart/form-data with one or more files under the key images.

Implementation:

The core logic has been moved from the controller to a new transactional service method: ICarService.addImagesToCar().

The @Transactional annotation ensures that either all images are saved and associated with the car in the database, or the entire operation is rolled back if any part fails. This guarantees data consistency.

5. Development Environment: MinIO with Docker
   To facilitate development with an object storage backend, a Docker Compose file is provided to run a local MinIO server.

docker-compose.yml
This file sets up a MinIO server with a persistent data volume.

```
version: '3.8'

services:
  minio:
    image: minio/minio:latest
    container_name: minio-server
    command: server /data --console-address ":9001"
    ports:
      - "9002:9000"
      - "9003:9001"
    environment:
      MINIO_ROOT_USER: minioadmin
      MINIO_ROOT_PASSWORD: minioadmin
    volumes:
      - minio-data:/data
    healthcheck:
      test: ["CMD", "mc", "ready", "local"]
      interval: 30s
      timeout: 20s
      retries: 3
    restart: unless-stopped

volumes:
  minio-data:
    driver: local

```

**Running MinIO**
Using Docker Compose CLI:

Navigate to the directory containing the docker-compose.yml file.

Run the command: docker-compose up -d

**Using Portainer:**

Navigate to "Stacks" and click "+ Add stack".

Give it a name (e.g., minio-stack).

Paste the content of the docker-compose.yml file into the web editor.

Click "Deploy the stack".

**Accessing MinIO**
API Endpoint (for your application.properties): http://<your-server-ip>:9002

Web Console: http://<your-server-ip>:9003

Username: minioadmin

Password: minioadmin

Important: After starting MinIO for the first time, you must manually create the bucket (e.g., car-rental-app) using the web console before the Spring Boot application can use it.