package za.ac.cput.domain.enums;

public enum ImageType {
    CAR("cars"),
    SELFIE("selfies"),
    DOC("docs");

    private final String folder;

    ImageType(String folder) {
        this.folder = folder;
    }

    public String getFolder() {
        return folder;
    }
}
