package za.ac.cput.factory;
import za.ac.cput.domain.impl.Car;
import za.ac.cput.domain.impl.PriceGroup;

public interface IFactoryCar extends IFactory<Car> {
    Car createCar(int id, String make, String model, int year, String category, PriceGroup priceGroup, String licensePlate);
}
