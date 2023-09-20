package za.ac.cput.service;

import za.ac.cput.domain.HelpCenter;

import java.util.ArrayList;

public interface IHelpCenterService extends IService<HelpCenter, Integer> {

    ArrayList<HelpCenter> getAll();
}
