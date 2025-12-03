package lavamaniareloaded;

public interface IEnergyStorage {
    int _energyAmount=0;
    public void PushEnergy(IEnergyStorage destination,int EnergyAmount);
    public int GetStoredEnergyAmount();
    private boolean TryTransaction(int EnergyAmount)
    {
       return true;
    };

}
