package com.thelocalmarketplace.software.test.items;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import com.jjjwelectronics.EmptyDevice;
import com.jjjwelectronics.bag.AbstractReusableBagDispenser;
import com.jjjwelectronics.bag.ReusableBag;
import com.thelocalmarketplace.software.items.BagDispenserController;

public class ReusableBagTest {
  // stub an instance of AbstractReusableBagDispenser
  private static class ReusableBagDispenser extends AbstractReusableBagDispenser {
    @Override
    public ReusableBag dispense() throws EmptyDevice {
      return new ReusableBag(); // will be used for verifying the dispense method
    }
  }

  private AbstractReusableBagDispenser bagDispenser;
  private BagDispenserController bagDispenserController;

  @Before
  public void setUp() {
    // instantiate a new controller, passing by reference the stubbed AbstractReusableBagDispenser
    bagDispenser = new ReusableBagDispenser();
    bagDispenserController = new BagDispenserController(bagDispenser);
  }

  @Test
  public void testDispenseBag() {
    bagDispenserController.dispenseBag();

    // needs assertions
  }

  @Test (expected = EmptyDevice.class)
  public void testDispenseBagWithEmptyDispenser() {
    // refactor the dispense method to throw EmptyDevice
    AbstractReusableBagDispenser emptyDispenser = new AbstractReusableBagDispenser() {
      @Override
      public ReusableBag dispense() throws EmptyDevice {
        throw new EmptyDevice();
      }
    }

    // promote a new controller with the empty dispenser
    BagDispenserController emptyController = new BagDispenserController(emptyDispenser);

    emptyController.dispenseBag(); // call the controller dispense method, at this point emptyDevice exception should be thrown
  }

  
}
