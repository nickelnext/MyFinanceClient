package Quotes;

import Utils.UtilFuncs;

public class Quotation_Share extends Quotation{

	
	protected int lottoMinimo;
	protected String faseMercato;
	protected float prezzoUltimoContratto;
	protected float variazionePercentuale;
	protected float variazioneAssoluta;
	protected String dataOraUltimoAcquisto;
	protected float prezzoAcquisto;
	protected float prezzoVendita;
	protected int quantitaUltimo;
	protected int quantitaAcquisto;
	protected int quantitaVendita;
	protected int quantitaTotale;
	protected float maxOggi;
	protected float minOggi;
	protected float maxAnno;
	protected float minAnno;
	protected String dataMaxAnno;
	protected String dataMinAnno;
	protected float chiusuraPrecedente;
	protected String valuta;
	
	public String getValuta() {
		return valuta;
	}


	public void setValuta(String valuta) {
		this.valuta = valuta;
	}


	public Quotation_Share() {
		super();
		super.setType(QuotationType.SHARE);
	}
	

	public int getLottoMinimo() {
		return lottoMinimo;
	}
	public void setLottoMinimo(String string) {
		this.lottoMinimo = UtilFuncs.repInteger(string);
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
	public String getDataOraUltimoAcquisto() {
		return dataOraUltimoAcquisto;
	}


	public void setDataOraUltimoAcquisto(String dataOraUltimoAcquisto) {
		this.dataOraUltimoAcquisto = dataOraUltimoAcquisto;
	}


	public String getDataMaxAnno() {
		return dataMaxAnno;
	}


	public void setDataMaxAnno(String dataMaxAnno) {
		this.dataMaxAnno = dataMaxAnno;
	}


	public String getDataMinAnno() {
		return dataMinAnno;
	}


	public void setDataMinAnno(String dataMinAnno) {
		this.dataMinAnno = dataMinAnno;
	}


	public void setLottoMinimo(int lottoMinimo) {
		this.lottoMinimo = lottoMinimo;
	}


	public void setPrezzoUltimoContratto(float prezzoUltimoContratto) {
		this.prezzoUltimoContratto = prezzoUltimoContratto;
	}


	public void setVariazionePercentuale(float variazionePercentuale) {
		this.variazionePercentuale = variazionePercentuale;
	}


	public void setVariazioneAssoluta(float variazioneAssoluta) {
		this.variazioneAssoluta = variazioneAssoluta;
	}


	public void setPrezzoAcquisto(float prezzoAcquisto) {
		this.prezzoAcquisto = prezzoAcquisto;
	}


	public void setPrezzoVendita(float prezzoVendita) {
		this.prezzoVendita = prezzoVendita;
	}


	public void setQuantitaUltimo(int quantitaUltimo) {
		this.quantitaUltimo = quantitaUltimo;
	}


	public void setQuantitaAcquisto(int quantitaAcquisto) {
		this.quantitaAcquisto = quantitaAcquisto;
	}


	public void setQuantitaVendita(int quantitaVendita) {
		this.quantitaVendita = quantitaVendita;
	}


	public void setQuantitaTotale(int quantitaTotale) {
		this.quantitaTotale = quantitaTotale;
	}


	public void setMaxOggi(float maxOggi) {
		this.maxOggi = maxOggi;
	}


	public void setMinOggi(float minOggi) {
		this.minOggi = minOggi;
	}


	public void setMaxAnno(float maxAnno) {
		this.maxAnno = maxAnno;
	}


	public void setMinAnno(float minAnno) {
		this.minAnno = minAnno;
	}


	public void setChiusuraPrecedente(float chiusuraPrecedente) {
		this.chiusuraPrecedente = chiusuraPrecedente;
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
	public int getQuantitaUltimo() {
		return quantitaUltimo;
	}
	public void setQuantitaUltimo(String string) {
		this.quantitaUltimo = UtilFuncs.repInteger(string);
	}
	public int getQuantitaAcquisto() {
		return quantitaAcquisto;
	}
	public void setQuantitaAcquisto(String string) {
		this.quantitaAcquisto = UtilFuncs.repInteger(string);
	}
	public int getQuantitaVendita() {
		return quantitaVendita;
	}
	public void setQuantitaVendita(String string) {
		this.quantitaVendita = UtilFuncs.repInteger(string);
	}
	public int getQuantitaTotale() {
		return quantitaTotale;
	}
	public void setQuantitaTotale(String string) {
		this.quantitaTotale = UtilFuncs.repInteger(string);
	}
	public float getMaxOggi() {
		return maxOggi;
	}
	public void setMaxOggi(String string) {
		this.maxOggi = UtilFuncs.repFloat(string, getCountry());
	}
	public float getMinOggi() {
		return minOggi;
	}
	public void setMinOggi(String string) {
		this.minOggi = UtilFuncs.repFloat(string, getCountry());
	}
	public float getMaxAnno() {
		return maxAnno;
	}
	public void setMaxAnno(String maxAnno) {
		this.maxAnno = UtilFuncs.repFloat(maxAnno, getCountry());
	}
	public float getMinAnno() {
		return minAnno;
	}
	public void setMinAnno(String minAnno) {
		this.minAnno = UtilFuncs.repFloat(minAnno, getCountry());
	}
	public float getChiusuraPrecedente() {
		return chiusuraPrecedente;
	}
	public void setChiusuraPrecedente(String string) {
		this.chiusuraPrecedente = UtilFuncs.repFloat(string, getCountry());
	}

}
