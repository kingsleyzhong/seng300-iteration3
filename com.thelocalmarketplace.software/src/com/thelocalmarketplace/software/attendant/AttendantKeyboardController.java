package com.thelocalmarketplace.software.attendant;

import com.jjjwelectronics.DisabledDevice;
import com.thelocalmarketplace.hardware.AttendantStation;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

/**
 * <p>A class that will read keystrokes and execute them on the com.jjjwelectronics.* keyboard class. Implements
 * java.awt.event.KeyListener. Does this need to be a class or can it be another inner listener?</p>
 * <p></p>
 * <p>Project Iteration 3 Group 1:</p>
 * <p></p>
 * <p> Derek Atabayev 				: 30177060 </p>
 * <p> Enioluwafe Balogun 			: 30174298 </p>
 * <p> Subeg Chahal 				: 30196531 </p>
 * <p> Jun Heo 						: 30173430 </p>
 * <p> Emily Kiddle 				: 30122331 </p>
 * <p> Anthony Kostal-Vazquez 		: 30048301 </p>
 * <p> Jessica Li 					: 30180801 </p>
 * <p> Sua Lim 						: 30177039 </p>
 * <p> Savitur Maharaj 				: 30152888 </p>
 * <p> Nick McCamis 				: 30192610 </p>
 * <p> Ethan McCorquodale 			: 30125353 </p>
 * <p> Katelan Ng 					: 30144672 </p>
 * <p> Arcleah Pascual 				: 30056034 </p>
 * <p> Dvij Raval 					: 30024340 </p>
 * <p> Chloe Robitaille 			: 30022887 </p>
 * <p> Danissa Sandykbayeva 		: 30200531 </p>
 * <p> Emily Stein 					: 30149842 </p>
 * <p> Thi My Tuyen Tran 			: 30193980 </p>
 * <p> Aoi Ueki 					: 30179305 </p>
 * <p> Ethan Woo 					: 30172855 </p>
 * <p> Kingsley Zhong 				: 30197260 </p>
 */
public class AttendantKeyboardController implements KeyListener{

    private AttendantStation as;
    public AttendantKeyboardController(AttendantStation as){
        this.as = as;
    }

    /**
     * Invoked when a key has been typed.
     * See the class description for {@link KeyEvent} for a definition of
     * a key typed event.
     *
     * @param e the event to be processed
     */
    @Override
    public void keyTyped(KeyEvent e) {
        // May not be used
    }

    /**
     * Invoked when a key has been pressed.
     * See the class description for {@link KeyEvent} for a definition of
     * a key pressed event.
     *
     * @param e the event to be processed
     */
    @Override
    public void keyPressed(KeyEvent e) {
        // The goal is to have this listener event press a key on the keyboard facade
        if (as.keyboard.WINDOWS_QWERTY.contains(e.getKeyText(e.getKeyCode()))) {
            try {
                as.keyboard.getKey(e.getKeyText(e.getKeyCode())).press();
            } catch (DisabledDevice ex) {
                throw new RuntimeException(ex);
            }
        } else if (e.getKeyText(e.getKeyCode()).equals("Shift")){
            try {
                as.keyboard.getKey("Shift (Right)").press();
            } catch (DisabledDevice ex) {
                throw new RuntimeException(ex);
            }
        } else if (e.getKeyText(e.getKeyCode()).equals("Escape")) {
            try {
                as.keyboard.getKey("FnLock Esc").press();
            } catch (DisabledDevice ex) {
                throw new RuntimeException(ex);
            }
        }
        // else ignore
    }

    /**
     * Invoked when a key has been released.
     * See the class description for {@link KeyEvent} for a definition of
     * a key released event.
     *
     * @param e the event to be processed
     */
    @Override
    public void keyReleased(KeyEvent e) {
        // The goal is to have this listener event release a key on the keyboard facade
        if (as.keyboard.WINDOWS_QWERTY.contains(e.getKeyText(e.getKeyCode()))) {
            try {
                as.keyboard.getKey(e.getKeyText(e.getKeyCode())).release();
            } catch (DisabledDevice ex) {
                throw new RuntimeException(ex);
            }
        } else if (e.getKeyText(e.getKeyCode()).equals("Shift")){
            try {
                as.keyboard.getKey("Shift (Right)").release();
            } catch (DisabledDevice ex) {
                throw new RuntimeException(ex);
            }
        } else if (e.getKeyText(e.getKeyCode()).equals("Escape")) {
            try {
                as.keyboard.getKey("FnLock Esc").release();
            } catch (DisabledDevice ex) {
                throw new RuntimeException(ex);
            }
        }
        // else ignore
    }
}
