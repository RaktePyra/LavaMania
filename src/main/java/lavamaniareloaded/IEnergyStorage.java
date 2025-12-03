package lavamaniareloaded;

public interface IEnergyStorage {
    public void PushEnergy(IEnergyStorage destination,int EnergyAmount);
    public int GetStoredEnergyAmount();
}
