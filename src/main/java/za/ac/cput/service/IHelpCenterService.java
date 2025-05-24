package za.ac.cput.service;

import za.ac.cput.domain.HelpCenter;

import java.util.ArrayList;
import java.util.List;

public interface IHelpCenterService extends IService<HelpCenter, Integer> {

    List<HelpCenter> getAll();
}
