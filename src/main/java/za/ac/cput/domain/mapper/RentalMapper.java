// File: RentalMapper.java
// Location: za.ac.cput.domain.mapper

package za.ac.cput.domain.mapper;

import za.ac.cput.domain.Rental;
import za.ac.cput.domain.dto.RentalDTO;
import za.ac.cput.domain.dto.UserDTO;
import za.ac.cput.domain.mapper.UserMapper;

public class RentalMapper {

    public static RentalDTO toDto(Rental rental) {
        if (rental == null) return null;

        return RentalDTO.builder()
                .id(rental.getId())
                .user(UserMapper.toDto(rental.getUser())) // assuming rental has User
                .car(rental.getCar())                     // or map to CarDTO if you prefer
                .issuer(rental.getIssuer())
                .receiver(rental.getReceiver())
                .fine(rental.getFine())
                .issuedDate(rental.getIssuedDate())
                .returnedDate(rental.getReturnedDate())
                .status(rental.getStatus())
                .build();
    }

    public static Rental toEntity(RentalDTO dto) {
        if (dto == null) return null;

        Rental rental = new Rental();
        rental.setId(dto.getId());
        rental.setUser(UserMapper.toEntity(dto.getUser()));
        rental.setCar(dto.getCar()); // or CarMapper.toEntity() if needed
        rental.setIssuer(dto.getIssuer());
        rental.setReceiver(dto.getReceiver());
        rental.setFine((int) dto.getFine());
        rental.setIssuedDate(dto.getIssuedDate());
        rental.setReturnedDate(dto.getReturnedDate());
        rental.setStatus(dto.getStatus());
        return rental;
    }
}
