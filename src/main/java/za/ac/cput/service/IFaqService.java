package za.ac.cput.service;

import za.ac.cput.domain.Faq;

import java.util.ArrayList;
import java.util.List;

public interface IFaqService extends IService<Faq, Integer> {

    List<Faq> getAll();
}
