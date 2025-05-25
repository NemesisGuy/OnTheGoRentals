package za.ac.cput.service;

import za.ac.cput.domain.entity.HelpCenter;

import java.util.List;
import java.util.UUID;

public interface IHelpCenterService extends IService<HelpCenter, Integer> {

    List<HelpCenter> getAll();

    List<HelpCenter> findByCategory(String category);
    List<HelpCenter> read(String category);
    HelpCenter read(UUID uuid);

}
