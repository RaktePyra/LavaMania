package lavamaniareloaded;

public interface IEnergyStorage {
    public void PushEnergy(IEnergyStorage destination,int EnergyAmount);
    public void ReceiveEnergy(int energy_amount);
    public int GetStoredEnergyAmount();
    public boolean _isStorageOnly =false;
}
