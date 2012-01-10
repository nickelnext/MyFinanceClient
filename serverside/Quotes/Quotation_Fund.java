package Quotes;

import Utils.UtilFuncs;

public class Quotation_Fund extends Quotation{
	
	

	
	protected String nomeGestore;
	protected String categoriaAssociati;
	protected String benchmarkDichiarato;
	protected float ultimoPrezzo;
	protected String dataUltimoPrezzo;
	protected float prezzoPrecedente;
	protected String valuta;
	protected float variazionePercentuale;
	protected float variazioneAssoluta;
	protected float performance1Mese;
	protected float performance3Mesi;
	protected float performance1Anno;
	protected float performance3Anni;
	
	
	public Quotation_Fund() {
		super();
		this.setType(QuotationType.FUND);
	}
	
	public String getNomeGestore() {
		return nomeGestore;
	}
	public void setNomeGestore(String nomeGestore) {
		this.nomeGestore = nomeGestore;
	}
	public String getCategoriaAssociati() {
		return categoriaAssociati;
	}
	public void setCategoriaAssociati(String categoriaAssociati) {
		this.categoriaAssociati = categoriaAssociati;
	}
	public String getBenchmarkDichiarato() {
		return benchmarkDichiarato;
	}
	public void setBenchmarkDichiarato(String benchmarkDichiarato) {
		this.benchmarkDichiarato = benchmarkDichiarato;
	}
	public float getUltimoPrezzo() {
		return ultimoPrezzo;
	}
	public void setUltimoPrezzo(String ultimoPrezzo) {
		this.ultimoPrezzo = UtilFuncs.repFloat(ultimoPrezzo, getCountry());
	}
	public String getDataUltimoPrezzo() {
		return dataUltimoPrezzo;
	}
	public void setDataUltimoPrezzo(String dataUltimoPrezzo) {
		this.dataUltimoPrezzo = dataUltimoPrezzo;
	}
	public float getPrezzoPrecedente() {
		return prezzoPrecedente;
	}
	public void setPrezzoPrecedente(String prezzoPrecedente) {
		this.prezzoPrecedente = UtilFuncs.repFloat(prezzoPrecedente, getCountry());
	}
	public String getValuta() {
		return valuta;
	}
	public void setValuta(String valuta) {
		this.valuta = valuta;
	}
	public float getVariazionePercentuale() {
		return variazionePercentuale;
	}
	public void setVariazionePercentuale(String variazionePercentuale) {
		this.variazionePercentuale = UtilFuncs.repFloat(variazionePercentuale, getCountry());
	}
	public float getVariazioneAssoluta() {
		return variazioneAssoluta;
	}
	public void setVariazioneAssoluta(String variazioneAssoluta) {
		this.variazioneAssoluta = UtilFuncs.repFloat(variazioneAssoluta, getCountry());
	}
	public float getPerformance1Mese() {
		return performance1Mese;
	}
	public void setPerformance1Mese(String performance1Mese) {
		this.performance1Mese = UtilFuncs.repFloat(performance1Mese, getCountry());
	}
	public float getPerformance3Mesi() {
		return performance3Mesi;
	}
	public void setPerformance3Mesi(String performance3Mesi) {
		this.performance3Mesi = UtilFuncs.repFloat(performance3Mesi, getCountry());
	}
	public float getPerformance1Anno() {
		return performance1Anno;
	}
	public void setPerformance1Anno(String performance1Anno) {
		this.performance1Anno = UtilFuncs.repFloat(performance1Anno, getCountry());
	}
	public float getPerformance3Anni() {
		return performance3Anni;
	}
	public void setPerformance3Anni(String performance3Anni) {
		this.performance3Anni = UtilFuncs.repFloat(performance3Anni, getCountry());
	}
	
	


}




