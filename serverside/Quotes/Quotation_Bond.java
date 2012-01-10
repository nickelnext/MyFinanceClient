package Quotes;

import Utils.UtilFuncs;

public class Quotation_Bond extends Quotation{

	
	protected String valuta;
	protected String mercato;
	protected String faseMercato;
	protected float prezzoUltimoContratto;
	protected float variazionePercentuale;
	protected float variazioneAssoluta;
	protected String dataUltimoContratto;
	protected int volumeUltimo;
	protected int volumeAcquisto;
	protected int volumeVendita;
	protected float prezzoAcquisto;
	protected float prezzoVendita;
	protected int volumeTotale;
	protected float maxOggi;
	protected float maxAnno;
	protected float minOggi;
	protected float minAnno;
	protected String dataMinAnno;
	protected String dataMaxAnno;
	protected float aperturaChiusuraPrecedente;
	protected String scadenza;
	protected String dataStaccoCedola;
	protected float cedola;
	protected int lottoMinimo;
	


	public Quotation_Bond() {
		super();
		super.setType(QuotationType.BOND);
	}
	
	public String getValuta() {
		return valuta;
	}
	public void setValuta(String valuta) {
		this.valuta = valuta;
	}
	public String getMercato() {
		return mercato;
	}
	public void setMercato(String mercato) {
		this.mercato = mercato;
	}
	public String getFaseMercato() {
		return faseMercato;
	}
	public void setFaseMercato(String faseMercato) {
		this.faseMercato = faseMercato;
	}
	public float getPrezzoUltimoContratto() {
		return prezzoUltimoContratto;
	}
	public void setPrezzoUltimoContratto(String string) {
		this.prezzoUltimoContratto = UtilFuncs.repFloat(string, getCountry());
	}
	

	public float getVariazionePercentuale() {
		return variazionePercentuale;
	}
	public void setVariazionePercentuale(String string) {
		this.variazionePercentuale = UtilFuncs.repFloat(string, getCountry());
	}
	public float getVariazioneAssoluta() {
		return variazioneAssoluta;
	}
	public void setVariazioneAssoluta(String string) {
		this.variazioneAssoluta = UtilFuncs.repFloat(string, getCountry());
	}
	public String getDataUltimoContratto() {
		return dataUltimoContratto;
	}
	public void setDataUltimoContratto(String string) {
		this.dataUltimoContratto = string;
	}
	public int getVolumeUltimo() {
		return volumeUltimo;
	}
	public void setVolumeUltimo(String string) {
		this.volumeUltimo = UtilFuncs.repInteger(string);
	}
	public int getVolumeAcquisto() {
		return volumeAcquisto;
	}
	public void setVolumeAcquisto(String string) {
		this.volumeAcquisto = UtilFuncs.repInteger(string);
	}
	public int getVolumeVendita() {
		return volumeVendita;
	}
	public void setVolumeVendita(String string) {
		this.volumeVendita = UtilFuncs.repInteger(string);
	}
	public float getPrezzoAcquisto() {
		return prezzoAcquisto;
	}
	public void setPrezzoAcquisto(String string) {
		this.prezzoAcquisto = UtilFuncs.repFloat(string, getCountry());
	}
	public float getPrezzoVendita() {
		return prezzoVendita;
	}
	public void setPrezzoVendita(String string) {
		this.prezzoVendita = UtilFuncs.repFloat(string, getCountry());
	}
	public int getVolumeTotale() {
		return volumeTotale;
	}
	public void setVolumeTotale(String string) {
		this.volumeTotale = UtilFuncs.repInteger(string);
	}
	

	public float getMaxOggi() {
		return maxOggi;
	}
	public void setMaxOggi(String string) {
		this.maxOggi = UtilFuncs.repFloat(string, getCountry());
	}
	public float getMaxAnno() {
		return maxAnno;
	}
	public void setMaxAnno(String string) {
		this.maxAnno = UtilFuncs.repFloat(string, getCountry());
	}
	public float getMinOggi() {
		return minOggi;
	}
	public void setMinOggi(String string) {
		this.minOggi = UtilFuncs.repFloat(string, getCountry());
	}
	public float getMinAnno() {
		return minAnno;
	}
	public void setMinAnno(String string) {
		this.minAnno = UtilFuncs.repFloat(string, getCountry());
	}
	public String getDataMinAnno() {
		return dataMinAnno;
	}
	public void setDataMinAnno(String string) {
		this.dataMinAnno = string;
	}
	public String getDataMaxAnno() {
		return dataMaxAnno;
	}
	public void setDataMaxAnno(String string) {
		this.dataMaxAnno = string;
	}
	public float getAperturaChiusuraPrecedente() {
		return aperturaChiusuraPrecedente;
	}
	public void setAperturaChiusuraPrecedente(String string) {
		this.aperturaChiusuraPrecedente = UtilFuncs.repFloat(string, getCountry());
	}
	public String getScadenza() {
		return scadenza;
	}
	public void setScadenza(String string) {
		this.scadenza = string;
	}
	public String getDataStaccoCedola() {
		return dataStaccoCedola;
	}
	public void setDataStaccoCedola(String string) {
		this.dataStaccoCedola = string;
	}

	public float getCedola() {
		return cedola;
	}
	public void setCedola(String string) {
		this.cedola = UtilFuncs.repFloat(string, getCountry());
	}
	public int getLottoMinimo() {
		return lottoMinimo;
	}
	public void setLottoMinimo(String string) {
		this.lottoMinimo = UtilFuncs.repInteger(string);
	}

}
