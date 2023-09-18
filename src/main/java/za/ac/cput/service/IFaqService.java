package za.ac.cput.service;

import za.ac.cput.domain.Faq;

import java.util.ArrayList;

public interface IFaqService extends IService<Faq, Integer> {

    ArrayList<Faq> getAll();
}
