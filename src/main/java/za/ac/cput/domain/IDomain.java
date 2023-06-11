package za.ac.cput.domain;

/**
 * IDomain.java
 * Interface for the Domain
 * Author: Peter Buckingham (220165289)
 * Date: 29 March 2021
 */

public interface IDomain {
    int getId();

    boolean equals(Object o);

    public int hashCode();

    String toString();
}
