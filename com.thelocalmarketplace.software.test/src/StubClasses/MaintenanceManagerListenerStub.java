package StubClasses;

import com.tdc.banknote.BanknoteStorageUnit;
import com.tdc.coin.CoinStorageUnit;
import com.thelocalmarketplace.software.Session;
import com.thelocalmarketplace.software.attendant.MaintenanceManagerListener;

public class MaintenanceManagerListenerStub implements MaintenanceManagerListener {
    public boolean hardwareOpened = false;
    public boolean hardwareClosed = false;
    public boolean inkAdded = false;
    public boolean paperAdded = false;
    public boolean coinAdded = false;
    public boolean coinRemoved = false;
    public boolean banknoteAdded = false;
    public boolean banknoteRemoved = false;

    @Override
    public void notifyInkAdded(Session session) {
        inkAdded = true;
    }

    @Override
    public void notifyPaperAdded(Session session) {
        paperAdded = true;
    }

    @Override
    public void notifyCoinAdded(Session session) {
        coinAdded = true;
    }

    @Override
    public void notifyBanknoteAdded(Session session) {
        banknoteAdded = true;
    }

    @Override
    public void notifyCoinRemoved(Session session, CoinStorageUnit coinStorage) {
        coinRemoved = true;
    }

    @Override
    public void notifyBanknoteRemoved(Session session, BanknoteStorageUnit banknoteStorage) {
        banknoteRemoved = true;
    }

    @Override
    public void notifyHardwareOpened(Session session) {
        hardwareOpened = true;
    }

    @Override
    public void notifyHardwareClosed(Session session) {
        hardwareClosed = true;
    }
}
