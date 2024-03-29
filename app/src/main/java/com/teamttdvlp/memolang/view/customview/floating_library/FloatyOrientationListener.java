package com.teamttdvlp.memolang.view.customview.floating_library;

/**
 *
 * Orientation Listener Interface, which provides stubs to methods that are called when orientation changes
 * <br><br>
 * Created by ericbhatti on 12/02/15.
 *
 * @author Eric Bhatti
 *
 * @since 02 December, 2015
 *
 *
 *
 */
public interface FloatyOrientationListener {


    /**
     *
     * This method is called before the orientation change happens, you can use this to save the data of your views so you can later populate the data back in {@link #afterOrientationChange}
     *
     * @param floatingQuickAddService The floating window
     *
     */
    public void beforeOrientationChange(FloatingAddServiceManager floatingQuickAddService);

    /**
     * This method is called after the orientation change happens, you can use this to restore the data of your views that you saved in {@link #beforeOrientationChange}
     *
     * @param floatingQuickAddService The floating window
     *
     */
    public void afterOrientationChange(FloatingAddServiceManager floatingQuickAddService);

}
