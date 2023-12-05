package com.thelocalmarketplace.software.test.items;

import com.jjjwelectronics.EmptyDevice;
import com.jjjwelectronics.bag.AbstractReusableBagDispenser;
import com.jjjwelectronics.bag.ReusableBag;
import com.thelocalmarketplace.software.items.BagDispenserController;
import com.thelocalmarketplace.software.items.ItemManager;
import org.junit.Before;
import org.junit.Test;

public class ReusableBagTest {
  // stub an instance of AbstractReusableBagDispenser
  private static class ReusableBagDispenser extends AbstractReusableBagDispenser {
    @Override
    public int getQuantityRemaining() {
      return 0;
    }

    @Override
    public ReusableBag dispense() throws EmptyDevice {
      return new ReusableBag(); // will be used for verifying the dispense method
    }
  }

  private AbstractReusableBagDispenser bagDispenser;
  private BagDispenserController bagDispenserController;
  private ItemManager manager;

  @Before
  public void setUp() {
    // instantiate a new controller, passing by reference the stubbed AbstractReusableBagDispenser
    manager = new ItemManager();
    bagDispenser = new ReusableBagDispenser();
    bagDispenserController = new BagDispenserController(bagDispenser, manager);
  }

  @Test
  public void dispenseBag() {
    bagDispenserController.dispenseBag(1);

    // needs assertions
  }

  @Test (expected = EmptyDevice.class)
  public void dispenseBagWithEmptyDispenser() {
    // refactor the dispense method to throw EmptyDevice
    AbstractReusableBagDispenser emptyDispenser = new AbstractReusableBagDispenser() {
      @Override
      public ReusableBag dispense() throws EmptyDevice {
        System.out.println("Empty dispenser");
        throw new EmptyDevice("Empty dispenser");
      }

      @Override
      public int getQuantityRemaining() {
        throw new UnsupportedOperationException("Unimplemented method 'getQuantityRemaining'");
      }
    };

    // promote a new controller with the empty dispenser
    BagDispenserController emptyController = new BagDispenserController(emptyDispenser, manager);

    emptyController.dispenseBag(1); // call the controller dispense method, at this point emptyDevice exception should be thrown
  }

  
}
