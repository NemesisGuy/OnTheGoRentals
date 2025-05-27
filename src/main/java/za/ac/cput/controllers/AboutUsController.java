package za.ac.cput.controllers;
/**
 * AboutUsController.java
 * Controller class for About Us page
 * Author: Cwenga Dlova (214310671)
 * Date: 24/09/2023
 */


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import za.ac.cput.domain.entity.AboutUs;
import za.ac.cput.domain.dto.response.AboutUsResponseDTO;
import za.ac.cput.domain.mapper.AboutUsMapper;
import za.ac.cput.service.impl.AboutUsServiceImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * AboutUsController.java
 * This is the controller for the AboutUs class
 * Author: Peter Buckingham (220165289)
 * Date: 05 April 2023
 * 220165289
 */
@RestController
@RequestMapping("/api/v1/about-us")
public class AboutUsController {

    private final AboutUsServiceImpl service;

    @Autowired
    public AboutUsController(AboutUsServiceImpl service) {
        this.service = service;
    }

    @GetMapping("/{id}")
    public ResponseEntity<AboutUsResponseDTO> read(@PathVariable int id) {
        AboutUs readAbout = this.service.read(id);
        if (readAbout == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(AboutUsMapper.toDto(readAbout));
    }

    @GetMapping
    public ResponseEntity<List<AboutUsResponseDTO>> getAll() {
        List<AboutUs> allAboutUs = service.getAll();
        List<AboutUsResponseDTO> aboutUsResponseDTOList = new ArrayList<>();
        for (AboutUs aboutUs : allAboutUs) {
            aboutUsResponseDTOList.add(AboutUsMapper.toDto(aboutUs));
        }

        return ResponseEntity.ok((aboutUsResponseDTOList));
    }

    @GetMapping("/latest")
    public ResponseEntity<AboutUsResponseDTO> getLatest() {
        List<AboutUs> allAboutUs = service.getAll();
        Optional<AboutUsResponseDTO> newestAboutUs = Optional.ofNullable(AboutUsMapper.toDto(allAboutUs.get(allAboutUs.size()-1)));
        //get the latest about us


        return newestAboutUs.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
