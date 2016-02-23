package com.mfino.bsim.flashiz;

import com.mobey.android.app.SDKAppManager;
import com.mobey.android.app.SDKReporterManager;

/**
 * Simple example - How To Use Flashiz Android SDK
 * Application class - Must be implemented !
 * @author Olivier Demolliens - @ odemolliens - olivier.demolliens@flashiz.com
 * Copyright 2014 FLASHiZ - All rights reserved.
 */
public class SampleAppManager extends SDKAppManager {

	@Override
	public void onCreate() {
		super.onCreate();
	/*	TestFlight.takeOff(this, SampleAppManager.getManager().getTestFlightToken());
		TestFlight.startSession();*/
		System.out.println("Pramod");
		System.out.println("Testing");
	}
	@Override
    public void managerT() {

     setManagerT(new SDKReporterManager());

    }

	
	
}
