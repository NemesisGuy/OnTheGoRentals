package za.ac.cput.factory;
import za.ac.cput.domain.Car;

public interface IFactoryCar extends IFactory<Car> {
    Car createCar();
    Car createCar(int id, String make, String model, int year, String category, String licensePlate);

}
