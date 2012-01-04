package it.util;

import java.util.ArrayList;
import java.util.TimerTask;
import it.util.UpdateUtils;

public class UpdateTimeTask extends TimerTask{
	
	private ArrayList<String> portfolii = new ArrayList<String>();
	private UpdateUtils up;
	
	public void run(){
		for(String s : portfolii){
			up.updatePortfolio(s);
		}
	}
	
	public void add(String portfolioName){
		portfolii.add(portfolioName);
	}

}

/**
da inserire nel codice:

UpdateTimeTask upTask = new UpdateTimeTask();
Timer timer = new Timer();

//quando apro un portafolgio e quindi devo ricordarmi di aggiornarlo
upTask.add(portfolioName);

//faccio partire il timer
timer.schedule(new UpdateTask(), 100, 200);

//cancello il timer(e quindi l'update automatico)
timer.cancel();
**/