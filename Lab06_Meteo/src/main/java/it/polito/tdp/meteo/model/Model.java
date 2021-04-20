package it.polito.tdp.meteo.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import it.polito.tdp.meteo.DAO.MeteoDAO;

public class Model {
	
	MeteoDAO meteo;
	int min;
	boolean controllo;
	List<Citta>sequenzaFin;
	int giorniCons;
	
	private final static int COST = 100;
	private final static int NUMERO_GIORNI_CITTA_CONSECUTIVI_MIN = 3;
	private final static int NUMERO_GIORNI_CITTA_MAX = 6;
	private final static int NUMERO_GIORNI_TOTALI = 15;

	public Model() {
		meteo=new MeteoDAO();
		controllo=true;
		sequenzaFin=new LinkedList<Citta>();
	}

	// of course you can change the String output with what you think works best
	public String getUmiditaMedia(int mese) {
		String result=new String();
		Map<String,Double>rilevamenti=this.meteo.getRilevamentiPerLocalitaMese(mese);
		for(String s: rilevamenti.keySet()) {
			result+=s+": "+rilevamenti.get(s)+"\n";
		}
		return result;
	}
	
	// of course you can change the String output with what you think works best
	public String trovaSequenza(int mese) {
		List<Citta> citta=this.meteo.getAllCitta();
		Map<Citta,List<Rilevamento>> rilevamenti=new HashMap<Citta,List<Rilevamento>>();
		List<Citta>sequenza=new LinkedList<Citta>();
		Map<Citta,Integer>giorniCitta=new HashMap<Citta,Integer>();
		int tot=0;
		for(Citta c: citta) {
			 rilevamenti.put(c,new LinkedList<Rilevamento>(this.meteo.getAllRilevamentiLocalitaMese(mese, c.getNome())));	
			 giorniCitta.put(c,0);
		}
		calcolaSequenza(sequenza,tot,rilevamenti,citta,giorniCitta);
		String s=new String();
		int i=1;
		for(Citta c:sequenzaFin) {
		s+=	i+") "+c+"\n";
		i++;
		}
		s+="COSTO TOTALE: "+min+" euro";
		return s;
	}

	private void calcolaSequenza(List<Citta> sequenza, int tot,Map<Citta,List<Rilevamento>> rilevamenti,List<Citta>citta,Map<Citta,Integer>giorniCitta) {
		// TODO Auto-generated method stub
		
		//CONTROLLO GIORNI TOTALI
		if(sequenza.size() == NUMERO_GIORNI_TOTALI){
			for(Citta c:giorniCitta.keySet()) {
				if(giorniCitta.get(c)>NUMERO_GIORNI_CITTA_MAX)
					return ;
			}
			
			//CONTROLLO ALMENO UN GIORNO IN CITTA'
			for(Citta c:giorniCitta.keySet()) {
				if(giorniCitta.get(c)==0)
					return ;
			}
			
			//CONTROLLO GIORNI CONSECUTIVI
			for(int i=0;i<sequenza.size();i++) {
				if((i==sequenza.size()-1) && !(sequenza.get(i).equals(sequenza.get(i-1))) && !(sequenza.get(i).equals(sequenza.get(i-2)))) {
				return ;
				}
				if(i!=0) {
				if(sequenza.get(i).equals(sequenza.get(i-1))) {
					giorniCons++;
				}
				else {
					if(giorniCons<NUMERO_GIORNI_CITTA_CONSECUTIVI_MIN) {
						return ;
					}
					giorniCons=1;
				}
				}else {
					giorniCons=1;
				}
			}
			if(controllo) {
			min=tot;
			controllo=false;
			}else {
				if(tot<min) {
					min=tot;
					sequenzaFin.clear();
					for(Citta c:sequenza) {
						sequenzaFin.add(c);
					}
				}
			}
			
			
		}
		else {
			//CONTROLLO GIORNI TOTALI
			for(Citta c:giorniCitta.keySet()) {
				if(giorniCitta.get(c)>NUMERO_GIORNI_CITTA_MAX)
					return ;
			}
			
		for(Citta c: citta) {
				sequenza.add(c);
				giorniCitta.put(c,giorniCitta.get(c)+1);
				tot+=rilevamenti.get(c).get(sequenza.size()-1).getUmidita();
				if(sequenza.size()>=2) {
				if(!(sequenza.get(sequenza.size()-1).equals(sequenza.get(sequenza.size()-2)))){
				tot+=COST;
				}
				}
				calcolaSequenza(sequenza,tot,rilevamenti,citta,giorniCitta);
				
				//BACKTRACKING
				
				if(sequenza.size()!=0) {
				if(sequenza.size()>=2) {
					if(!(sequenza.get(sequenza.size()-1).equals(sequenza.get(sequenza.size()-2)))){
						tot-=COST;
						}
					}
				tot-=rilevamenti.get(c).get(sequenza.size()-1).getUmidita();
				sequenza.remove(sequenza.size()-1);
				giorniCitta.put(c,giorniCitta.get(c)-1);
				
				
				}
		}
			
			}
		}
	}
	
	


